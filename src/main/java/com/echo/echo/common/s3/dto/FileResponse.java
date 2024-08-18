package com.echo.echo.common.s3.dto;

import lombok.Getter;

@Getter
public class FileResponse {

    String name;
    String uploadId;
    String path;
    String type;
    String eTag;

}
