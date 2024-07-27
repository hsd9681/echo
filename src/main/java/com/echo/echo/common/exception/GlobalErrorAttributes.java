package com.echo.echo.common.exception;

import com.echo.echo.common.exception.codes.ErrorCode;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> originMap = super.getErrorAttributes(request, options);
        Map<String, Object> map = new HashMap<>();

        map.put("status", ErrorCode.FAIL.getStatus().value());

        Throwable throwable = getError(request);
        if (throwable instanceof CustomException) {
            CustomException ex = (CustomException) getError(request);
            map.put("message", ex.getBaseCode().getCommonReason().getMsg());
            map.put("status", ex.getBaseCode().getCommonReason().getStatus().value());
        }

        if (throwable instanceof SecurityException) {
            map.put("message", ErrorCode.UNAUTHORIZED.getMsg());
            map.put("status", ErrorCode.UNAUTHORIZED.getStatus().value());
        }

        map.put("path", originMap.get("path"));
        map.put("timestamp", originMap.get("timestamp"));

        return map;
    }
}

