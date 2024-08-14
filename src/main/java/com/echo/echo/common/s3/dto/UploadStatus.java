package com.echo.echo.common.s3.dto;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.services.s3.model.CompletedPart;

import java.util.HashMap;
import java.util.Map;

@Getter
public class UploadStatus {

    private final String fileKey;
    private final String contentType;
    @Setter
    private String uploadId;
    private int partCounter;
    @Setter
    private int buffered;

    private final Map<Integer, CompletedPart> completedParts = new HashMap<>();

    public UploadStatus(String contentType, String fileKey) {
        this.contentType = contentType;
        this.fileKey = fileKey;
        this.buffered = 0;
    }

    public void addBuffered(int buffered) {
        this.buffered += buffered;
    }

    public int getAddedPartCounter() {
        return ++this.partCounter;
    }
}
