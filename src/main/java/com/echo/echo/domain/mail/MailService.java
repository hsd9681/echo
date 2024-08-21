package com.echo.echo.domain.mail;

import com.echo.echo.config.MailConfig;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MailService {

    private final MailConfig mailConfig;
    private final JavaMailSender javaMailSender;

    public Mono<Boolean> sendMail(Mail mail) {
        return Mono.fromCallable(() -> {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

            mimeMessageHelper.addTo(mail.getTo());
            mimeMessageHelper.setFrom(new InternetAddress(mailConfig.getUsername(), "Echo"));
            mimeMessageHelper.setSubject("Echo " + mail.getSubject());

            mimeMessageHelper.setText(mail.getBody(), true);
//            이미지 삽입
//            mimeMessageHelper.addInline("logo", new ClassPathResource("static/images/logo_mail.png"));

            javaMailSender.send(message);
            return true;
        }).doOnError(error -> System.out.println("error = " + error)).then(Mono.just(false));
    }

}
