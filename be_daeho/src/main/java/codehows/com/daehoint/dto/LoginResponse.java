package codehows.com.daehoint.dto;

import codehows.com.daehoint.constants.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private Authority authority;
}
