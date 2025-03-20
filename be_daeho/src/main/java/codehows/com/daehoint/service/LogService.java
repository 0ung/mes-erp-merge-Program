package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.LogResponse;
import codehows.com.daehoint.entity.AccessPageHistory;
import codehows.com.daehoint.entity.DownloadHistory;
import codehows.com.daehoint.entity.LoginHistory;
import codehows.com.daehoint.entity.PrintHistory;
import codehows.com.daehoint.excpetion.HistoryNotExistException;
import codehows.com.daehoint.repository.AccessPageHistoryRepo;
import codehows.com.daehoint.repository.DownloadHistoryRepo;
import codehows.com.daehoint.repository.LoginHistoryRepo;
import codehows.com.daehoint.repository.PrintHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;



/**
 * 로그 관리 서비스 클래스
 *
 * <p>이 클래스는 로그인 기록, 페이지 접근 기록, 출력 기록, 다운로드 기록 등의 로그 데이터를 관리하는 서비스입니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>로그 데이터 조회, 엑셀 파일로 생성</li>
 *   <li>모든 로그 데이터를 DTO로 변환하여 통합된 응답 객체로 반환</li>
 *   <li>로그별 엑셀 파일 생성 기능 제공</li>
 * </ul>
 *
 * <p>의존성:</p>
 * <ul>
 *   <li>{@code AccessPageHistoryRepo}: 페이지 접근 로그 데이터 관리</li>
 *   <li>{@code LoginHistoryRepo}: 로그인 로그 데이터 관리</li>
 *   <li>{@code PrintHistoryRepo}: 출력 로그 데이터 관리</li>
 *   <li>{@code DownloadHistoryRepo}: 다운로드 로그 데이터 관리</li>
 * </ul>
 *
 * <p>참고:</p>
 * <ul>
 *   <li>로그 데이터는 각각 별도의 레포지토리에서 관리됩니다.</li>
 *   <li>모든 로그 데이터는 Java POI 라이브러리를 사용하여 엑셀 파일로 변환 가능합니다.</li>
 *   <li>{@code HistoryNotExistException}: 로그 데이터가 없을 경우 예외 처리</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class LogService {
	private final AccessPageHistoryRepo accessRepository;
	private final LoginHistoryRepo loginRepository;
	private final PrintHistoryRepo printRepository;
	private final DownloadHistoryRepo downloadHistoryRepo;

	private  <T> void saveLog(T history, JpaRepository<T, Long> repository) {
		if (history == null) {
			throw new HistoryNotExistException("로그 정보가 없음");
		}
		repository.save(history);
	}

	public void loginLogSave(LoginHistory loginHistory) {
		saveLog(loginHistory, loginRepository);
	}

	public void printLogSave(PrintHistory printHistory) {
		saveLog(printHistory, printRepository);
	}

	public void accessLogSave(AccessPageHistory accessPageHistory) {
		saveLog(accessPageHistory, accessRepository);
	}

	public void downloadSave(DownloadHistory downloadHistory) {
		saveLog(downloadHistory, downloadHistoryRepo);
	}

	/**
	 * 로그인 기록, 페이지 접근 기록, 출력 기록을 각각의 리포지토리에서 조회하여
	 * DTO (Data Transfer Object)로 변환한 후, LogResponse 객체로 반환하는 메서드.
	 * <p>
	 * 각 로그 데이터를 해당 DTO로 변환하여 리스트로 생성하고, 이를 LogResponse에 담아 반환한다.
	 *
	 * @return LogResponse 로그인 기록, 페이지 접근 기록, 출력 기록 리스트를 포함한 LogResponse 객체를 반환.
	 */
	@Transactional
	public LogResponse getLogResponse() {

		List<LogResponse.LoginLogs> loginLogs = loginRepository.findAll().stream().map(LogResponse.LoginLogs::toLoginLogs).toList();
		List<LogResponse.PageAccessLogs> pageAccessLogs = accessRepository.findAll().stream().map(LogResponse.PageAccessLogs::toPageAccessLogs).toList();
		List<LogResponse.PrintLogs> printLogs = printRepository.findAll().stream().map(LogResponse.PrintLogs::toPrintLogs).toList();
		List<LogResponse.DownloadLogs> downloadLogs = downloadHistoryRepo.findAll().stream().map(LogResponse.DownloadLogs::toDownloadLogs).toList();
		return new LogResponse(loginLogs, pageAccessLogs, printLogs, downloadLogs);
	}


	public Workbook generateLoginExcelFile() throws IOException {
		List<LoginHistory> loginHistoryList = loginRepository.findAll();
		Workbook workbook = new XSSFWorkbook();  // 엑셀 워크북 생성
		Sheet sheet = workbook.createSheet("Login Logs");  // "Login Logs" 시트 생성

		// 헤더 생성
		Row headerRow = sheet.createRow(0);
		String[] headers = {"Time", "Access ID", "Access IP"};
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
		}

		// 데이터 추가
		int rowNum = 1;
		for (LoginHistory log : loginHistoryList) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(log.getCreateDateTime().toString());  // 시간 추가
			row.createCell(1).setCellValue(log.getAccessId());                   // 사용자 ID 추가
			row.createCell(2).setCellValue(log.getAccessIp());                   // IP 추가
		}

		return workbook;
	}

	public Workbook generatePageAccessExcelFile() throws IOException {
		List<AccessPageHistory> accessPageHistories = accessRepository.findAll();
		Workbook workbook = new XSSFWorkbook();;
		Sheet sheet = workbook.createSheet("Page Access Logs");

		// 헤더 생성
		Row headerRow = sheet.createRow(0);
		String[] headers = {"Time", "Access ID", "Access Page", "Access IP"};
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
		}

		// 데이터 추가
		int rowNum = 1;
		for (AccessPageHistory log : accessPageHistories) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(log.getCreateDateTime().toString());  // 시간 추가
			row.createCell(1).setCellValue(log.getAccessId());                   // 사용자 ID 추가
			row.createCell(2).setCellValue(log.getAccessPage());                 // 페이지 추가
			row.createCell(3).setCellValue(log.getAccessIp());                   // IP 추가
		}

		return workbook;
	}

	public Workbook generatePrintExcelFile() throws IOException {
		List<PrintHistory> printHistories = printRepository.findAll();
		Workbook workbook = new XSSFWorkbook();;
		Sheet sheet = workbook.createSheet("Print Logs");

		// 헤더 생성
		Row headerRow = sheet.createRow(0);
		String[] headers = {"Time", "Access ID", "Print Page", "Access IP"};
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
		}

		// 데이터 추가
		int rowNum = 1;
		for (PrintHistory log : printHistories) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(log.getCreateDateTime().toString());  // 시간 추가
			row.createCell(1).setCellValue(log.getAccessId());                   // 사용자 ID 추가
			row.createCell(2).setCellValue(log.getPrintPage());                  // 출력 페이지 추가
			row.createCell(3).setCellValue(log.getAccessIp());                   // IP 추가
		}

		return workbook;
	}

	public Workbook generateDownloadHistoryExcelFile() throws IOException {
		List<DownloadHistory> downloadHistoryList = downloadHistoryRepo.findAll();
		Workbook workbook = new XSSFWorkbook();;
		Sheet sheet = workbook.createSheet("Download History");

		// 헤더 생성
		Row headerRow = sheet.createRow(0);
		String[] headers = {"Time", "Access ID", "Access IP", "File Name"};
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
		}

		// 데이터 추가
		int rowNum = 1;
		for (DownloadHistory history : downloadHistoryList) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(history.getCreateDateTime().toString());  // 시간 추가
			row.createCell(1).setCellValue(history.getAccessId());                   // Access ID 추가
			row.createCell(2).setCellValue(history.getAccessIp());                   // Access IP 추가
			row.createCell(3).setCellValue(history.getFileName());                   // 파일 이름 추가
		}

		return workbook;
	}



	}
