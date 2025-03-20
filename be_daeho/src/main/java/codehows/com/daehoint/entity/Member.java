package codehows.com.daehoint.entity;

import codehows.com.daehoint.constants.Authority;
import codehows.com.daehoint.constants.Rank;
import codehows.com.daehoint.dto.MemberResponse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member")
public class Member extends BaseEntity implements UserDetails {
	@Id
	@Column(name = "member_id")
	private String id;
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "member_rank")
	private Rank rank;

	@Enumerated(EnumType.STRING)
	private Authority authority;

	private String name;

	@Column(nullable = true)
	@Setter
	private String refreshToken;

	//권한 부여
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		if (this.authority == Authority.ROLE_A) {
			authorities.add(new SimpleGrantedAuthority("ROLE_A"));
		} else if (this.authority == Authority.ROLE_B) {
			authorities.add(new SimpleGrantedAuthority("ROLE_B"));
		} else if (this.authority == Authority.ROLE_C) {
			authorities.add(new SimpleGrantedAuthority("ROLE_C"));
		} else if (this.authority == Authority.ROLE_ADMIN) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		} else {
			return null;
		}
		return authorities;
	}

	@Override
	public String getUsername() {
		return this.id;
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void updateMember(MemberResponse response) {
		this.id = response.getId();
		this.name = response.getName();
		this.authority = response.getAuth();
		this.rank = response.getRank();
	}

	public void resetPassword(PasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode("1234");
	}
}
