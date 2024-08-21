package com.echo.echo.common.s3.service;

import com.echo.echo.common.s3.dto.AWSS3Object;
import com.echo.echo.common.s3.dto.UploadStatus;
import com.echo.echo.common.s3.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.BytesWrapper;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Service {

    private final S3AsyncClient s3Client;
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    @Value("${aws.s3.upload.part.min.size}")
    private Long uploadPartMinSize;
    @Value("${aws.s3.upload.max.size}")
    private Long uploadMaxSize;

    public Mono<Void> deleteObject(String objectKeyUri) {
        String objectKey = FileUtils.extractKeyFromUrl(objectKeyUri);
        return Mono.just(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build())
                .map(s3Client::deleteObject)
                .flatMap(Mono::fromFuture)
                .then();
    }

    public Flux<AWSS3Object> getObjects() {
        return Flux.from(s3Client.listObjectsV2Paginator(ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .build()))
                .flatMap(response -> Flux.fromIterable(response.contents()))
                .map(s3Object -> new AWSS3Object(s3Object.key(), s3Object.lastModified(),s3Object.eTag(), s3Object.size()));
    }

    public Mono<byte[]> getByteObject(String objectKeyUri) {
        String objectKey = FileUtils.extractKeyFromUrl(objectKeyUri);
        return Mono.just(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build())
                .map(it -> s3Client.getObject(it, AsyncResponseTransformer.toBytes()))
                .flatMap(Mono::fromFuture)
                .map(BytesWrapper::asByteArray);
    }

    public Mono<String> upload(FilePart filePart) {

        return FileUtils.checkFileSize(filePart, uploadMaxSize)
                .then(Mono.defer(() -> {
                    // 파일 확장자 획득
                    String extension = StringUtils.getFilenameExtension(filePart.filename());
                    // 업로드 파일 이름 획득
                    String filename = String.format("%s-%s.%s", System.currentTimeMillis(), UUID.randomUUID(), extension);
                    // 메타 데이터 설정 : 파일 이름을 사용한 Key : Value
                    Map<String, String> metadata = Map.of("filename", filename);
                    // get media type
                    MediaType mediaType = Objects.requireNonNullElse(filePart.headers().getContentType(), MediaType.APPLICATION_OCTET_STREAM);

                    // S3에 멀티파트 업로드를 시작하는 비동기 요청 생성
                    CompletableFuture<CreateMultipartUploadResponse> s3AsyncClientMultipartUpload = s3Client
                            .createMultipartUpload(CreateMultipartUploadRequest.builder()
                                    .contentType(mediaType.toString())
                                    .key(filename)
                                    .metadata(metadata)
                                    .bucket(bucketName)
                                    .build());

                    // 업로드 상태 추적 객체 생성
                    UploadStatus uploadStatus = new UploadStatus(Objects.requireNonNull(filePart.headers().getContentType()).toString(), filename);

                    // S3 멀티파트 업로드 요청 결과 처리
                    return Mono.fromFuture(s3AsyncClientMultipartUpload)
                            .flatMapMany(response -> {
                                // 49번째 줄의 요청의 결과를 확인하고 업로드 ID 설정
                                FileUtils.checkSdkResponse(response);
                                uploadStatus.setUploadId(response.uploadId());
                                // 파일의 내용을 Flux<DataBuffer>로 파일을 스트림 처리하여 반환
                                return filePart.content();
                            })
                            // 파일 내용을 버퍼링하면서, 특정 크기 이상이 되면 나누어 처리
                            .bufferUntil(dataBuffer -> {
                                // 버퍼된 데이터의 크리글 업로드 상태에 추가
                                uploadStatus.addBuffered(dataBuffer.readableByteCount());
                                // 버퍼된 데이터가 멀티파트 최소 크기 이상이면 true 반환(버퍼 플러시)
                                if (uploadStatus.getBuffered() >= uploadPartMinSize) {
                                    // 버퍼 초기화
                                    uploadStatus.setBuffered(0);
                                    return true;
                                }
                                return false;
                            })
                            // DataBuffer를 ByteBuffer로 변환
                            .map(FileUtils::dataBufferToByteBuffer)
                            // 변환된 ByteBuffer를 사용하여 S3에 멀티파트 업로드의 일부를 업로드
                            .flatMap(byteBuffer -> uploadPartObject(uploadStatus, byteBuffer))
                            // 백프레셔를 처리하기 위해 버퍼 사용
                            .onBackpressureBuffer()
                            // 업로드가 완료된 부분들을 업로드 상태에 추가
                            .reduce(uploadStatus, (status, completedPart) -> {
                                (status).getCompletedParts().put(completedPart.partNumber(), completedPart);
                                return status;
                            })
                            // 모든 파트가 업로드 되면 멀티파트 업로드를 완료
                            .flatMap(uploadStatus1 -> completeUpload(uploadStatus))
                            // 업로드 완료 후 응답 데이터 생성 후 반환
                            .map(response -> {
                                FileUtils.checkSdkResponse(response);
                                return response.location();
                            });
                }));
    }

    private Mono<CompletedPart> uploadPartObject(UploadStatus uploadStatus, ByteBuffer buffer) {
        final int partNumber = uploadStatus.getAddedPartCounter();

        CompletableFuture<UploadPartResponse> uploadPartResponseCompletableFuture = s3Client.uploadPart(UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(uploadStatus.getFileKey())
                        .partNumber(partNumber)
                        .uploadId(uploadStatus.getUploadId())
                        .contentLength((long) buffer.capacity())
                        .build(),
                AsyncRequestBody.fromPublisher(Mono.just(buffer)));

        return Mono
                .fromFuture(uploadPartResponseCompletableFuture)
                .map(uploadPartResult -> {
                    FileUtils.checkSdkResponse(uploadPartResult);
                    return CompletedPart.builder()
                            .eTag(uploadPartResult.eTag())
                            .partNumber(partNumber)
                            .build();
                });
    }

    private Mono<CompleteMultipartUploadResponse> completeUpload(UploadStatus uploadStatus) {
        CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
                .parts(uploadStatus.getCompletedParts().values())
                .build();

        return Mono.fromFuture(s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .uploadId(uploadStatus.getUploadId())
                .multipartUpload(multipartUpload)
                .key(uploadStatus.getFileKey())
                .build()));
    }

}
