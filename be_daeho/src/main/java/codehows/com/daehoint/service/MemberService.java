package codehows.com.daehoint.service;

import codehows.com.daehoint.config.jwt.TokenProvider;
import codehows.com.daehoint.dto.LoginRequest;
import codehows.com.daehoint.dto.LoginResponse;
import codehows.com.daehoint.dto.PasswordUpdateRequest;
import codehows.com.daehoint.dto.SignUpRequest;
import codehows.com.daehoint.entity.Member;
import codehows.com.daehoint.excpetion.DuplicateException;
import codehows.com.daehoint.excpetion.NotFoundUserException;
import codehows.com.daehoint.repository.MemberRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;
import java.util.NoSuchElementException;

/**
 * MemberService 클래스
 *
 * <p>이 클래스는 사용자 인증 및 관리와 관련된 서비스 로직을 제공합니다.
 * 회원 가입, 로그인, 로그아웃, 비밀번호 변경, 리프레시 토큰 갱신 등의 주요 기능을 수행합니다.
 * Spring Security를 활용하여 인증 및 권한 관리를 구현하며, JWT 토큰 기반 인증 방식을 사용합니다.</p>
 *
 * 주요 기능:
 * <ul>
 *   <li>회원 조회: {@code findById(String id)}</li>
 *   <li>회원 가입: {@code signUp(SignUpRequest signUpRequest)}</li>
 *   <li>로그인: {@code login(LoginRequest loginRequest, HttpServletResponse response)}</li>
 *   <li>로그아웃: {@code logout(HttpServletResponse response)}</li>
 *   <li>비밀번호 변경: {@code updatePassword(Principal principal, PasswordUpdateRequest passwordUpdateRequest)}</li>
 *   <li>리프레시 토큰 갱신: {@code refreshToken(HttpServletRequest request, HttpServletResponse response)}</li>
 * </ul>
 *
 * <p>기술 스택 및 의존성:</p>
 * <ul>
 *   <li>{@code TokenProvider}: JWT 토큰 생성 및 검증</li>
 *   <li>{@code PasswordEncoder}: 비밀번호 암호화</li>
 *   <li>{@code AuthenticationManagerBuilder}: 인증 매니저 설정</li>
 *   <li>{@code MemberRepo}: 사용자 데이터 관리</li>
 * </ul>
 *
 * <p>주요 메서드 동작:</p>
 * <ul>
 *   <li>회원 가입 시 중복 검사 및 비밀번호 암호화를 수행합니다.</li>
 *   <li>로그인 시 Spring Security를 통한 인증 절차를 수행하며, 액세스 및 리프레시 토큰을 발급합니다.</li>
 *   <li>로그아웃 시 리프레시 토큰을 삭제하고 쿠키를 만료 처리합니다.</li>
 *   <li>리프레시 토큰 유효성을 검증하여 새로운 액세스 토큰을 생성합니다.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {
	private final MemberRepo memberRepo;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final TokenProvider tokenProvider;

	public Member findById(String id) {
		Member member = memberRepo.findById(id).orElse(null);
		if (member == null) {
			throw new NotFoundUserException("유저가 존재하지 않음");
		}
		return member;
	}

	//가입
	public void signUp(SignUpRequest signUpRequest) {
		Member validateMember = memberRepo.findById(signUpRequest.getId()).orElse(null);

		if (validateMember != null) {
			throw new DuplicateException();
		}

		signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		Member member = SignUpRequest.toEntity(signUpRequest);

		memberRepo.save(member);
	}

	public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) throws Exception {
		try {
			//이거는 스프링 시큐티리에 등록하기 위한 과정
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getId(),
				loginRequest.getPassword());
			Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			Member member = memberRepo.findById(authentication.getName()).orElseThrow();

			String refreshToken = tokenProvider.createRefreshToken(member);
			String accessToken = tokenProvider.createAccessToken(member);

			member.updateRefreshToken(refreshToken);
			setRefreshCookie(response, refreshToken);

			memberRepo.save(member);
			return new LoginResponse(accessToken, member.getAuthority());
		} catch (NoSuchElementException | InternalAuthenticationServiceException e1) {
			throw new NotFoundUserException();
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public void logout(HttpServletResponse response) throws NotFoundUserException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			// 리프레시 토큰을 DB에서 삭제
			String userId = authentication.getName(); // 사용자의 이름 또는 ID
			Member member = memberRepo.findById(userId).orElse(null);
			if (member == null) {
				throw new NotFoundUserException();
			}
			member.updateRefreshToken("");
			SecurityContextHolder.clearContext();
			cookieExpired(response);

		} else {
			throw new NotFoundUserException();
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return findById(username);
	}

	private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true); // HttpOnly 속성 설정
		refreshTokenCookie.setSecure(true); // HTTPS를 사용할 때만 쿠키를 전송 (운영 환경에서는 true로 설정)
		refreshTokenCookie.setPath("/"); // 쿠키 경로 설정
		refreshTokenCookie.setMaxAge(6 * 60 * 60); // 6시간 동안 유효
		response.addCookie(refreshTokenCookie); // 응답에 쿠키 추가
	}

	private void cookieExpired(HttpServletResponse response) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", null);
		refreshTokenCookie.setHttpOnly(true);  // HttpOnly 속성 설정
		refreshTokenCookie.setSecure(true);    // HTTPS 환경에서만 전송
		refreshTokenCookie.setPath("/");       // 경로 설정
		refreshTokenCookie.setMaxAge(0);       // MaxAge 0으로 설정하여 쿠키 만료
		response.addCookie(refreshTokenCookie); // 응답에 쿠키 추가
	}

	@Transactional
	public void updatePassword(Principal principal, PasswordUpdateRequest passwordUpdateRequest) {
		Member member = memberRepo.findById(principal.getName()).orElse(null);
		if (member == null) {
			throw new NotFoundUserException("잘못된 비밀번호 수정 요청");
		}
		member.updatePassword(passwordEncoder.encode(passwordUpdateRequest.getPasswd()));
	}

	public String refreshToken(HttpServletRequest request, HttpServletResponse response) throws
		IOException {
		String refreshToken = getRefreshToken(request.getCookies());
		Member member = memberRepo.findById(tokenProvider.parsingToken(refreshToken)).orElse(null);
		if (member == null) {
			log.error("등록되지않은 사용자");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}
		String newAccessToken = null;
		if (refreshToken != null && tokenProvider.validateToken(refreshToken)) {
			newAccessToken = tokenProvider.createAccessToken(member);
		} else {
			log.error("유효하지 않은 Refresh Token");
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN");
		}
		return newAccessToken;
	}

	private String getRefreshToken(Cookie[] cookies) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
