package codehows.com.daehoint.dto;

import codehows.com.daehoint.constants.Authority;
import codehows.com.daehoint.constants.Rank;
import codehows.com.daehoint.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberResponse {
    private String name;
    private String id;
    private Rank rank;
    private Authority auth;

    public static MemberResponse toDTO(Member member){
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .rank(member.getRank())
                .auth(member.getAuthority())
                .build();
    }
}
