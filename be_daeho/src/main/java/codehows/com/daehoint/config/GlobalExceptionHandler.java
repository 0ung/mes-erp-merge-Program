package codehows.com.daehoint.config;

import codehows.com.daehoint.excpetion.DuplicateException;
import codehows.com.daehoint.excpetion.NotFoundUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * <b>GlobalExceptionHandler 클래스</b><br>
 * 애플리케이션 전반에서 발생하는 예외를 처리하고, 사용자에게 적절한 응답을 반환하는 글로벌 예외 처리 클래스입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - 다양한 유형의 예외를 처리하고, 로그를 기록하여 문제의 원인을 추적.<br>
 * - 사용자에게 적절한 HTTP 상태 코드와 메시지를 반환.<br><br>
 *
 * <b>핵심 메서드:</b><br>
 * 1. `handleUserException(NotFoundUserException e)`:<br>
 *    - 사용자를 찾을 수 없는 경우 발생하는 예외 처리.<br>
 *    - HTTP 상태 코드: `400 BAD_REQUEST`.<br>
 *    - 메시지: "회원이 존재하지 않습니다.".<br>
 * 2. `handleException(Exception e)`:<br>
 *    - 처리되지 않은 일반적인 예외를 처리.<br>
 *    - HTTP 상태 코드: `500 INTERNAL_SERVER_ERROR`.<br>
 *    - 메시지: "Interval Server Error".<br>
 * 3. `handleIllegalAccess(IllegalAccessException e)`:<br>
 *    - 불법 접근 예외를 처리.<br>
 *    - 로그 기록만 수행.<br>
 * 4. `handleIoException(IOException e)`:<br>
 *    - 입출력 예외를 처리.<br>
 *    - 로그 기록만 수행.<br>
 * 5. `handleDuplicateException(DuplicateException e)`:<br>
 *    - 중복된 회원 예외를 처리.<br>
 *    - HTTP 상태 코드: `400 BAD_REQUEST`.<br>
 *    - 메시지: "중복된 회원입니다.".<br><br>
 *
 * <b>주요 어노테이션:</b><br>
 * - `@RestControllerAdvice`: 컨트롤러 전반에서 예외를 처리하는 전역 예외 처리 클래스.<br>
 * - `@ExceptionHandler`: 특정 예외를 처리하기 위한 메서드에 적용.<br>
 * - `@Slf4j`: 로그 기록을 위한 Lombok 어노테이션.<br><br>
 *
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(NotFoundUserException.class)
	public ResponseEntity<?> handleUserException(NotFoundUserException e) {
		log.error("NotFoundUserException occurred: {}", e.getMessage(), e);
		return new ResponseEntity<>("회원이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(Exception e) {
		log.error("Unhandled Exception occurred: {}", e.getMessage(), e);
		return new ResponseEntity<>("Interval Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(IllegalAccessException.class)
	public void handleIllegalAccess(IllegalAccessException e) {
		log.error("IllegalAccessException occurred: {}", e.getMessage(), e);
	}

	@ExceptionHandler(IOException.class)
	public void handleIoException(IOException e) {
		log.error("IOException occurred: {}", e.getMessage(), e);
	}

	@ExceptionHandler(DuplicateException.class)
	public ResponseEntity<?> handleDuplicateException(DuplicateException e) {
		log.error("DuplicateException occurred: {}", e.getMessage(), e);
		return new ResponseEntity<>("중복된 회원입니다.", HttpStatus.BAD_REQUEST);
	}
}
