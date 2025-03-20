package codehows.com.daehoint.annotation;

import codehows.com.daehoint.entity.AccessPageHistory;
import codehows.com.daehoint.entity.DownloadHistory;
import codehows.com.daehoint.entity.LoginHistory;
import codehows.com.daehoint.entity.PrintHistory;
import codehows.com.daehoint.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * <b>LoggingAspect 클래스</b><br>
 * 이 클래스는 프로젝트에서 정의된 커스텀 Annotation과 연동하여 로그 기록 기능을 제공하는 AOP(Aspect-Oriented Programming) 클래스입니다.<br>
 * 각 어노테이션의 동작을 감지하여 로그를 기록하거나, 관련 비즈니스 로직을 수행합니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - `AccessLogAnnotation`: 특정 페이지 접근 시 로그 기록.<br>
 * - `DownloadLogAnnotation`: 파일 다운로드 요청 시 로그 기록.<br>
 * - `LoginLogAnnotation`: 로그인 이벤트 발생 시 로그 기록.<br>
 * - `PrintLogAnnotation`: 인쇄 요청 발생 시 로그 기록.<br><br>
 *
 * <b>핵심 메서드:</b><br>
 * - `loginLog()`: 로그인 이벤트에 대한 로그를 저장.<br>
 * - `printLog(JoinPoint)`: 인쇄 이벤트에 대한 로그를 저장.<br>
 * - `accessLog(JoinPoint)`: 페이지 접근 이벤트에 대한 로그를 저장.<br>
 * - `download(JoinPoint)`: 파일 다운로드 이벤트에 대한 로그를 저장.<br><br>
 *
 * <b>주요 로직:</b><br>
 * - 클라이언트 IP 및 사용자 ID 추출:<br>
 *   - 클라이언트의 IP 주소는 헤더 정보(`X-Forwarded-For`, `X-Real-IP`)와 `request.getRemoteAddr()`를 통해 확인.<br>
 *   - 사용자 ID는 Spring Security의 `SecurityContextHolder`를 통해 추출.<br>
 * - 어노테이션의 속성 값을 동적으로 읽어 로그 저장에 활용.<br>
 * - 관련 엔티티(`AccessPageHistory`, `DownloadHistory`, `LoginHistory`, `PrintHistory`)에 데이터를 저장.<br><br>
 *
 * <b>구성:</b><br>
 * - `@Aspect`: 이 클래스가 AOP에서 동작하도록 설정.<br>
 * - `@Component`: Spring Bean으로 등록.<br>
 * - `@RequiredArgsConstructor`: 의존성 주입을 위한 Lombok 어노테이션.<br>
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

	private final LogService logService;

	// 공통 함수: IP 주소와 사용자 ID를 가져오는 로직
	private String getClientIp() throws IllegalAccessException {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			throw new IllegalAccessException();
		}

		HttpServletRequest request = attributes.getRequest();

		// 먼저 X-Forwarded-For 헤더에서 클라이언트 IP를 가져옴
		String ip = request.getHeader("X-Forwarded-For");
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
			// X-Forwarded-For에 여러 IP가 포함될 수 있으므로 첫 번째 IP를 가져옴
			return ip.split(",")[0];
		}

		// X-Forwarded-For 헤더가 없으면 X-Real-IP를 확인
		ip = request.getHeader("X-Real-IP");
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}

		// 마지막으로 request.getRemoteAddr()에서 IP를 가져옴
		return request.getRemoteAddr();
	}


	private String getUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return "인가되지 않은 인원";
		}
		return authentication.getName();
	}

	@AfterReturning("@annotation(LoginLogAnnotation)")
	public void loginLog() throws IllegalAccessException {
		String ip = getClientIp();
		String id = getUserId();

		logService.loginLogSave(
			LoginHistory.builder()
				.accessIp(ip)
				.accessId(id)
				.build()
		);
	}

	@AfterReturning("@annotation(PrintLogAnnotation)")
	public void printLog(JoinPoint joinPoint) throws IllegalAccessException {
		String ip = getClientIp();
		String id = getUserId();

		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		PrintLogAnnotation printLogAnnotation = method.getAnnotation(PrintLogAnnotation.class);

		logService.printLogSave(
			PrintHistory.builder()
				.accessId(id)
				.accessIp(ip)
				.printPage(printLogAnnotation.printPage())
				.build()
		);
	}

	@AfterReturning("@annotation(AccessLogAnnotation)")
	public void accessLog(JoinPoint joinPoint) throws IllegalAccessException {
		String ip = getClientIp();
		String id = getUserId();

		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		AccessLogAnnotation accessLogAnnotation = method.getAnnotation(AccessLogAnnotation.class);
		Object[] args = joinPoint.getArgs();
		String processType = "";
		if (args != null) {
			for (Object arg : args) {
				if (arg instanceof String a) {
					processType = a; // processType 추출
					break;
				}
			}
		}

		logService.accessLogSave(
			AccessPageHistory.builder()
				.accessPage(processType + accessLogAnnotation.accessPage())
				.accessId(id)
				.accessIp(ip)
				.build()
		);
	}

	@AfterReturning("@annotation(DownloadLogAnnotation)")
	public void download(JoinPoint joinPoint) throws IllegalAccessException {
		// 클라이언트 IP와 유저 ID를 가져옴
		String ip = getClientIp();
		String id = getUserId();

		// 메서드 시그니처에서 해당 메서드와 어노테이션을 가져옴
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		DownloadLogAnnotation downloadLogAnnotation = method.getAnnotation(DownloadLogAnnotation.class);

		Object[] args = joinPoint.getArgs();
		String processType = "";
		if (args != null) {
			for (Object arg : args) {
				if (arg instanceof String) {
					processType = (String)arg; // processType 추출
					break;
				}
			}
		}
		String dynamicFileName = processType + downloadLogAnnotation.fileName();
		// 로그 저장
		logService.downloadSave(
			DownloadHistory.builder()
				.accessId(id)
				.accessIp(ip)
				.fileName(dynamicFileName)  // 동적으로 생성한 파일 이름 사용
				.build()
		);
	}

}
