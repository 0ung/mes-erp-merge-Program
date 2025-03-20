package codehows.com.daehoint.controller;

import codehows.com.daehoint.annotation.LoginLogAnnotation;
import codehows.com.daehoint.dto.LoginRequest;
import codehows.com.daehoint.dto.LoginResponse;
import codehows.com.daehoint.dto.PasswordUpdateRequest;
import codehows.com.daehoint.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

/**
 * <b>MemberController 클래스</b><br>
 * 회원 인증 및 계정 관리를 위한 REST 컨트롤러입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - 로그인 및 로그아웃 처리.<br>
 * - 비밀번호 수정.<br>
 * - 액세스 토큰 갱신.<br><br>
 *
 * <b>핵심 엔드포인트:</b><br>
 * 1. <b>`/member/login`</b>:<br>
 *    - 회원 로그인 요청을 처리.<br>
 *    - 성공 시 `LoginResponse`를 반환하며, 액세스 및 리프레시 토큰을 포함.<br>
 * 2. <b>`/member/logout`</b>:<br>
 *    - 회원 로그아웃 요청을 처리.<br>
 * 3. <b>`/member/password`</b>:<br>
 *    - 회원 비밀번호 수정 요청을 처리.<br>
 *    - `Principal` 객체를 사용하여 현재 사용자 확인 후 비밀번호 변경.<br>
 * 4. <b>`/member/refresh`</b>:<br>
 *    - 리프레시 토큰을 사용하여 새로운 액세스 토큰을 생성.<br>
 *    - `LoginResponse` 객체로 새 토큰을 반환.<br><br>
 *
 * <b>구성 요소:</b><br>
 * - <b>서비스 의존성:</b> `MemberService`.<br>
 * - <b>애노테이션:</b> `@RestController`, `@RequestMapping`, `@LoginLogAnnotation`.<br><br>
 *
 * <b>특징:</b><br>
 * - 로그인 시 `@LoginLogAnnotation`을 활용하여 로그 기록.<br>
 * - 요청에 따라 적절한 HTTP 상태 코드(`200 OK`, `401 Unauthorized` 등)를 반환.<br>
 * - 비밀번호 변경, 로그아웃, 토큰 갱신 등 계정 관리 기능 포함.<br><br>
 *
 * <b>예외 처리:</b><br>
 * - 인증 및 요청 처리 중 발생하는 오류는 `GlobalExceptionHandler`를 통해 처리.<br>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {
	private final MemberService memberService;

	@PostMapping("/login")
	@LoginLogAnnotation()
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws
		Exception {
		log.info("로그인 접속시도");
		LoginResponse login = memberService.login(loginRequest, response);
		return ResponseEntity.ok(login);
	}

	@GetMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		log.info("로그아웃");
		memberService.logout(response);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@PatchMapping("/password")
	public ResponseEntity<?> updatePassword(Principal principal,
		@RequestBody PasswordUpdateRequest passwordUpdateRequest) {
		log.info("비밀번호 수정");
		memberService.updatePassword(principal, passwordUpdateRequest);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/refresh")
	public ResponseEntity<?> getRefresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return new ResponseEntity<>(
			LoginResponse.builder().accessToken(memberService.refreshToken(request, response)).build(), HttpStatus.OK);
	}
}
