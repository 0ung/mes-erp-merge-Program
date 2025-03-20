package codehows.com.daehoint.controller;

import codehows.com.daehoint.annotation.AccessLogAnnotation;
import codehows.com.daehoint.annotation.DownloadLogAnnotation;
import codehows.com.daehoint.service.LogService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * <b>LogController 클래스</b><br>
 * 로그 데이터 조회 및 다운로드를 처리하는 REST 컨트롤러입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - 로그인 기록, 페이지 접근 기록, 출력 기록, 다운로드 기록 등의 로그 데이터를 엑셀 형식으로 제공.<br>
 * - 특정 페이지 접근 시 `@AccessLogAnnotation`을 통해 접근 로그를 기록.<br>
 * - 다운로드 요청 시 `@DownloadLogAnnotation`을 통해 다운로드 로그를 기록.<br><br>
 *
 * <b>핵심 엔드포인트:</b><br>
 * 1. <b>`/log`</b>:<br>
 *    - 전체 로그 데이터를 조회.<br>
 * 2. <b>`/log/login`</b>:<br>
 *    - 로그인 기록 데이터를 엑셀로 다운로드.<br>
 * 3. <b>`/log/page`</b>:<br>
 *    - 페이지 접근 기록 데이터를 엑셀로 다운로드.<br>
 * 4. <b>`/log/print`</b>:<br>
 *    - 출력 기록 데이터를 엑셀로 다운로드.<br>
 * 5. <b>`/log/download`</b>:<br>
 *    - 다운로드 기록 데이터를 엑셀로 다운로드.<br><br>
 *
 * <b>구성 요소:</b><br>
 * - <b>서비스 의존성:</b> `LogService`.<br>
 * - <b>애노테이션:</b> `@RestController`, `@RequestMapping`, `@AccessLogAnnotation`, `@DownloadLogAnnotation`.<br><br>
 *
 * <b>특징:</b><br>
 * - HTTP 응답에 직접 엑셀 파일을 작성하여 전송.<br>
 * - 각 로그 유형별로 개별 메서드를 통해 엑셀 파일 제공.<br><br>
 *
 * <b>예외 처리:</b><br>
 * - 엑셀 파일 생성 또는 응답 처리 중 예외 발생 시 런타임 예외를 발생시킴.<br>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {

	private final LogService logService;

	@GetMapping("")
	@AccessLogAnnotation(accessPage = "기록 관리 페이지")
	public ResponseEntity<?> logHistory() {
		return new ResponseEntity<>(logService.getLogResponse(), HttpStatus.OK);
	}

	/**
	 * 로그인 기록 엑셀 다운로드
	 *
	 * @param response HttpServletResponse를 통해 엑셀 파일을 응답으로 전송
	 * @throws IOException 엑셀 파일 생성 및 응답 처리 중 오류 발생 시
	 */
	@GetMapping("/login")
	@DownloadLogAnnotation(fileName = "login_logs")
	public void downloadLoginHistory(HttpServletResponse response) throws IOException {
		// 엑셀 데이터를 응답 스트림에 기록
		try (Workbook workbook = logService.generateLoginExcelFile(); ServletOutputStream outputStream = response.getOutputStream()) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=login_logs.xlsx");
			workbook.write(outputStream); // Workbook을 직접 출력 스트림에 작성
			outputStream.flush();
		} catch (IOException e) {
			// 스트림 관련 예외 처리
			throw new RuntimeException("Error while writing excel data to response", e);
		}
	}

	/**
	 * 페이지 접근 기록 엑셀 다운로드
	 *
	 * @param response HttpServletResponse를 통해 엑셀 파일을 응답으로 전송
	 * @throws IOException 엑셀 파일 생성 및 응답 처리 중 오류 발생 시
	 */
	@GetMapping("/page")
	@DownloadLogAnnotation(fileName = "page_access_logs")
	public void downloadPageAccessHistory(HttpServletResponse response) throws IOException {

		try (Workbook workbook = logService.generatePageAccessExcelFile();
			 ServletOutputStream outputStream = response.getOutputStream()) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=access_logs.xlsx");
			workbook.write(outputStream);  // Workbook을 직접 출력 스트림에 작성
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error while writing excel data to response", e);
		}
	}

	/**
	 * 출력 기록 엑셀 다운로드
	 *
	 * @param response HttpServletResponse를 통해 엑셀 파일을 응답으로 전송
	 * @throws IOException 엑셀 파일 생성 및 응답 처리 중 오류 발생 시
	 */
	@GetMapping("/print")
	@DownloadLogAnnotation(fileName = "print_logs")
	public void downloadPrintHistory(HttpServletResponse response) throws IOException {
		try (Workbook workbook = logService.generatePrintExcelFile();
			 ServletOutputStream outputStream = response.getOutputStream()) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=print_logs.xlsx");
			workbook.write(outputStream);  // Workbook을 직접 출력 스트림에 작성
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error while writing excel data to response", e);
		}
	}

	/**
	 * 다운로드 기록 엑셀 다운로드
	 *
	 * @param response HttpServletResponse를 통해 엑셀 파일을 응답으로 전송
	 * @throws IOException 엑셀 파일 생성 및 응답 처리 중 오류 발생 시
	 */
	@GetMapping("/download")
	@DownloadLogAnnotation(fileName = "download_logs")
	public void downloadDownloadHistory(HttpServletResponse response) throws IOException {
		try (Workbook workbook = logService.generateDownloadHistoryExcelFile();
			 ServletOutputStream outputStream = response.getOutputStream()) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=download_logs.xlsx");
			workbook.write(outputStream);  // Workbook을 직접 출력 스트림에 작성
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error while writing excel data to response", e);
		}
	}
}