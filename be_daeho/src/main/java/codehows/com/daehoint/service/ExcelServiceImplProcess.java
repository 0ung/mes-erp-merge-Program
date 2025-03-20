package codehows.com.daehoint.service;

import codehows.com.daehoint.constants.Category;
import codehows.com.daehoint.dto.DailyProcessReportResponse;
import codehows.com.daehoint.dto.ProcessStockResponse;
import codehows.com.daehoint.entity.StandardInfo;
import codehows.com.daehoint.repository.StandardInfoRepo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelServiceImplProcess {

    private final ResourceLoader resourceLoader;

    private final StandardInfoRepo standardInfoRepo;
    private final DailyProcessReportService dailyProcessReportService;

    public void excelDownload(HttpServletResponse response, String filePath, String category, boolean isSnapShot, Long id) throws IOException {
        try (InputStream inputStream = resourceLoader.getResource(filePath).getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            // 1. 템플릿에서 Sheet 가져오기
            Sheet sheet = workbook.getSheetAt(0);

            DailyProcessReportResponse processReportResponse = null;
            // 2. 데이터 생성 및 삽입
            if (id == null) {
                processReportResponse = dailyProcessReportService.getProcessProductionDailyReport(category);
            } else {
                processReportResponse = dailyProcessReportService.getProcessProductionDailyReportById(id);
            }

            addData(sheet, processReportResponse, workbook);

            // 4. 엑셀 시트에 데이터 채우기
            fillExcelSheet(sheet, provideExcelData(category));

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

    public ExcelData provideExcelData(String category) {
        ExcelData excelData = new ExcelData();
        StandardInfo standardInfo = standardInfoRepo.findById(1L).orElse(null);
        if (standardInfo == null) {
            return null;
        }
        switch (category) {
            case "ACCY" -> {
                excelData.addData("A4", "◆ 작성부서 : " + standardInfo.getACCYWritingDepartment());
                excelData.addData("A5", "◆ 작 성 자  : " + standardInfo.getACCYWriter());
            }
            case "CASE ASSY" -> {
                excelData.addData("A4", "◆ 작성부서 : " + standardInfo.getCASEWritingDepartment());
                excelData.addData("A5", "◆ 작 성 자  : " + standardInfo.getCASEWriter());
            }
            case "DIP ASSY" -> {
                excelData.addData("A4", "◆ 작성부서 : " + standardInfo.getDIPWritingDepartment());
                excelData.addData("A5", "◆ 작 성 자  : " + standardInfo.getDIPWriter());
            }
            case "IM ASSY" -> {
                excelData.addData("A4", "◆ 작성부서 : " + standardInfo.getIMWritingDepartment());
                excelData.addData("A5", "◆ 작 성 자  : " + standardInfo.getIMWriter());
            }
            case "MANUAL ASSY" -> {
                excelData.addData("A4", "◆ 작성부서 : " + standardInfo.getMANUALWritingDepartment());
                excelData.addData("a5", "◆ 작 성 자  : " + standardInfo.getMANUALWriter());
            }
            case "PACKING ASSY" -> {
                excelData.addData("A4", "◆ 작성부서 : " + standardInfo.getPACKINGWritingDepartment());
                excelData.addData("A5", "◆ 작 성 자  : " + standardInfo.getPACKINGWriter());
            }
            case "PCB ASSY" -> {
                excelData.addData("A4", "◆ 작성부서 : " + standardInfo.getPCBWritingDepartment());
                excelData.addData("A5", "◆ 작 성 자  : " + standardInfo.getPCBWriter());
            }
            case "SM ASSY" -> {
                excelData.addData("A4", "◆ 작성부서 : " + standardInfo.getSMWritingDepartment());
                excelData.addData("A5", "◆ 작 성 자  : " + standardInfo.getSMWriter());
            }
        }

        return excelData;
    }

    private void addData(Sheet sheet, DailyProcessReportResponse processReportResponse, Workbook workbook) {
        int rowIndex = 10;
        int no = 1;
        sheet.shiftRows(rowIndex, sheet.getLastRowNum(), processReportResponse.getProductionDataDTO().size());
        for (DailyProcessReportResponse.ProductionDataDTO productionDataDTO : processReportResponse.getProductionDataDTO()) {
            Row row = sheet.createRow(rowIndex++);
            updateCell(productionDataDTO, row, no, workbook, sheet);
            no++;
        }
        sumData(processReportResponse, rowIndex, sheet, workbook);

        rowIndex += 6;
        Row row = sheet.createRow(rowIndex++);
        updateCell(processReportResponse.getManInputManageDataDTO().get(0), row, workbook, sheet);

        rowIndex += 5;
        row = sheet.createRow(rowIndex++);
        updateCell(processReportResponse.getProductionCostDataDTO().get(0), row, workbook, sheet);

        rowIndex += 6;
        for (DailyProcessReportResponse.TechProblem techProblem : processReportResponse.getTechProblem()) {
            row = sheet.createRow(rowIndex++);
            updateCell(techProblem, row, no, workbook, sheet);
        }
        rowIndex += 6;
        for (DailyProcessReportResponse.StopRisks stopRisks : processReportResponse.getStopRisks()) {
            row = sheet.createRow(rowIndex++);
            updateCell(stopRisks, row, no, workbook, sheet);
        }
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

    private void updateCell(DailyProcessReportResponse.ProductionDataDTO dataDTO, Row row, int no, Workbook workbook, Sheet sheet) {
        // 셀에 스타일을 적용하여 값 설정
        setCellWithStyle(row, 0, no, workbook);  // 첫 번째 셀: 번호
        setMergedCellWithStyle(sheet, row, 1, 2, dataDTO.getLotNo(), workbook);  // 두 번째~세 번째 셀: Lot No
        setMergedCellWithStyle(sheet, row, 3, 4, dataDTO.getProductName(), workbook);  // 네 번째~다섯 번째 셀: 제품명
        setMergedCellWithStyle(sheet, row, 5, 6, dataDTO.getModelNo(), workbook);  // 여섯 번째~일곱 번째 셀: 모델 번호
        setMergedCellWithStyle(sheet, row, 7, 8, dataDTO.getSpecification(), workbook);  // 여덟 번째~아홉 번째 셀: 규격
        setCellWithStyle(row, 9, dataDTO.getUnit(), workbook);  // 열 번째 셀: 단위
        setCellWithStyle(row, 10, dataDTO.getPlannedQuantity(), workbook);  // 열한 번째 셀: 계획 수량
        setCellWithStyle(row, 11, dataDTO.getInputQuantity(), workbook);  // 열두 번째 셀: 입력 수량
        setCellWithStyle(row, 12, dataDTO.getDefectiveQuantity(), workbook);  // 열세 번째 셀: 불량 수량
        setCellWithStyle(row, 13, dataDTO.getDefectRate(), workbook);  // 열네 번째 셀: 불량률
        setCellWithStyle(row, 14, dataDTO.getCompletedQuantity(), workbook);  // 열다섯 번째 셀: 완료 수량
        setCellWithStyle(row, 15, dataDTO.getAchievementRate(), workbook);  // 열여섯 번째 셀: 달성률
        setCellWithStyle(row, 16, dataDTO.getWorkInProgressQuantity(), workbook);  // 열일곱 번째 셀: 재공 수량
        setCellWithStyle(row, 17, dataDTO.getMaterialCost(), workbook);  // 열여덟 번째 셀: 재료 비용
        setCellWithStyle(row, 18, dataDTO.getManHours(), workbook);  // 열아홉 번째 셀: 작업 시간
        setCellWithStyle(row, 19, dataDTO.getProcessingCost(), workbook);  // 스무 번째 셀: 처리 비용
        setCellWithStyle(row, 20, dataDTO.getSubtotal(), workbook);  // 스물한 번째 셀: 소계
        setCellWithStyle(row, 21, dataDTO.getPricePerSet(), workbook);  // 스물두 번째 셀: 세트당 가격
        setCellWithStyle(row, 22, dataDTO.getTotalProduction(), workbook);  // 스물세 번째 셀: 총 생산량
        setCellWithStyle(row, 23, dataDTO.getPerformanceMaterialCost(), workbook);  // 스물네 번째 셀: 성과 재료 비용
        setCellWithStyle(row, 24, dataDTO.getPerformanceProcessingCost(), workbook);  // 스물다섯 번째 셀: 성과 처리 비용
        setCellWithStyle(row, 25, dataDTO.getTotalPerformanceAmount(), workbook);  // 스물여섯 번째 셀: 총 성과 금액
        setCellWithStyle(row, 26, dataDTO.getMonthlyCumulativeProduction(), workbook);  // 스물일곱 번째 셀: 월 누적 생산량
    }

    private void sumData(DailyProcessReportResponse dataDTO, int rowIndex, Sheet sheet, Workbook workbook) {
        // 새 행 생성
        Row row = sheet.createRow(rowIndex);

        // ProductionDataDTO 리스트 가져오기
        List<DailyProcessReportResponse.ProductionDataDTO> dtoList = dataDTO.getProductionDataDTO();

        // 각 항목의 합계 계산
        double sumPlannedQty = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getPlannedQuantity).sum();
        double sumInputQty = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getInputQuantity).sum();
        double sumDefectiveQty = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getDefectiveQuantity).sum();
        double sumDefectRate = sumPlannedQty > 0 ? (sumDefectiveQty / sumPlannedQty) * 100 : 0;
        double sumCompletedQty = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getCompletedQuantity).sum();
        double sumAchievementRate = sumPlannedQty > 0 ? (sumCompletedQty / sumPlannedQty) * 100 : 0;
        double sumWorkInProgressQty = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getWorkInProgressQuantity).sum();
        double sumMaterialCost = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getMaterialCost).sum();
        double sumManHours = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getManHours).sum();
        double sumProcessingCost = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getProcessingCost).sum();
        double sumSubtotal = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getSubtotal).sum();
        double sumPricePerSet = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getPricePerSet).sum();
        double sumTotalProduction = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getTotalProduction).sum();
        double sumPerformanceMaterialCost = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getPerformanceMaterialCost).sum();
        double sumPerformanceProcessingCost = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getPerformanceProcessingCost).sum();
        double sumTotalPerformanceAmount = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getTotalPerformanceAmount).sum();
        double sumMonthlyCumulativeProduction = dtoList.stream().mapToDouble(DailyProcessReportResponse.ProductionDataDTO::getMonthlyCumulativeProduction).sum();

        // 합계 데이터를 셀에 설정
        setCellWithStyle(row, 0, "합계", workbook);
        setCellWithStyle(row, 1, "", workbook);
        setCellWithStyle(row, 2, "", workbook);
        setCellWithStyle(row, 3, "", workbook);
        setCellWithStyle(row, 4, "", workbook);
        setCellWithStyle(row, 5, "", workbook);
        setCellWithStyle(row, 6, "", workbook);
        setCellWithStyle(row, 7, "", workbook);
        setCellWithStyle(row, 8, "", workbook);
        setCellWithStyle(row, 9, "", workbook);
        setCellWithStyle(row, 10, sumPlannedQty, workbook);  // 열한 번째 셀: 계획 수량
        setCellWithStyle(row, 11, sumInputQty, workbook);   // 열두 번째 셀: 입력 수량
        setCellWithStyle(row, 12, sumDefectiveQty, workbook);  // 열세 번째 셀: 불량 수량
        setCellWithStyle(row, 13, sumDefectRate, workbook);    // 열네 번째 셀: 불량률
        setCellWithStyle(row, 14, sumCompletedQty, workbook);  // 열다섯 번째 셀: 완료 수량
        setCellWithStyle(row, 15, sumAchievementRate, workbook);  // 열여섯 번째 셀: 달성률
        setCellWithStyle(row, 16, sumWorkInProgressQty, workbook);  // 열일곱 번째 셀: 재공 수량
        setCellWithStyle(row, 17, sumMaterialCost, workbook);  // 열여덟 번째 셀: 재료 비용
        setCellWithStyle(row, 18, sumManHours, workbook);  // 열아홉 번째 셀: 작업 시간
        setCellWithStyle(row, 19, sumProcessingCost, workbook);  // 스무 번째 셀: 처리 비용
        setCellWithStyle(row, 20, sumSubtotal, workbook);  // 스물한 번째 셀: 소계
        setCellWithStyle(row, 21, sumPricePerSet, workbook);  // 스물두 번째 셀: 세트당 가격
        setCellWithStyle(row, 22, sumTotalProduction, workbook);  // 스물세 번째 셀: 총 생산량
        setCellWithStyle(row, 23, sumPerformanceMaterialCost, workbook);  // 스물네 번째 셀: 성과 재료 비용
        setCellWithStyle(row, 24, sumPerformanceProcessingCost, workbook);  // 스물다섯 번째 셀: 성과 처리 비용
        setCellWithStyle(row, 25, sumTotalPerformanceAmount, workbook);  // 스물여섯 번째 셀: 총 성과 금액
        setCellWithStyle(row, 26, sumMonthlyCumulativeProduction, workbook);  // 스물일곱 번째 셀: 월 누적 생산량
    }


    private void updateCell(DailyProcessReportResponse.ManInputManageDataDTO dataDTO, Row row, Workbook workbook, Sheet sheet) {
        setCellWithStyle(row, 0, dataDTO.getAvailablePersonnel(), workbook);
        setCellWithStyle(row, 1, "", workbook);
        setCellWithStyle(row, 2, dataDTO.getStandardManHours(), workbook);
        setCellWithStyle(row, 3, dataDTO.getAvailableManHours(), workbook);
        setCellWithStyle(row, 4, dataDTO.getNonProductiveManHours(), workbook);
        setCellWithStyle(row, 5, dataDTO.getWorkloadManHours(), workbook);
        setCellWithStyle(row, 6, dataDTO.getStopManHours(), workbook);
        setCellWithStyle(row, 7, dataDTO.getReworkManHours(), workbook);
        setCellWithStyle(row, 8, dataDTO.getActualManHours(), workbook);
        setCellWithStyle(row, 9, dataDTO.getWorkingManHours(), workbook);
        setCellWithStyle(row, 10, dataDTO.getWorkEfficiency(), workbook);
        setCellWithStyle(row, 11, dataDTO.getActualEfficiency(), workbook);
        setCellWithStyle(row, 12, dataDTO.getLossRate(), workbook);
        setCellWithStyle(row, 13, dataDTO.getManHourInputRate(), workbook);
        setCellWithStyle(row, 14, dataDTO.getManHourOperationRate(), workbook);
        setCellWithStyle(row, 15, dataDTO.getOverallManHourEfficiency(), workbook);
        setCellWithStyle(row, 16, dataDTO.getOvertimeManHours(), workbook);
        setCellWithStyle(row, 17, dataDTO.getOvertimePersonnel(), workbook);
        setCellWithStyle(row, 18, dataDTO.getAdditionalInputRate(), workbook);
        setCellWithStyle(row, 19, dataDTO.getFluxOnTime(), workbook);
        setCellWithStyle(row, 20, dataDTO.getFluxOperatingTime(), workbook);
        setCellWithStyle(row, 21, dataDTO.getFluxOperatingRate(), workbook);
        setCellWithStyle(row, 22, dataDTO.getSolderingOnTime(), workbook);
        setCellWithStyle(row, 23, dataDTO.getSolderingOperatingTime(), workbook);
        setCellWithStyle(row, 24, dataDTO.getSolderingOperatingRate(), workbook);
        setCellWithStyle(row, 25, dataDTO.getRemarks(), workbook);
        setCellWithStyle(row, 26, "", workbook);
    }

    private void updateCell(DailyProcessReportResponse.ProductionCostDataDTO dataDTO, Row row, Workbook workbook, Sheet sheet) {
        setCellWithStyle(row, 0, dataDTO.getTotalProductionMaterialCostSum(), workbook);
        setCellWithStyle(row, 1, "", workbook);
        setCellWithStyle(row, 2, dataDTO.getProcessUsageSubMaterialSum(), workbook);
        setCellWithStyle(row, 3, dataDTO.getMaterialTotalSum(), workbook);
        setCellWithStyle(row, 4, dataDTO.getTotalProductionProcessingCostSum(), workbook);
        setCellWithStyle(row, 5, dataDTO.getProcessInOutsourcingWorkSum(), workbook);
        setCellWithStyle(row, 6, dataDTO.getProcessTotalSum(), workbook);
        setCellWithStyle(row, 7, dataDTO.getTotalProductionActualSum(), workbook);
        setCellWithStyle(row, 8, dataDTO.getDefectiveQuantity(), workbook);
        setCellWithStyle(row, 9, dataDTO.getDefectiveCost(), workbook);
        setCellWithStyle(row, 10, dataDTO.getStopAndNonproductiveHours(), workbook);
        setCellWithStyle(row, 11, dataDTO.getStopAndNonproductiveCost(), workbook);
        setCellWithStyle(row, 12, dataDTO.getReworkHours(), workbook);
        setCellWithStyle(row, 13, dataDTO.getReworkCost(), workbook);
        setCellWithStyle(row, 14, dataDTO.getTotalCost(), workbook);
        setCellWithStyle(row, 15, dataDTO.getManufacturingExpenseIndirect(), workbook);
        setCellWithStyle(row, 16, dataDTO.getManufacturingExpenseGeneralAdmin(), workbook);
        setCellWithStyle(row, 17, dataDTO.getManufacturingExpenseSellingAndAdmin(), workbook);
        setCellWithStyle(row, 18, dataDTO.getManufacturingExpenseDepreciationEtc(), workbook);
        setCellWithStyle(row, 19, dataDTO.getManufacturingExpenseTotal(), workbook);
        setCellWithStyle(row, 20, dataDTO.getEstimateCostTotal(), workbook);
        setCellWithStyle(row, 21, dataDTO.getProcessTotalProductionInputAmount(), workbook);
        setCellWithStyle(row, 22, dataDTO.getProcessTotalProductionActualProfit(), workbook);
        setCellWithStyle(row, 23, dataDTO.getProcessTotalProductionProfitRate(), workbook);
        setCellWithStyle(row, 24, dataDTO.getProcessTotalProductionLossRate(), workbook);
        setCellWithStyle(row, 25, dataDTO.getProcessTotalProductionMaterialRate(), workbook);
        setCellWithStyle(row, 26, dataDTO.getProcessTotalProductionProcessingRate(), workbook);
    }

    private void updateCell(DailyProcessReportResponse.TechProblem dataDTO, Row row, int no, Workbook workbook, Sheet sheet) {
        setCellWithStyle(row, 0, no, workbook);
        setCellWithStyle(row, 1, "", workbook);
        setCellWithStyle(row, 2, dataDTO.getCategory(), workbook);
        setCellWithStyle(row, 3, dataDTO.getDescription(), workbook);
        setCellWithStyle(row, 4, "", workbook);
        setCellWithStyle(row, 5, "", workbook);
        setCellWithStyle(row, 6, "", workbook);
        setCellWithStyle(row, 7, "", workbook);
        setCellWithStyle(row, 8, "", workbook);
        setCellWithStyle(row, 9, "", workbook);
        setCellWithStyle(row, 10, "", workbook);
        setCellWithStyle(row, 11, "", workbook);
        setCellWithStyle(row, 12, "", workbook);
        setCellWithStyle(row, 13, "", workbook);
        setCellWithStyle(row, 14, "", workbook);
        setCellWithStyle(row, 15, "", workbook);
        setCellWithStyle(row, 16, "", workbook);
        setCellWithStyle(row, 17, "", workbook);
        setCellWithStyle(row, 18, "", workbook);
        setCellWithStyle(row, 19, dataDTO.getPersonnel(), workbook);
        setCellWithStyle(row, 20, dataDTO.getManHours(), workbook);
        setCellWithStyle(row, 21, dataDTO.getCost(), workbook);
        setCellWithStyle(row, 22, dataDTO.getProgressResult(), workbook);
        setCellWithStyle(row, 23, dataDTO.getProcessResult(), workbook);
        setCellWithStyle(row, 24, dataDTO.getResponsibleDept1(), workbook);
        setCellWithStyle(row, 25, dataDTO.getResponsibleDept2(), workbook);
        setCellWithStyle(row, 26, dataDTO.getRemarks(), workbook);

    }

    private void updateCell(DailyProcessReportResponse.StopRisks dataDTO, Row row, int no, Workbook workbook, Sheet sheet) {
        setCellWithStyle(row, 0, no, workbook);
        setCellWithStyle(row, 1, "", workbook);
        setCellWithStyle(row, 2, dataDTO.getCategory(), workbook);
        setCellWithStyle(row, 3, dataDTO.getDescription(), workbook);
        setCellWithStyle(row, 4, "", workbook);
        setCellWithStyle(row, 5, "", workbook);
        setCellWithStyle(row, 6, "", workbook);
        setCellWithStyle(row, 7, "", workbook);
        setCellWithStyle(row, 8, "", workbook);
        setCellWithStyle(row, 9, "", workbook);
        setCellWithStyle(row, 10, "", workbook);
        setCellWithStyle(row, 11, "", workbook);
        setCellWithStyle(row, 12, "", workbook);
        setCellWithStyle(row, 13, "", workbook);
        setCellWithStyle(row, 14, "", workbook);
        setCellWithStyle(row, 15, "", workbook);
        setCellWithStyle(row, 16, "", workbook);
        setCellWithStyle(row, 17, "", workbook);
        setCellWithStyle(row, 18, "", workbook);
        setCellWithStyle(row, 19, dataDTO.getPersonnel(), workbook);
        setCellWithStyle(row, 20, dataDTO.getManHours(), workbook);
        setCellWithStyle(row, 21, dataDTO.getCost(), workbook);
        setCellWithStyle(row, 22, dataDTO.getProgressResult(), workbook);
        setCellWithStyle(row, 23, dataDTO.getProcessResult(), workbook);
        setCellWithStyle(row, 24, dataDTO.getResponsibleDept1(), workbook);
        setCellWithStyle(row, 25, dataDTO.getResponsibleDept2(), workbook);
        setCellWithStyle(row, 26, dataDTO.getRemarks(), workbook);
    }

    private void setMergedCellWithStyle(Sheet sheet, Row row, int startCol, int endCol, Object value, Workbook workbook) {
        CellRangeAddress cellRangeAddress = new CellRangeAddress(row.getRowNum(), row.getRowNum(), startCol, endCol);
        sheet.addMergedRegion(cellRangeAddress);

        for (int col = startCol; col <= endCol; col++) {
            Cell cell = row.createCell(col);
            setCellWithStyle(cell, value, workbook);
        }
    }

    private void setCellWithStyle(Cell cell, Object value, Workbook workbook) {
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
}
