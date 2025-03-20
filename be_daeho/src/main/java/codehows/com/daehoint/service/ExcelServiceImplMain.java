package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.MainProductionReportResponse;
import codehows.com.daehoint.dto.StandardInfoResponse;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelServiceImplMain {

    private final ResourceLoader resourceLoader;
    private final StandardInfoRepo standardInfoRepo;
    private final MainProductionReportService mainProductionReportService;
    private final LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

    public void excelDownload(HttpServletResponse response, String filePath, Long id) throws IOException {
        try (InputStream inputStream = resourceLoader.getResource(filePath).getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            MainProductionReportResponse report = (id == null)
                    ? mainProductionReportService.getMainProductionReportResponse()
                    : mainProductionReportService.searchByIdDailyMainReport(id);

            if (report == null) {
                throw new IllegalArgumentException("보고서 데이터가 null입니다.");
            }

            addDtoDataToTable(sheet, report, workbook);
            fillExcelSheet(sheet, provideExcelData());

            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 다운로드 중 오류 발생", e);
        }
    }

    private ExcelData provideExcelData() {
        ExcelData excelData = new ExcelData();
        StandardInfo standardInfo = standardInfoRepo.findById(1L).orElse(null);
        StandardInfoResponse response = StandardInfoResponse.builder()
                .mainWritingDepartment(standardInfo == null ? "데이터 없음" : standardInfo.getMainWritingDepartment())
                .mainWriter(standardInfo == null ? "데이터 없음" : standardInfo.getMainWriter())
                .power(100.0)
                .build();

        excelData.addData("A5", "◆ 작성부서 : " + response.getMainWritingDepartment());
        excelData.addData("A6", "◆ 작 성 자 : " + response.getMainWriter());
        return excelData;
    }

    private void addDtoDataToTable(Sheet sheet, MainProductionReportResponse report, Workbook workbook) {
        int startRow = 13;

        List<Object> attendanceStatusData = AttendanceStatusDTOToTable(report.getAttendanceStatusDataDTO().get(0));
        addDataToCol(sheet, attendanceStatusData, startRow, workbook);

        List<Object> manPowerData = ManPowerInputDTOToTable(report.getManPowerInputManageDataDTO().get(0));
        addDataToCol(sheet, manPowerData, startRow + 7, workbook);

        List<Object> costAnalyzeData = CostAnalyzeDTOToTable(report.getCostAnalyzeDataDTO().get(0));
        addDataToCol(sheet, costAnalyzeData, startRow + 14, workbook);

        List<Object> manufacturingCostData = ManufacturingCostDTOToTable(report.getManufacturingCostAnalysisDataDTO().get(0));
        addDataToCol(sheet, manufacturingCostData, startRow + 20, workbook);
    }

    private List<Object> AttendanceStatusDTOToTable(MainProductionReportResponse.AttendanceStatusDataDTO dto) {
        return List.of(
                dto.getProductionPersonnel(),
                dto.getDirectPersonnel(),
                dto.getSupportPersonnel(),
                dto.getEtcPersonnel(),
                dto.getTotalPersonnel(),
                dto.getIndirectManHours(),
                dto.getDirectManHours(),
                dto.getTotalManHours(),
                dto.getDirectYearlyLeavePersonnel(),
                dto.getDirectYearlyLeaveHours(),
                dto.getDirectPartTimePersonnel(),
                dto.getDirectPartTimeHours(),
                dto.getDirectEtcPersonnel(),
                dto.getDirectEtcPersonnelTime(),
                dto.getDirectTotalPersonnel(),
                dto.getDirectTotalPersonnelTime(),
                dto.getSubYearlyLeavePersonnel(),
                dto.getSubYearlyLeaveHours(),
                dto.getSubPartTimePersonnel(),
                dto.getSubPartTimeHours(),
                dto.getSubTotalPersonnel(),
                dto.getSubTotalHours(),
                dto.getDirectMan(),
                dto.getDirectTime(),
                dto.getSubMan(),
                dto.getSubTime(),
                dto.getEtcMan(),
                dto.getEtcTime(),
                dto.getTotalMan(),
                dto.getTotalTime(),
                dto.getAttendanceRemark()
        );
    }

    private List<Object> ManPowerInputDTOToTable(MainProductionReportResponse.ManPowerInputManageDataDTO dto) {
        return List.of(
                dto.getAvailablePersonnel(),
                dto.getAvailableManHours(),
                "",
                dto.getStandardManHours(),
                "",
                dto.getNonProductiveManHours(),
                "",
                dto.getLoadManHours(),
                "",
                dto.getStoppedManHours(),
                "",
                dto.getReworkManHours(), "",
                dto.getActualManHours(),
                "",
                dto.getWorkingManHours(),
                "",
                dto.getWorkEfficiency(),
                dto.getActualEfficiency(),
                dto.getLossRate(),
                dto.getManHourInputRate(),
                dto.getManHourOperationRate(),
                dto.getTotalEfficiency(),
                dto.getSpecialSupportPersonnel(),
                dto.getSpecialSupportManHours(),
                dto.getAdditionalInputRate(),
                dto.getFluxEquipmentRunningTime(),
                dto.getFluxEquipmentRunningRate(),
                dto.getSolderingEquipmentRunningTime(),
                dto.getSolderingEquipmentRunningRate(),
                dto.getManPowerRemark()
        );
    }

    private List<Object> CostAnalyzeDTOToTable(MainProductionReportResponse.CostAnalyzeDataDTO dto) {
        return List.of(
                dto.getRawMaterialCost(),
                "",
                dto.getSubsidiaryMaterialCost(),
                "",
                dto.getTotalMaterialCost(),
                "",
                dto.getProductionCost(),
                "",
                dto.getSmImCost(),
                "",
                dto.getExternalProcessingCost(),
                "",
                dto.getTotalProductionCost(),
                "",
                dto.getTotalProductionAmount(),
                "",
                "",
                dto.getLossHandlingCnt(),
                dto.getLossHandlingCost(),
                "",
                dto.getNonProductiveTimeHour(),
                dto.getNonProductiveTimeCost(),
                "",
                dto.getReworkTimeCnt(),
                dto.getReworkTimeCost(),
                "",
                dto.getTotalLossCost(),
                "",
                dto.getTotalProductionDirectInputCost(),
                "",
                ""
        );
    }

    private List<Object> ManufacturingCostDTOToTable(MainProductionReportResponse.ManufacturingCostAnalysisDataDTO dto) {
        return List.of(
                dto.getDirectPersonnelCost(),
                "",
                dto.getIndirectPersonnelCost(),
                "",
                dto.getGeneralManagementCost(),
                "",
                dto.getSalesCost(),
                "",
                dto.getEquipmentDepreciationCost(),
                "",
                dto.getOtherCost(),
                "",
                dto.getTotalManufacturingCost(),
                "",
                dto.getTotalProductCost(),
                "",
                "",
                "",
                dto.getTotalEstimateCost(),
                "",
                "",
                dto.getTotalProfit(),
                "",
                "",
                dto.getNetProfit(),
                "",
                dto.getInvestCost(),
                "",
                dto.getTotalExpenditure(),
                "",
                dto.getCostRemark()
        );
    }

    private void addDataToCol(Sheet sheet, List<Object> data, int startRow, Workbook workbook) {
        Row row = sheet.getRow(startRow); // 특정 행 가져오기
        if (row == null) {
            row = sheet.createRow(startRow); // 행이 없으면 새로 생성
        }
        for (int colIdx = 0; colIdx < data.size(); colIdx++) {
            Cell cell = row.createCell(colIdx); // 열 방향으로 셀 생성
            setCellWithStyle(cell, data.get(colIdx), workbook); // 데이터 및 스타일 적용
        }
    }


    private void setCellWithStyle(Cell cell, Object value, Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(false);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        if (value instanceof Number) {
            DataFormat format = workbook.createDataFormat();
            cellStyle.setDataFormat(format.getFormat("#,##0"));
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else {
            cell.setCellValue("");
        }

        cell.setCellStyle(cellStyle);
    }

    private void fillExcelSheet(Sheet sheet, ExcelData excelData) {
        for (String cellAddress : excelData.getDataSet()) {
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
