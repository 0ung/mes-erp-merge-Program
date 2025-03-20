package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.ProcessStockResponse;
import codehows.com.daehoint.entity.StandardInfo;
import codehows.com.daehoint.repository.StandardInfoRepo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelProcessStock {

    private final ResourceLoader resourceLoader;
    private final StandardInfoRepo standardInfoRepo;
    private final ProcessStockService processStockService;

    //TODO 20241226 전체 공정데이터 조회로 메서드 수정 필요시 다시 수정 할것
    //TODO 20250102 공정 데이터 수 변환
    public void excelDownload(HttpServletResponse response, boolean isSnapShot) throws IOException {
        try (InputStream inputStream = resourceLoader.getResource("classpath:excel/processStock.xlsx").getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            // 1. 템플릿에서 Sheet 가져오기
            Sheet sheet = workbook.getSheetAt(0);

            // 2. 데이터 생성 및 삽입
            List<ProcessStockResponse> stockList = processStockService.getProcessStock(isSnapShot);
//            List<ProcessStockResponse> stockList = reportService.getTotalProcess(true);

            addData(sheet, stockList, workbook);

            // 4. 엑셀 시트에 데이터 채우기
            fillExcelSheet(sheet, provideExcelData());

            for (int i = 0; i < 18; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                // 자동 크기 조정 후, 열 너비에 여유를 추가 (10 ~ 50 단위로 늘려줄 수 있음)
                sheet.setColumnWidth(i, currentWidth + 500);
            }
            // 5. HTTP 응답 설정
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"purchase_report.xlsx\"");

            // 6. 엑셀 데이터를 HTTP Response로 출력
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 카테고리별로 데이터를 분류하는 함수
    private Map<String, List<ProcessStockResponse>> categorizeByCategory(List<ProcessStockResponse> stockList, String[] arrSort) {
        Map<String, List<ProcessStockResponse>> categorizedData = new LinkedHashMap<>();

        // arrSort에 정의된 카테고리 순서대로 Map에 초기화
        for (String category : arrSort) {
            categorizedData.put(category, new ArrayList<>());  // 각 카테고리에 대한 List 초기화
        }
        // stockList를 순회하며 각 카테고리별로 분류
        for (ProcessStockResponse stock : stockList) {
            String category = stock.getCategory();
            if (categorizedData.containsKey(category)) {
                categorizedData.get(category).add(stock);  // 해당 카테고리 List에 추가
            }
        }

        return categorizedData;  // 카테고리별로 분류된 데이터 반환
    }

    private void addData(Sheet sheet, List<ProcessStockResponse> stockList, Workbook workbook) {
        String[] arrSort = {"SM ASSY", "IM ASSY", "DIP ASSY", "PCB ASSY", "CASE ASSY", "PACKING ASSY"};
        int rowIndex = 10;

        Map<String, List<ProcessStockResponse>> result = categorizeByCategory(stockList, arrSort);
        for (String category : result.keySet()) {
            int no = 0;
            List<ProcessStockResponse> categoryStocks = result.get(category);

            if (!categoryStocks.isEmpty()) {
                sheet.shiftRows(rowIndex, sheet.getLastRowNum(), categoryStocks.size());
            }
            // 각 카테고리별로 데이터 추가
            for (ProcessStockResponse stock : categoryStocks) {
                no++;
                Row row = sheet.createRow(rowIndex++);
                updateCell(stock, row, no, workbook);  // stock 데이터를 Excel row에 입력
            }
            sumData(categoryStocks, rowIndex, sheet, workbook);

            rowIndex += 5;  // 각 카테고리 사이에 2행을 띄우는 경우
        }

        rowIndex += 5;

        sumData(stockList, rowIndex, sheet, workbook);
    }

    private void sumData(List<ProcessStockResponse> categoryStocks, int rowIndex, Sheet sheet, Workbook workbook) {
        Row row = sheet.createRow(rowIndex);
        // 합계 계산
        double totalMaterialCost = categoryStocks.stream().mapToDouble(ProcessStockResponse::getMaterialCost).sum();
        double totalProcessingCost = categoryStocks.stream().mapToDouble(ProcessStockResponse::getProcessingCost).sum();
        double totalCost = categoryStocks.stream().mapToDouble(ProcessStockResponse::getTotalCost).sum();
        double totalWipQuantity = categoryStocks.stream().mapToDouble(ProcessStockResponse::getWipQuantity).sum();
        double totalWipCost = categoryStocks.stream().mapToDouble(ProcessStockResponse::getWipCost).sum();
        double totalQcPendingQuantity = categoryStocks.stream().mapToDouble(ProcessStockResponse::getQcPendingQuantity).sum();
        double totalQcPendingCost = categoryStocks.stream().mapToDouble(ProcessStockResponse::getQcPendingCost).sum();
        double totalQcPassedQuantity = categoryStocks.stream().mapToDouble(ProcessStockResponse::getQcPassedQuantity).sum();
        double totalQcPassedCost = categoryStocks.stream().mapToDouble(ProcessStockResponse::getQcPassedCost).sum();
        double totalDefectiveQuantity = categoryStocks.stream().mapToDouble(ProcessStockResponse::getDefectiveQuantity).sum();
        double totalDefectiveCost = categoryStocks.stream().mapToDouble(ProcessStockResponse::getDefectiveCost).sum();
        double totalQuantity = categoryStocks.stream().mapToDouble(ProcessStockResponse::getTotalQuantity).sum();
        double totalCostSummary = categoryStocks.stream().mapToDouble(ProcessStockResponse::getTotalCostSummary).sum();

        // 계산된 합계를 셀에 입력 (스타일 포함)
        setCellWithStyle(row, 0, "합계", workbook);
        setCellWithStyle(row, 1, "", workbook);
        setCellWithStyle(row, 2, "", workbook);
        setCellWithStyle(row, 3, "", workbook);
        setCellWithStyle(row, 4, totalMaterialCost, workbook);  // 다섯 번째 셀: 원가
        setCellWithStyle(row, 5, totalProcessingCost, workbook);  // 여섯 번째 셀: 가공비
        setCellWithStyle(row, 6, totalCost, workbook);  // 일곱 번째 셀: 총비용
        setCellWithStyle(row, 7, totalWipQuantity, workbook);  // 여덟 번째 셀: WIP 수량
        setCellWithStyle(row, 8, totalWipCost, workbook);  // 아홉 번째 셀: WIP 비용
        setCellWithStyle(row, 9, totalQcPendingQuantity, workbook);  // 열 번째 셀: QC 대기 수량
        setCellWithStyle(row, 10, totalQcPendingCost, workbook);  // 열한 번째 셀: QC 대기 비용
        setCellWithStyle(row, 11, totalQcPassedQuantity, workbook);  // 열두 번째 셀: QC 통과 수량
        setCellWithStyle(row, 12, totalQcPassedCost, workbook);  // 열세 번째 셀: QC 통과 비용
        setCellWithStyle(row, 13, totalDefectiveQuantity, workbook);  // 열네 번째 셀: 불량 수량
        setCellWithStyle(row, 14, totalDefectiveCost, workbook);  // 열다섯 번째 셀: 불량 비용
        setCellWithStyle(row, 15, totalQuantity, workbook);  // 열여섯 번째 셀: 총 수량
        setCellWithStyle(row, 16, totalCostSummary, workbook);  // 열일곱 번째 셀: 총비용 요약
        setCellWithStyle(row, 17, "", workbook);  // 열여덟 번째 셀: 비고 (예: "합계" 텍스트)
    }


    private ExcelData provideExcelData() {
        ExcelData excelData = new ExcelData();
        StandardInfo standardInfo = standardInfoRepo.findById(1L).orElse(null);
        if (standardInfo == null) {
            return null;
        }
        excelData.addData("A4", "◆ 작성부서 : " + standardInfo.getProcessStockWritingDepartment());
        excelData.addData("A5", "◆ 작 성 자 : " + standardInfo.getProcessStockWriter());
        return excelData;
    }

    private void setCellWithStyle(Row row, int columnIndex, Object value, Workbook workbook) {
        // 셀 가져오기
        Cell cell = row.createCell(columnIndex);

        // 셀 스타일 설정
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);      // 상단 경계선 설정
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);  // 하단 경계선 설정
        cellStyle.setBorderLeft(BorderStyle.THIN);     // 좌측 경계선 설정
        cellStyle.setBorderRight(BorderStyle.THIN);    // 우측 경계선 설정
        cellStyle.setAlignment(HorizontalAlignment.CENTER);  // 수평 중앙 정렬
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 수직 중앙 정렬

        // 폰트 설정
        Font font = workbook.createFont();
        font.setBold(false);  // 굵게 설정하지 않음
        font.setFontHeightInPoints((short) 12);  // 폰트 크기 설정
        cellStyle.setFont(font);  // 폰트 적용

        // 숫자 형식 설정 (한국식 천 단위 쉼표 포맷)
        if (value instanceof Double || value instanceof Integer) {
            DataFormat format = workbook.createDataFormat();
            cellStyle.setDataFormat(format.getFormat("#,##0"));  // 한국식 천 단위 구분자
        }

        // 셀에 값 설정
        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue((String) value);  // 값이 String일 경우
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);  // 값이 Double일 경우
            } else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);  // 값이 Integer일 경우
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);  // 값이 Boolean일 경우
            }
        } else {
            cell.setCellValue("");  // 값이 null인 경우 빈 셀로 설정
        }

        // 셀 스타일 적용
        cell.setCellStyle(cellStyle);
    }

    private void updateCell(ProcessStockResponse stock, Row row, int no, Workbook workbook) {

        // 셀에 스타일을 적용하여 값 설정
        setCellWithStyle(row, 0, no, workbook);  // 두 번째 셀: 제품명
        setCellWithStyle(row, 1, stock.getProductName(), workbook);  // 두 번째 셀: 제품명
        setCellWithStyle(row, 2, stock.getModelNo(), workbook);  // 세 번째 셀: 모델 번호
        setCellWithStyle(row, 3, stock.getSpecification(), workbook);  // 네 번째 셀: 사양
        setCellWithStyle(row, 4, stock.getMaterialCost(), workbook);  // 다섯 번째 셀: 원가
        setCellWithStyle(row, 5, stock.getProcessingCost(), workbook);  // 여섯 번째 셀: 가공비
        setCellWithStyle(row, 6, stock.getTotalCost(), workbook);  // 일곱 번째 셀: 총비용
        setCellWithStyle(row, 7, stock.getWipQuantity(), workbook);  // 여덟 번째 셀: WIP 수량
        setCellWithStyle(row, 8, stock.getWipCost(), workbook);  // 아홉 번째 셀: WIP 비용
        setCellWithStyle(row, 9, stock.getQcPendingQuantity(), workbook);  // 열 번째 셀: QC 대기 수량
        setCellWithStyle(row, 10, stock.getQcPendingCost(), workbook);  // 열한 번째 셀: QC 대기 비용
        setCellWithStyle(row, 11, stock.getQcPassedQuantity(), workbook);  // 열두 번째 셀: QC 통과 수량
        setCellWithStyle(row, 12, stock.getQcPassedCost(), workbook);  // 열세 번째 셀: QC 통과 비용
        setCellWithStyle(row, 13, stock.getDefectiveQuantity(), workbook);  // 열네 번째 셀: 불량 수량
        setCellWithStyle(row, 14, stock.getDefectiveCost(), workbook);  // 열다섯 번째 셀: 불량 비용
        setCellWithStyle(row, 15, stock.getTotalQuantity(), workbook);  // 열여섯 번째 셀: 총 수량
        setCellWithStyle(row, 16, stock.getTotalCostSummary(), workbook);  // 열일곱 번째 셀: 총비용 요약
        setCellWithStyle(row, 17, stock.getRemarks(), workbook);  // 열여덟 번째 셀: 비고
    }


    private void fillExcelSheet(Sheet sheet, ExcelData excelData) {
        for (String cellAddress : excelData.getDataSet()) { //저장된 모든 셀 주소
            String value = excelData.getData(cellAddress);

            CellReference cellRef = new CellReference(cellAddress);
            Row row = sheet.getRow(cellRef.getRow());
            if (row == null) {
                row = sheet.createRow(cellRef.getRow());
            }
            Cell cell = row.getCell(cellRef.getCol());
            if (cell == null) {
                cell = row.createCell(cellRef.getCol());
            }
            cell.setCellValue(value);
        }
    }
}
