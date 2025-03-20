package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.MailDTO;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * MailService 클래스
 *
 * <p>이 클래스는 이메일 발송과 관련된 기능을 제공하는 서비스입니다.
 * Spring의 JavaMailSender를 사용하여 이메일을 생성하고 발송하며, 로그 파일을 읽고
 * 정리하여 이메일 본문에 포함할 내용을 생성합니다.</p>
 *
 * 주요 기능:
 * <ul>
 *   <li>DB 반영 결과를 이메일로 발송</li>
 *   <li>에러 발생 시 이메일로 알림 발송</li>
 *   <li>로그 파일을 읽고 분석하여 이메일 본문 생성</li>
 * </ul>
 *
 * 주요 메서드:
 * <ul>
 *   <li>{@code sendMail()}: 성공적으로 반영된 DB 데이터를 이메일로 발송</li>
 *   <li>{@code sendMail(String error)}: 에러 메시지를 이메일로 발송</li>
 *   <li>{@code parseLog()}: 로그 파일을 읽고 내용 반환</li>
 *   <li>{@code sortLog(String log)}: 로그 데이터를 분석하고 요약된 결과를 반환</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String id;

    public void sendMail() {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MailDTO mailDTO = MailDTO.builder()
                .to("gupo941020@daehoint.co.kr")
                .message(sortLog(parseLog()))
                .subject(LocalDate.now() + "DB 반영 결과")
                .build();
        try {
            mimeMessage.setFrom();
            mimeMessage.addRecipients(Message.RecipientType.TO, mailDTO.getTo());
//            mimeMessage.addRecipients(Message.RecipientType.TO, "shlee@daehoint.co.kr");
            mimeMessage.setSubject(mailDTO.getSubject());
            mimeMessage.setText(mailDTO.getMessage());
            mimeMessage.setFrom(id);
            emailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMail(String error) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MailDTO mailDTO = MailDTO.builder()
                .to("gupo941020@daehoint.co.kr")
                .message(error)
                .subject(LocalDate.now() + "데이터 갱신 실패")
                .build();
        try {
            mimeMessage.setFrom();
            mimeMessage.addRecipients(Message.RecipientType.TO, mailDTO.getTo());
            mimeMessage.setSubject(mailDTO.getSubject());
            mimeMessage.setText(mailDTO.getMessage());
            mimeMessage.setFrom(id);
            emailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String parseLog() {
        StringBuilder sb = new StringBuilder();
        LocalDate today = LocalDate.now().minusDays(1L);
        // 현재 날짜를 기반으로 파일 이름을 생성
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = today.format(sdf);
        System.out.println(currentDate);
        String logFileName = "/home/logs/mergeERPMES-" + currentDate + ".log"; // 파일 경로 + 이름

        // 파일 읽기
        try {
            File file = new File(logFileName);  // 해당 날짜의 로그 파일을 찾음
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            // 파일 내용 읽어서 StringBuilder에 추가
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");  // 각 라인 뒤에 줄바꿈 추가
            }

            // 스트림 닫기
            fileReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error reading log file: " + e.getMessage());
        }
        return sb.toString();  // 로그 내용 반환
    }

    public String sortLog(String log) {
        String[] loStrings = log.split("\n");
        StringBuilder sb = new StringBuilder();
        int successHoliday = 0;
        int successPrice = 0;
        int successProcess = 0;
        int failedProcess = 0;
        int successFinal = 0;
        int failedFinal = 0;
        for (String s : loStrings) {
            if (s.contains("공휴일 갱신완료")) {
                successHoliday++;
            }
            if (s.contains("가격정보 반영 완료")) {
                successPrice++;
            }
            if (s.contains("생산 데이터 저장 완료")) {
                successProcess++;
            }
            if (s.contains("생산 데이터 저장 실패")) {
                failedProcess++;
            }
            if (s.contains("집계 데이터 저장 완료")) {
                successFinal++;
            }
            if (s.contains("집계 데이터 저장 실패")) {
                failedFinal++;
            }
        }
        // 결과를 깔끔하게 출력
        sb.append("=== 로그 분석 결과 ===\n");
        sb.append("공휴일 갱신완료: ").append(successHoliday).append("회\n");
        sb.append("가격정보 반영 완료: ").append(successPrice).append("회\n");
        sb.append("생산 데이터 저장 완료: ").append(successProcess).append("회\n");
        sb.append("생산 데이터 저장 실패: ").append(failedProcess).append("회\n");
        sb.append("집계 데이터 저장 완료: ").append(successFinal).append("회\n");
        sb.append("집계 데이터 저장 실패: ").append(failedFinal).append("회\n");

        return sb.toString();
    }
}
