package com.echo.echo.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class FindUserDto {
    @Email
    @NotBlank
    private String email;

    @Getter
    public static class Password {
        @Email
        @NotBlank
        private String email;
        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~!@#$%^&*]).{8,}$",
                message = "비밀번호는 대소문자, 숫자, 특수문자(~!@#$%^&*)를 포함하여 8자 이상이어야 합니다."
        )
        private String newPassword;
    }
}
