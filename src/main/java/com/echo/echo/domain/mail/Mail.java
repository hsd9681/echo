package com.echo.echo.domain.mail;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Mail {

    private final String to;
    private final String subject;
    private final String body;

    @Builder
    public Mail(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public static String createSignupMailBody(String verificationCode) {
        return """
                <!DOCTYPE html>
                <body>
                    인증번호는 ${code}입니다.
                    10분 내로 입력해주세요.
                </body>
                </html>""".replace("${code}", verificationCode);
    }

}
