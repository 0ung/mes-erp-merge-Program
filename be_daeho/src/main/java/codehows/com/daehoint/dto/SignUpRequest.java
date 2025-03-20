package codehows.com.daehoint.dto;

import codehows.com.daehoint.constants.Authority;
import codehows.com.daehoint.constants.Rank;
import codehows.com.daehoint.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class SignUpRequest {
    private String id;
    private String name;
    @Setter
    private String password;
    private Rank rank;
    private Authority auth;

    public static Member toEntity(SignUpRequest signUpRequest){
        return Member.builder()
                .id(signUpRequest.getId())
                .name(signUpRequest.getName())
                .password(signUpRequest.getPassword())
                .rank(signUpRequest.getRank())
                .authority(signUpRequest.getAuth())
                .build();
    }
}
