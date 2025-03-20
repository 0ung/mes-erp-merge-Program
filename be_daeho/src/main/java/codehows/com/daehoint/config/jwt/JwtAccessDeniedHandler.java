	package codehows.com.daehoint.config.jwt;

    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.security.access.AccessDeniedException;
    import org.springframework.security.web.access.AccessDeniedHandler;

    import java.io.IOException;

	/**
	 * <b>JwtAccessDeniedHandler</b><br>
	 * JWT 인증 실패 시 접근 거부를 처리하는 클래스입니다.<br><br>
	 *
	 * <b>주요 기능:</b><br>
	 * - `AccessDeniedHandler` 인터페이스를 구현하여 인증되지 않은 사용자의 요청을 처리.<br>
	 * - 요청 쿠키에서 `refreshToken`을 확인하여 인증 상태를 판단.<br><br>
	 *
	 * <b>구성 요소:</b><br>
	 * 1. `handle()`:<br>
	 *    - 요청이 인증되지 않은 경우 적절한 HTTP 상태 코드를 반환.<br>
	 *      - `refreshToken`이 없으면 `403 Forbidden` 반환.<br>
	 *      - `refreshToken`이 존재하면 `401 Unauthorized` 반환.<br>
	 * 2. `getRefreshTokenFromCookies()`:<br>
	 *    - 요청에 포함된 쿠키 배열에서 `refreshToken` 값을 검색하는 헬퍼 메서드.<br><br>
	 *
	 * <b>HTTP 상태 코드:</b><br>
	 * - `403 FORBIDDEN`: 인증 실패 및 `refreshToken`이 없을 경우.<br>
	 * - `401 UNAUTHORIZED`: 인증 실패 및 `refreshToken`이 존재할 경우.<br><br>
	 *
	 * <b>사용 예:</b><br>
	 * - Spring Security 설정에서 `AccessDeniedHandler`로 등록하여, 인증되지 않은 요청에 대한 응답 처리.<br><br>
	 *
	 * <b>주요 어노테이션:</b><br>
	 * - 없음 (Spring Security 설정 클래스에서 사용)<br>
	 */
	public class JwtAccessDeniedHandler implements AccessDeniedHandler {

		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

			// 쿠키에서 refreshToken을 확인
			String refreshToken = getRefreshTokenFromCookies(request.getCookies());

			// refreshToken이 없으면 403 반환
			if (refreshToken == null) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Refresh token is missing");
			} else {
				// 그 외에는 401 반환
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			}
		}

		// refreshToken을 쿠키에서 가져오는 헬퍼 메서드
		private String getRefreshTokenFromCookies(Cookie[] cookies) {
			if (cookies == null) {
				return null;
			}
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
			return null;
		}

	}
