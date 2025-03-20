package codehows.com.daehoint.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * <b>MailConfig 클래스</b><br>
 * 애플리케이션에서 이메일 전송을 설정 및 관리하는 구성 클래스입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - Naver SMTP 서버를 사용하여 이메일 전송을 설정.<br>
 * - SMTP 인증 및 SSL 설정을 포함한 JavaMailSender 구성.<br><br>
 *
 * <b>핵심 메서드:</b><br>
 * 1. `getJavaMailSender()`:<br>
 *    - JavaMailSender 객체를 생성 및 초기화.<br>
 *    - SMTP 서버 정보, 사용자 ID/비밀번호, 기본 인코딩 설정.<br>
 *    - 추가 프로퍼티를 설정하여 이메일 전송에 필요한 세부사항 정의.<br>
 * 2. `getProperties()`:<br>
 *    - JavaMailSender에 필요한 SMTP 프로퍼티 설정.<br>
 *    - 주요 프로퍼티:
 *      - `mail.transport.protocol`: 전송 프로토콜(SMTP).<br>
 *      - `mail.smtp.auth`: SMTP 인증 활성화.<br>
 *      - `mail.smtp.starttls.enable`: StartTLS 활성화.<br>
 *      - `mail.smtp.ssl.trust`: SSL 인증 서버 지정.<br>
 *      - `mail.smtp.ssl.enable`: SSL 활성화.<br>
 *
 * <b>주요 어노테이션:</b><br>
 * - `@Configuration`: 스프링 설정 클래스임을 나타냄.<br>
 * - `@Value`: 애플리케이션 설정 파일에서 값을 주입받기 위한 어노테이션.<br>
 * - `@Bean`: 스프링 컨테이너에 Bean 객체를 등록.<br><br>
 *
 * <b>사용 예:</b><br>
 * - 이메일 전송이 필요한 서비스나 컴포넌트에서 `JavaMailSender`를 주입받아 사용.<br>
 * - 예: 회원 가입 인증 이메일, 비밀번호 재설정 이메일 등.<br><br>
 *
 * <b>설정 정보:</b><br>
 * - SMTP 서버: `smtp.naver.com`<br>
 * - 포트: `465` (SSL 사용)<br>
 * - 사용자 계정: `spring.mail.username` 및 `spring.mail.password`<br>
 */
@Configuration
public class MailConfig {
    @Value("${spring.mail.username}")
    private String id;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender getJavaMailSender() {

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.naver.com");
        javaMailSender.setPort(465);
        javaMailSender.setUsername(id);
        javaMailSender.setPassword(password);
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setJavaMailProperties(getProperties());
        return javaMailSender;
    }
    private Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp"); // 프로토콜 설정
        properties.setProperty("mail.smtp.auth", "true"); // smtp 인증
        properties.setProperty("mail.smtp.starttls.enable", "true"); // smtp strattles 사용
        properties.setProperty("mail.debug", "true"); // 디버그 사용
        properties.setProperty("mail.smtp.ssl.trust", "smtp.naver.com"); // ssl 인증 서버 (smtp 서버명)
        properties.setProperty("mail.smtp.ssl.enable", "true"); // ssl 사용
        return properties;
    }


}
