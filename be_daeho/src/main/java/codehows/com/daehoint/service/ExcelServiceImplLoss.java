package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.StandardInfoResponse;
import codehows.com.daehoint.dto.sync.DailyWorkLossResponse;
import codehows.com.daehoint.entity.StandardInfo;
import codehows.com.daehoint.repository.StandardInfoRepo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelServiceImplLoss extends ExcelService {

	private final ResourceLoader resourceLoader;
	private final StandardInfoRepo standardInfoRepo;
	private final LossDateService lossDateService;

	private List<List<Object>> lossDataToTable(List<DailyWorkLossResponse> lossDTOList) {
		List<List<Object>> dataList = new ArrayList<>();

		List<String> no = new ArrayList<>();    //순번
		List<String> lossReasonList = new ArrayList<>();  //구분
		List<String> lossContentsList = new ArrayList<>();  //내용
		List<Double> lossWorkerList = new ArrayList<>();    //인원
		List<Double> lossTimeList = new ArrayList<>();  //공수
		List<Double> lossAmountList = new ArrayList<>();    //비용
		List<String> stateProgressingList = new ArrayList<>();  //처리결과
		List<String> lossBlameDept01List = new ArrayList<>();   //책임부서
		List<String> remarkList = new ArrayList<>();    //비고

		List<String> lossTimeTotalList = new ArrayList<>();

		List<String> nul = new ArrayList<>();

		// DTO 데이터를 각 필드별로 추가
		for (int i = 0; i < lossDTOList.size(); i++) {
			DailyWorkLossResponse dto = lossDTOList.get(i);
			no.add(String.valueOf(i + 1));  // 순번
			lossReasonList.add(dto.getLossReason());    //구분
			lossContentsList.add(dto.getLossContents());    //내용
			lossWorkerList.add(dto.getLossWorker()); //인원
			lossTimeList.add(dto.getLossTime()); //공수
			lossAmountList.add(dto.getLossAmount()); //비용
			stateProgressingList.add(dto.getStateProgressing());    //처리결과
			lossBlameDept01List.add(dto.getLossBlameDept01());  //책임부서
			remarkList.add(dto.getRemark());    //비고

			lossTimeTotalList.add(dto.getLossTimeTotal().toString());   //합계 공수?
		}

		dataList.add(new ArrayList<>(no));  // 순번
		dataList.add(new ArrayList<>(lossReasonList));  // 구분
		dataList.add(new ArrayList<>(lossContentsList));  // 내용
		dataList.add(new ArrayList<>(lossWorkerList));   // 인원
		dataList.add(new ArrayList<>(lossTimeList));   // 공수
		dataList.add(new ArrayList<>(lossAmountList));   // 비용
		dataList.add(new ArrayList<>(stateProgressingList));  // 처리 결과
		dataList.add(new ArrayList<>(lossBlameDept01List));  // 책임부서
		dataList.add(new ArrayList<>(remarkList));  // 비고
		dataList.add(new ArrayList<>(lossTimeTotalList));   // 총합계 시간

		return dataList;
	}

	private List<Object> lossSummaryToTable(List<DailyWorkLossResponse> lossDTOList) {

		double totalLossWorker = lossDTOList.stream()
			.mapToDouble(DailyWorkLossResponse::getLossWorker)
			.sum();
		double totalLossAmount = lossDTOList.stream()
			.mapToDouble(DailyWorkLossResponse::getLossAmount)
			.sum();
		double totalLossTime = lossDTOList.stream()
			.mapToDouble(DailyWorkLossResponse::getLossTime)
			.sum();

		return List.of(
			totalLossWorker, totalLossTime, totalLossAmount
		);
	}

	//셀 주소 매핑 데이터
	@Override
	public ExcelData provideExcelData(int row,String category) {
		ExcelData excelData = new ExcelData();
		StandardInfo standardInfo = standardInfoRepo.findById(1L).orElse(null);

		StandardInfoResponse standardInfoResponse = StandardInfoResponse.builder()
			.power(100.0)
			.mainWritingDepartment(standardInfo == null ? "데이터가 없음" : standardInfo.getLossDepartment())
			.mainWriter(standardInfo == null ? "데이터가 없음" : standardInfo.getLossWriter())
			.build();

		excelData.addData("C4", standardInfoResponse.getMainWritingDepartment());
		excelData.addData("C5", standardInfoResponse.getMainWriter());

		return excelData;
	}

	@Override
	public void excelDownload(HttpServletResponse response, String filePath, boolean isSnapshot) throws IOException {
		try (
			InputStream inputStream = resourceLoader.getResource(filePath).getInputStream();
			Workbook workbook = new XSSFWorkbook(inputStream)
		) {
			Sheet sheet = workbook.getSheetAt(0);

			// DailyWorkLossDTO 리스트를 생성
			List<DailyWorkLossResponse> report = lossDateService.getLossReport(isSnapshot);

			// 데이터 추가 및 행 시작 위치 설정
			int firstRow = addDataToTable(sheet, lossDataToTable(report), 8, workbook, new HashMap<>()); // loss 데이터 추가
			addDataToCol(sheet, lossSummaryToTable(report), 8 + firstRow, workbook, new HashMap<>(), 3); // 합계 데이터 추가

			// ExcelData 객체 생성 및 셀 주소로 매핑된 데이터 추가
			ExcelData excelData = provideExcelData(firstRow,"더미"); // 셀 주소로 매핑된 데이터
			fillExcelSheet(sheet, excelData); // 셀 주소에 맞게 데이터 추가

			// 엑셀 파일을 HTTP 응답으로 출력
			workbook.write(response.getOutputStream());
			response.getOutputStream().flush();
		} catch (IOException e) {
			System.err.println("Error during Excel download: " + e.getMessage());
			throw e;
		}
	}

	@Override
	protected void cellStyle(Cell cell, Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);  // 수평 중앙 정렬
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 수직 중앙 정렬

		Font font = workbook.createFont();
		font.setBold(false);  // 굵게 설정
		font.setFontHeightInPoints((short)12);

		cell.setCellStyle(cellStyle);
	}

	@Override
	protected void cellStyle2(Cell cell, Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);  // 수평 중앙 정렬
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 수직 중앙 정렬

		Font font = workbook.createFont();
		font.setBold(false);  // 굵게 설정
		font.setFontHeightInPoints((short)12);

		cell.setCellStyle(cellStyle);
	}

}
