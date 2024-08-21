package com.echo.echo.common.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class AWSS3Object {

    String key;
    Instant lastModified;
    String eTag;
    Long size;

}
