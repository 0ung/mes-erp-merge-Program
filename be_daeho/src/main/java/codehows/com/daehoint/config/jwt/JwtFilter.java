package codehows.com.daehoint.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	private final TokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// Access 토큰을 Authorization 헤더에서 추출
		String accessToken = getAccessToken(request.getHeader("Authorization"));
		// RefreshToken을 쿠키에서 추출
		if (accessToken != null && tokenProvider.validateToken(accessToken)) {
			// Access 토큰이 유효한 경우 SecurityContext에 인증 정보 설정
			SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(accessToken));
		}
		// 다음 필터로 진행
		filterChain.doFilter(request, response);
	}

	// Authorization 헤더에서 Access 토큰을 추출하는 메서드
	private String getAccessToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return null;
	}

}
