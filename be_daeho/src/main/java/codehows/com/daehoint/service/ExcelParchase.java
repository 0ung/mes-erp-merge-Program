package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.PurchaseAndReceiptResponse;
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
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ExcelParchase {

    private final ResourceLoader resourceLoader;
    private final StandardInfoRepo standardInfoRepo;
    private final PurchaseService purchaseService;

    public void fillExcelSheet(Sheet sheet, ExcelData excelData) {
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

    public void excelDownload(HttpServletResponse response, boolean isSnapShot) throws IOException {
        try (InputStream inputStream = resourceLoader.getResource("classpath:excel/purchase.xlsx").getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            // 1. 템플릿에서 Sheet 가져오기
            Sheet sheet = workbook.getSheetAt(0);

            // 2. 데이터 생성 및 삽입
            PurchaseAndReceiptResponse stockList = purchaseService.getPurchaseAndReceiptResponse(isSnapShot);
            addData(sheet, stockList, workbook);

            // 3. ExcelData 객체 생성
            ExcelData excelData = provideExcelData(); // Provide Excel data after adding stock data

            // 4. 엑셀 시트에 데이터 채우기
            fillExcelSheet(sheet, excelData);

            for (int i = 0; i < 32; i++) {
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
            throw new RuntimeException("엑셀 다운로드 중 오류 발생", e);
        }
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


    // 필드를 누적하는 함수 정의
// 제네릭을 사용하여 어떤 DTO 클래스든 처리할 수 있도록 만든 accumulateField
    private <T> double accumulateField(List<T> list, java.util.function.Function<T, Double> getter) {
        return list.stream()
                .mapToDouble(data -> getter.apply(data) != null ? getter.apply(data) : 0.0)
                .sum();
    }


    // 합계 데이터를 생성하는 함수
    private PurchaseAndReceiptResponse.DailyMaterialCostDTO createTotalRow(List<PurchaseAndReceiptResponse.DailyMaterialCostDTO> list) {
        return PurchaseAndReceiptResponse.DailyMaterialCostDTO.builder()
                .category("합계")
                .dailyDirectTransactionAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getDailyDirectTransactionAmount))
                .weeklyDirectTransactionAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getWeeklyDirectTransactionAmount))
                .monthlyDirectTransactionAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getMonthlyDirectTransactionAmount))
                .dailySubcontractAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getDailySubcontractAmount))
                .weeklySubcontractAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getWeeklySubcontractAmount))
                .monthlySubcontractAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getMonthlySubcontractAmount))
                .dailyTotalAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getDailyTotalAmount))
                .weeklyTotalAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getWeeklyTotalAmount))
                .monthlyTotalAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getMonthlyTotalAmount))
                .dailyDirectReceiptAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getDailyDirectReceiptAmount))
                .weeklyDirectReceiptAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getWeeklyDirectReceiptAmount))
                .monthlyDirectReceiptAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getMonthlyDirectReceiptAmount))
                .dailySubcontractReceiptAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getDailySubcontractReceiptAmount))
                .weeklySubcontractReceiptAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getWeeklySubcontractReceiptAmount))
                .monthlySubcontractReceiptAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getMonthlySubcontractReceiptAmount))
                .dailyTotalReceiptAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getDailyTotalReceiptAmount))
                .weeklyTotalReceiptAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getWeeklyTotalReceiptAmount))
                .monthlyTotalReceiptAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getMonthlyTotalReceiptAmount))
                .dailyPendingDirectAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getDailyPendingDirectAmount))
                .weeklyPendingDirectAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getWeeklyPendingDirectAmount))
                .monthlyPendingDirectAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getMonthlyPendingDirectAmount))
                .dailyPendingSubcontractAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getDailyPendingSubcontractAmount))
                .weeklyPendingSubcontractAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getWeeklyPendingSubcontractAmount))
                .monthlyPendingSubcontractAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getMonthlyPendingSubcontractAmount))
                .dailyPendingTotalAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getDailyPendingTotalAmount))
                .weeklyPendingTotalAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getWeeklyPendingTotalAmount))
                .monthlyPendingTotalAmount(accumulateField(list, PurchaseAndReceiptResponse.DailyMaterialCostDTO::getMonthlyPendingTotalAmount))
                .build();
    }

    private PurchaseAndReceiptResponse.ModelPurchasePlanDTO createPurchaseTotalRow(List<PurchaseAndReceiptResponse.ModelPurchasePlanDTO> list) {
        return PurchaseAndReceiptResponse.ModelPurchasePlanDTO.builder()
                .category("합계")
                .monthlyPurchasePlan(accumulateField(list, PurchaseAndReceiptResponse.ModelPurchasePlanDTO::getMonthlyPurchasePlan))
                .materialCostRatio(accumulateField(list, PurchaseAndReceiptResponse.ModelPurchasePlanDTO::getMaterialCostRatio))
                .monthlyPurchasePlan(accumulateField(list, PurchaseAndReceiptResponse.ModelPurchasePlanDTO::getMonthlyPurchasePlan))
                .build();
    }

    private PurchaseAndReceiptResponse.ModelReceiptStatusDTO createReceiptTotal(List<PurchaseAndReceiptResponse.ModelReceiptStatusDTO> list) {
        return PurchaseAndReceiptResponse.ModelReceiptStatusDTO.builder()
                .category("합계")
                .directPurchaseReceipt(accumulateField(list, PurchaseAndReceiptResponse.ModelReceiptStatusDTO::getDirectPurchaseReceipt))
                .subcontractReceipt(accumulateField(list, PurchaseAndReceiptResponse.ModelReceiptStatusDTO::getSubcontractReceipt))
                .totalMaterialReceipt(accumulateField(list, PurchaseAndReceiptResponse.ModelReceiptStatusDTO::getTotalMaterialReceipt))
                .receiptRatioMonthly(accumulateField(list, PurchaseAndReceiptResponse.ModelReceiptStatusDTO::getReceiptRatioMonthly))
                .build();
    }

    private PurchaseAndReceiptResponse.StockStatusDTO createStockTotal(List<PurchaseAndReceiptResponse.StockStatusDTO> list) {
        return PurchaseAndReceiptResponse.StockStatusDTO.builder()
                .category("합계")
                .directPurchaseMaterial(accumulateField(list, PurchaseAndReceiptResponse.StockStatusDTO::getDirectPurchaseMaterial))
                .subcontractMaterial(accumulateField(list, PurchaseAndReceiptResponse.StockStatusDTO::getSubcontractMaterial))
                .totalMaterial(accumulateField(list, PurchaseAndReceiptResponse.StockStatusDTO::getTotalMaterial))
                .build();
    }

    private PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO createWarehouseTotal(List<PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO> list) {
        return PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO.builder()
                .category("합계")
                .wiringWaiting(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getWiringWaiting))
                .wiringInProcess(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getWiringInProcess))
                .wiringTotal(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getWiringTotal))
                .mechanismWaiting(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getMechanismWaiting))
                .mechanismInProcess(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getMechanismInProcess))
                .mechanismTotal(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getMechanismTotal))
                .packingWaiting(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getPackingWaiting))
                .packingInProcess(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getPackingInProcess))
                .packingTotal(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getPackingTotal))
                .subMaterialsWaiting(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getSubMaterialsWaiting))
                .subMaterialsInProcess(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getSubMaterialsInProcess))
                .subMaterialsTotal(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getSubMaterialsTotal))
                .otherWaiting(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getOtherWaiting))
                .otherInProcess(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getOtherInProcess))
                .otherTotal(accumulateField(list, PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::getOtherTotal))
                .build();
    }


    private void addData(Sheet sheet, PurchaseAndReceiptResponse response, Workbook workbook) {
        int startRow = 10; // 데이터 작성 시작 행

        response.getDailyMaterialCost().add(createTotalRow(response.getDailyMaterialCost()));
        response.getStockStatus().add(createStockTotal(response.getStockStatus()));
        if(response.getModelReceiptStatus() != null){
            response.getModelReceiptStatus().add(createReceiptTotal(response.getModelReceiptStatus()));

        }
        if(response.getModelPurchasePlan() != null){
            response.getModelPurchasePlan().add(createPurchaseTotalRow(response.getModelPurchasePlan()));
        }

        response.getWarehouseMaterialStatus().add(createWarehouseTotal(response.getWarehouseMaterialStatus()));

        //일일자재 금액 합계
        if (response.getDailyMaterialCost() != null && !response.getDailyMaterialCost().isEmpty()) {
            for (PurchaseAndReceiptResponse.DailyMaterialCostDTO data : response.getDailyMaterialCost()) {
                // 데이터 삽입 행 생성/가져오기
                Row dataRow = sheet.getRow(startRow);
                if (dataRow == null) {
                    dataRow = sheet.createRow(startRow);
                }
                // 각 셀에 데이터 삽입 (열 번호는 4부터 시작)
                setCellWithStyle(dataRow, 1, data.getCategory() != null ? data.getCategory() : "N/A", workbook); // 5열
                setCellWithStyle(dataRow, 2, data.getMonthlySalesPlan() != null ? data.getMonthlySalesPlan() : 0.0, workbook); // 6열
                setCellWithStyle(dataRow, 3, data.getMonthlyPurchasePlan() != null ? data.getMonthlyPurchasePlan() : 0.0, workbook); // 7열
                setCellWithStyle(dataRow, 4, data.getDailyDirectTransactionAmount() != null ? data.getDailyDirectTransactionAmount() : 0.0, workbook); // 8열
                setCellWithStyle(dataRow, 5, data.getWeeklyDirectTransactionAmount() != null ? data.getWeeklyDirectTransactionAmount() : 0.0, workbook); // 9열
                setCellWithStyle(dataRow, 6, data.getMonthlyDirectTransactionAmount() != null ? data.getMonthlyDirectTransactionAmount() : 0.0, workbook); // 10열
                setCellWithStyle(dataRow, 7, data.getDailySubcontractAmount() != null ? data.getDailySubcontractAmount() : 0.0, workbook); // 11열
                setCellWithStyle(dataRow, 8, data.getWeeklySubcontractAmount() != null ? data.getWeeklySubcontractAmount() : 0.0, workbook); // 12열
                setCellWithStyle(dataRow, 9, data.getMonthlySubcontractAmount() != null ? data.getMonthlySubcontractAmount() : 0.0, workbook); // 13열
                setCellWithStyle(dataRow, 10, data.getDailyTotalAmount() != null ? data.getDailyTotalAmount() : 0.0, workbook); // 14열
                setCellWithStyle(dataRow, 11, data.getWeeklyTotalAmount() != null ? data.getWeeklyTotalAmount() : 0.0, workbook); // 15열
                setCellWithStyle(dataRow, 12, data.getMonthlyTotalAmount() != null ? data.getMonthlyTotalAmount() : 0.0, workbook); // 16열
                setCellWithStyle(dataRow, 13, data.getDailyDirectReceiptAmount() != null ? data.getDailyDirectReceiptAmount() : 0.0, workbook); // 17열
                setCellWithStyle(dataRow, 14, data.getWeeklyDirectReceiptAmount() != null ? data.getWeeklyDirectReceiptAmount() : 0.0, workbook); // 18열
                setCellWithStyle(dataRow, 15, data.getMonthlyDirectReceiptAmount() != null ? data.getMonthlyDirectReceiptAmount() : 0.0, workbook); // 19열
                setCellWithStyle(dataRow, 16, data.getDailySubcontractReceiptAmount() != null ? data.getDailySubcontractReceiptAmount() : 0.0, workbook); // 20열
                setCellWithStyle(dataRow, 17, data.getWeeklySubcontractReceiptAmount() != null ? data.getWeeklySubcontractReceiptAmount() : 0.0, workbook); // 21열
                setCellWithStyle(dataRow, 18, data.getMonthlySubcontractReceiptAmount() != null ? data.getMonthlySubcontractReceiptAmount() : 0.0, workbook); // 22열
                setCellWithStyle(dataRow, 19, data.getDailyTotalReceiptAmount() != null ? data.getDailyTotalReceiptAmount() : 0.0, workbook); // 23열
                setCellWithStyle(dataRow, 20, data.getWeeklyTotalReceiptAmount() != null ? data.getWeeklyTotalReceiptAmount() : 0.0, workbook); // 24열
                setCellWithStyle(dataRow, 21, data.getMonthlyTotalReceiptAmount() != null ? data.getMonthlyTotalReceiptAmount() : 0.0, workbook); // 25열
                setCellWithStyle(dataRow, 22, data.getDailyPendingDirectAmount() != null ? data.getDailyPendingDirectAmount() : 0.0, workbook); // 26열
                setCellWithStyle(dataRow, 23, data.getWeeklyPendingDirectAmount() != null ? data.getWeeklyPendingDirectAmount() : 0.0, workbook); // 27열
                setCellWithStyle(dataRow, 24, data.getMonthlyPendingDirectAmount() != null ? data.getMonthlyPendingDirectAmount() : 0.0, workbook); // 28열
                setCellWithStyle(dataRow, 25, data.getDailyPendingSubcontractAmount() != null ? data.getDailyPendingSubcontractAmount() : 0.0, workbook); // 29열
                setCellWithStyle(dataRow, 26, data.getWeeklyPendingSubcontractAmount() != null ? data.getWeeklyPendingSubcontractAmount() : 0.0, workbook); // 30열
                setCellWithStyle(dataRow, 27, data.getMonthlyPendingSubcontractAmount() != null ? data.getMonthlyPendingSubcontractAmount() : 0.0, workbook); // 31열
                setCellWithStyle(dataRow, 28, data.getDailyPendingTotalAmount() != null ? data.getDailyPendingTotalAmount() : 0.0, workbook); // 32열
                setCellWithStyle(dataRow, 29, data.getWeeklyPendingTotalAmount() != null ? data.getWeeklyPendingTotalAmount() : 0.0, workbook); // 33열
                setCellWithStyle(dataRow, 30, data.getMonthlyPendingTotalAmount() != null ? data.getMonthlyPendingTotalAmount() : 0.0, workbook); // 34열

                startRow++; // 다음 행으로 이동
            }
        }
        startRow = 20;


        // 2. 재고현황
        if (response.getStockStatus() != null && !response.getStockStatus().isEmpty()) {
            for (PurchaseAndReceiptResponse.StockStatusDTO data : response.getStockStatus()) {
                // 데이터 삽입 행 생성/가져오기
                Row dataRow = sheet.getRow(startRow);
                if (dataRow == null) {
                    dataRow = sheet.createRow(startRow);
                }

                // 각 셀에 데이터 삽입 (열 번호는 4부터 시작)
                setCellWithStyle(dataRow, 1, data.getCategory() != null ? data.getCategory() : "N/A", workbook); // 5열
                setCellWithStyle(dataRow, 2, data.getDirectPurchaseMaterial() != null ? data.getDirectPurchaseMaterial() : 0.0, workbook); // 6열
                setCellWithStyle(dataRow, 3, data.getSubcontractMaterial() != null ? data.getSubcontractMaterial() : 0.0, workbook); // 7열
                setCellWithStyle(dataRow, 4, data.getTotalMaterial() != null ? data.getTotalMaterial() : 0.0, workbook); // 8열
                startRow++; // 다음 행으로 이동
            }
        }

        startRow = 31;
        if (response.getModelPurchasePlan() != null && !response.getModelPurchasePlan().isEmpty()) {
            for (PurchaseAndReceiptResponse.ModelPurchasePlanDTO data : response.getModelPurchasePlan()) {
                Row dataRow = sheet.getRow(startRow);
                if (dataRow == null) {
                    dataRow = sheet.createRow(startRow);
                }

                // 각 열에 데이터 삽입
                setCellWithStyle(dataRow, 2, data.getCategory() != null ? data.getCategory() : "N/A", workbook);
                setCellWithStyle(dataRow, 3, data.getMonthlySalesPlan() != null ? data.getMonthlySalesPlan() : 0.0, workbook);
                setCellWithStyle(dataRow, 4, data.getMaterialCostRatio() != null ? data.getMaterialCostRatio() : 0.0, workbook);
                setCellWithStyle(dataRow, 5, data.getMonthlyPurchasePlan() != null ? data.getMonthlyPurchasePlan() : 0.0, workbook);
                setCellWithStyle(dataRow, 6, data.getRemarks() != null ? data.getRemarks() : "", workbook);

                startRow++;
            }
        }
        startRow = 31;
        if (response.getModelReceiptStatus() != null && !response.getModelReceiptStatus().isEmpty()) {
            for (PurchaseAndReceiptResponse.ModelReceiptStatusDTO data : response.getModelReceiptStatus()) {
                Row dataRow = sheet.getRow(startRow);
                if (dataRow == null) {
                    dataRow = sheet.createRow(startRow);
                }

                setCellWithStyle(dataRow, 9, data.getCategory() != null ? data.getCategory() : "N/A", workbook);
                setCellWithStyle(dataRow, 10, data.getDirectPurchaseReceipt() != null ? data.getDirectPurchaseReceipt() : 0.0, workbook);
                setCellWithStyle(dataRow, 11, data.getSubcontractReceipt() != null ? data.getSubcontractReceipt() : 0.0, workbook);
                setCellWithStyle(dataRow, 12, data.getTotalMaterialReceipt() != null ? data.getTotalMaterialReceipt() : 0.0, workbook);
                setCellWithStyle(dataRow, 13, data.getReceiptRatioMonthly() != null ? data.getReceiptRatioMonthly() : 0.0, workbook);
                setCellWithStyle(dataRow, 14, data.getRemarks() != null ? data.getRemarks() : "", workbook);

                startRow++;
            }
        }
        startRow = 40;
        if (response.getWarehouseMaterialStatus() != null && !response.getWarehouseMaterialStatus().isEmpty()) {
            for (PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO data : response.getWarehouseMaterialStatus()) {
                Row dataRow = sheet.getRow(startRow);
                if (dataRow == null) {
                    dataRow = sheet.createRow(startRow);
                }

                // 셀에 데이터 삽입 및 스타일 적용
                setCellWithStyle(dataRow, 1, data.getCategory() != null ? data.getCategory() : "N/A", workbook); // Category
                setCellWithStyle(dataRow, 2, data.getWiringWaiting() != null ? data.getWiringWaiting() : 0.0, workbook); // Wiring Waiting
                setCellWithStyle(dataRow, 3, data.getWiringInProcess() != null ? data.getWiringInProcess() : 0.0, workbook); // Wiring In Process
                setCellWithStyle(dataRow, 4, data.getWiringTotal() != null ? data.getWiringTotal() : 0.0, workbook); // Wiring Total
                setCellWithStyle(dataRow, 5, data.getMechanismWaiting() != null ? data.getMechanismWaiting() : 0.0, workbook); // Mechanism Waiting
                setCellWithStyle(dataRow, 6, data.getMechanismInProcess() != null ? data.getMechanismInProcess() : 0.0, workbook); // Mechanism In Process
                setCellWithStyle(dataRow, 7, data.getMechanismTotal() != null ? data.getMechanismTotal() : 0.0, workbook); // Mechanism Total
                setCellWithStyle(dataRow, 8, data.getPackingWaiting() != null ? data.getPackingWaiting() : 0.0, workbook); // Packing Waiting
                setCellWithStyle(dataRow, 9, data.getPackingInProcess() != null ? data.getPackingInProcess() : 0.0, workbook); // Packing In Process
                setCellWithStyle(dataRow, 10, data.getPackingTotal() != null ? data.getPackingTotal() : 0.0, workbook); // Packing Total
                setCellWithStyle(dataRow, 11, data.getSubMaterialsWaiting() != null ? data.getSubMaterialsWaiting() : 0.0, workbook); // Submaterials Waiting
                setCellWithStyle(dataRow, 12, data.getSubMaterialsInProcess() != null ? data.getSubMaterialsInProcess() : 0.0, workbook); // Submaterials In Process
                setCellWithStyle(dataRow, 13, data.getSubMaterialsTotal() != null ? data.getSubMaterialsTotal() : 0.0, workbook); // Submaterials Total
                setCellWithStyle(dataRow, 14, data.getOtherWaiting() != null ? data.getOtherWaiting() : 0.0, workbook); // Other Waiting
                setCellWithStyle(dataRow, 15, data.getOtherInProcess() != null ? data.getOtherInProcess() : 0.0, workbook); // Other In Process
                setCellWithStyle(dataRow, 16, data.getOtherTotal() != null ? data.getOtherTotal() : 0.0, workbook); // Other Total
                startRow++;
            }
        }
    }

    public ExcelData provideExcelData() {
        ExcelData excelData = new ExcelData();
        StandardInfo standardInfo = standardInfoRepo.findById(1L).orElse(null);
        if (standardInfo == null) {
            return null;
        }
        excelData.addData("B3", "◆ 작성부서 : "+ standardInfo.getPurchaseWritingDepartment());
        excelData.addData("B4", "◆ 작 성 자 : "+ standardInfo.getPurchaseWriter());
        excelData.addData("B1", calcDate());
        return excelData;
    }

    public String calcDate() {
        // 기준 날짜
        LocalDate today = LocalDate.now();

        // 한국 기준 주차 계산 (일~토 기준)
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        int weekOfMonth = today.get(weekFields.weekOfMonth());
        int month = today.getMonthValue();

        // 결과 문자열 생성
        return month + "월 " + weekOfMonth + "주 구매자재 발주 및 입고 현황";
    }
}
