package codehows.com.daehoint.config.jwt;

import codehows.com.daehoint.entity.Member;
import codehows.com.daehoint.repository.MemberRepo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TokenProvider {

	private final SecretKey key;
	private final MacAlgorithm alg;
	private final MemberRepo memberRepo;

	public TokenProvider(@Value("${jwt.secretKey}") String secretKey, MemberRepo memberRepo) {
		this.memberRepo = memberRepo;
		this.alg = Jwts.SIG.HS512; // HMAC-SHA512 알고리즘 사용
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // 비밀 키 자동 생성
	}

	// 만료 시간 구하는 함수
	private Date getExpiry(int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	// JWT 생성 공통 로직
	private String createJwt(String subject, Date expiry, Map<String, Object> claims) {
		return Jwts.builder()
			.header()
			.add("typ", "JWT")
			.and()
			.claims(claims)
			.subject(subject)
			.expiration(expiry)
			.issuedAt(new Date())
			.signWith(key, alg)
			.compact();
	}

	private Jws<Claims> parseJwt(String token) {
		return Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token);
	}

	// Access Token 생성 메서드
	public String createAccessToken(Member member) {
		Date expiry = getExpiry(60); // Access Token 유효 기간: 30분
		Map<String, Object> claims = new HashMap<>();
		claims.put("rank", member.getRank());
		claims.put("id", member.getId());
		return createJwt(member.getName(), expiry, claims);
	}

	// Refresh Token 생성 메서드
	public String createRefreshToken(Member member) {
		Date expiry = getExpiry(7 * 24 * 60);
		// Refresh Token 유효 기간: 7일
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", member.getId());
		return createJwt(member.getName(), expiry, claims);
	}

	public boolean validateToken(String token) {
		try {
			parseJwt(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.error("만료된 토큰입니다.");
		} catch (MalformedJwtException e) {
			log.error("손상된 JWT 토큰");
		} catch (SignatureException e) {
			log.error("서명오류");
		} catch (IllegalArgumentException e) {
			log.error("잘못된 토큰");
		} catch (Exception e) {
			log.error("Invalidation TOKEN ");
		}
		return false;
	}

	public String parsingToken(String token) {
		Jws<Claims> jwt = parseJwt(token);
		return (String)jwt.getPayload().get("id");
	}

	public Authentication getAuthentication(String token) {
		String id = parsingToken(token);
		Member member = memberRepo.findById(id).orElse(null);
		if (member == null) {
			log.error("로그인되지않는 사용자");
		}
		return new UsernamePasswordAuthenticationToken(member.getUsername(), null, member.getAuthorities());
	}
}
