package codehows.com.daehoint.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailDTO {

    private String to;
    private String subject;
    private String message;

}
