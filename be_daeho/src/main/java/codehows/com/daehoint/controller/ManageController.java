package codehows.com.daehoint.controller;

import codehows.com.daehoint.annotation.AccessLogAnnotation;
import codehows.com.daehoint.dto.HolidayResponse;
import codehows.com.daehoint.dto.MemberResponse;
import codehows.com.daehoint.dto.SignUpRequest;
import codehows.com.daehoint.dto.StandardInfoResponse;
import codehows.com.daehoint.service.ManageService;
import codehows.com.daehoint.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <b>ManageController 클래스</b><br>
 * 회원 관리, 기준 정보 관리, 휴일 관리 등 다양한 관리 기능을 제공하는 REST 컨트롤러입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - 회원 가입, 조회, 업데이트, 비밀번호 초기화.<br>
 * - 기준 정보 조회 및 업데이트.<br>
 * - 휴일 생성, 조회, 업데이트, 삭제.<br><br>
 *
 * <b>핵심 엔드포인트:</b><br>
 * 1. <b>`/manage/signup`</b>:<br>
 *    - 회원 가입 요청을 처리.<br>
 * 2. <b>`/manage/member`</b>:<br>
 *    - 회원 목록을 조회하거나 업데이트.<br>
 * 3. <b>`/manage/password/{id}`</b>:<br>
 *    - 회원 비밀번호 초기화.<br>
 * 4. <b>`/manage/standard`</b>:<br>
 *    - 기준 정보를 조회하거나 업데이트.<br>
 * 5. <b>`/manage/holiday`</b>:<br>
 *    - 휴일 데이터를 생성, 조회, 업데이트, 삭제.<br><br>
 *
 * <b>구성 요소:</b><br>
 * - <b>서비스 의존성:</b> `ManageService`, `MemberService`.<br>
 * - <b>애노테이션:</b> `@RestController`, `@RequestMapping`, `@AccessLogAnnotation`.<br><br>
 *
 * <b>특징:</b><br>
 * - 요청에 따라 회원 및 휴일 데이터를 처리하는 관리 기능 제공.<br>
 * - 각 엔드포인트는 명확한 HTTP 메서드(`GET`, `POST`, `PUT`, `DELETE`)를 사용하여 작업을 수행.<br>
 * - `@AccessLogAnnotation`을 통해 특정 페이지 접근 시 로그 기록.<br><br>
 *
 * <b>예외 처리:</b><br>
 * - 관리 작업 중 발생하는 오류는 `GlobalExceptionHandler`를 통해 처리.<br>
 */
@RestController
@RequestMapping("/manage")
@RequiredArgsConstructor
@Slf4j
public class ManageController {

	private final ManageService manageService;
	private final MemberService memberService;

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
		log.info("회원가입");

		memberService.signUp(signUpRequest);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@AccessLogAnnotation(accessPage = "회원관리")
	@GetMapping("/member")
	public ResponseEntity<?> getMember() {
		return new ResponseEntity<>(manageService.getMember(), HttpStatus.OK);
	}

	@PutMapping("/member")
	public ResponseEntity<?> updateMember(@RequestBody MemberResponse response) {
		manageService.updateMember(response);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/password/{id}")
	public ResponseEntity<?> resetPassword(@PathVariable(name = "id") String id) {
		manageService.resetPassword(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@AccessLogAnnotation(accessPage = "기준정보관리")
	@GetMapping("/standard")
	public ResponseEntity<?> getStandardInfo() {
		StandardInfoResponse response = manageService.getStandardInfo();
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
			.body(response);
	}

	@PutMapping("/standard")
	public ResponseEntity<?> updateStandardInfo(@RequestBody StandardInfoResponse standardInfoResponse) {
		manageService.updateStandardInfo(standardInfoResponse);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/holiday")
	public ResponseEntity<?> createHoliday(@RequestBody HolidayResponse holidayResponse) {
		manageService.createHoliday(holidayResponse);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@AccessLogAnnotation(accessPage = "휴일관리")
	@GetMapping("/holiday")
	public ResponseEntity<?> getHoliday() {
		return new ResponseEntity<>(manageService.getHoliday(), HttpStatus.OK);
	}

	@PutMapping("/holiday")
	public ResponseEntity<?> updateHoliday(@RequestBody HolidayResponse holidayResponse) {
		manageService.updateHoliday(holidayResponse);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/holiday/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		manageService.deleteHoliday(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
