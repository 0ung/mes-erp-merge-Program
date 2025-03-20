package codehows.com.daehoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginRequest {
    private String id;
    private String password;
}
