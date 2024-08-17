package com.echo.echo.domain.text.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TextRequest {

    @NotBlank(message = "공백은 전송할 수 없습니다.")
    private String contents;
}
