package com.echo.echo.common.s3.util;

import com.echo.echo.common.exception.CustomException;
import com.echo.echo.common.s3.error.S3ErrorCode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.SdkResponse;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@UtilityClass
public class FileUtils {

    private final String[] contentTypes = {
            "image/png",
            "image/jpg",
            "image/jpeg",
            "image/bmp",
            "image/gif",
            "image/ief",
            "image/pipeg",
            "image/svg+xml",
            "image/tiff",
            "image/webp",
            "image/ico"
    };

    private boolean isValidType(final FilePart filePart) {
        log.info(String.valueOf(filePart.headers().getContentType()));
        return isSupportedContentType(Objects.requireNonNull(filePart.headers().getContentType()).toString());
    }

    private boolean isEmpty(final FilePart filePart) {
        return ObjectUtils.isEmpty(filePart.filename())
                && ObjectUtils.isEmpty(filePart.headers().getContentType());
    }

    private boolean isSupportedContentType(final String contentType) {
        return Arrays.asList(contentTypes).contains(contentType);
    }

    public ByteBuffer dataBufferToByteBuffer(List<DataBuffer> buffers) {
        log.info("Creating ByteBuffer from {} chunks", buffers.size());

        int partSize = 0;
        for(DataBuffer b : buffers) {
            partSize += b.readableByteCount();
        }

        ByteBuffer partData = ByteBuffer.allocate(partSize);
        buffers.forEach(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            partData.put(bytes);
        });
        partData.rewind();

        log.info("PartData: capacity={}", partData.capacity());
        return partData;
    }

    public void checkSdkResponse(SdkResponse sdkResponse) {
        if (sdkResponse.sdkHttpResponse() == null || !sdkResponse.sdkHttpResponse().isSuccessful()) {
            throw new CustomException(S3ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    public void filePartValidator(FilePart filePart) {
        if (isEmpty(filePart)){
            throw new CustomException(S3ErrorCode.FILE_IS_NULL);
        }
        if (!isValidType(filePart)){
            throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
        }
    }

    public Mono<Void> checkFileSize(FilePart filePart, Long maxFileSize) {
        return filePart.content()
                .map(dataBuffer -> {
                    long size = dataBuffer.readableByteCount();
                    DataBufferUtils.release(dataBuffer);
                    return size;
                })
                .reduce(Long::sum)
                .flatMap(totalSize -> {
                    if (totalSize > maxFileSize) {
                        return Mono.error(new CustomException(S3ErrorCode.FILE_SIZE_OVER));
                    }
                    return Mono.empty();
                });
    }
}
