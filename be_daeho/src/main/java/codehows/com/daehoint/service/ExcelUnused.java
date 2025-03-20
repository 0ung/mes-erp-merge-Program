package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.sync.MaterialIssueListDTO;
import codehows.com.daehoint.entity.StandardInfo;
import codehows.com.daehoint.repository.StandardInfoRepo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelUnused {

    private final ResourceLoader resourceLoader;
    private final StandardInfoRepo standardInfoRepo;
    private final LtUnusedMaterialService ltUnusedMaterialService;

    public void excelDownload(HttpServletResponse response) throws IOException {
        try (InputStream inputStream = resourceLoader.getResource("classpath:excel/unused.xlsx").getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            List<MaterialIssueListDTO> list = ltUnusedMaterialService.getIssueListDTOS(false);

            addData(sheet, list, workbook);

            fillExcelSheet(sheet, provideExcelData());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"ltUnused.xlsx\"");
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new IOException();
        }
    }

    private void addData(Sheet sheet, List<MaterialIssueListDTO> issueListDTOS, Workbook workbook) {
        int rowIndex = 10;
        int no = 0;
        int lastIndex = issueListDTOS.size() - 1;

        for (MaterialIssueListDTO issueListDTO : issueListDTOS) {
            Row row = sheet.createRow(rowIndex);
            updateCell(issueListDTO, row, no, workbook);
            rowIndex++;
            no++;

            // 마지막 반복이 아니면 행을 이동
            if (no <= lastIndex) {
                sheet.shiftRows(rowIndex, sheet.getLastRowNum(), 1);
            }
        }
        sumData(issueListDTOS, rowIndex, sheet, workbook);
    }

    private ExcelData provideExcelData() {
        ExcelData excelData = new ExcelData();
        StandardInfo standardInfo = standardInfoRepo.findById(1L).orElse(null);
        if (standardInfo == null) {
            return null;
        }
        excelData.addData("A4", "◆ 작성부서 : " + standardInfo.getLtWritingDepartment());
        excelData.addData("A5", "◆ 작 성 자 : " + standardInfo.getLtWriter());
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

    private void updateCell(MaterialIssueListDTO materialIssueListDTO, Row row, int no, Workbook workbook) {
        setCellWithStyle(row, 1, no, workbook);
        setCellWithStyle(row, 2, materialIssueListDTO.getItemCd(), workbook);
        setCellWithStyle(row, 3, materialIssueListDTO.getItemName(), workbook);
        setCellWithStyle(row, 4, materialIssueListDTO.getDateOfOccurrence(), workbook);
        setCellWithStyle(row, 5, materialIssueListDTO.getReason(), workbook);
        setCellWithStyle(row, 6, materialIssueListDTO.getApplyContents(), workbook);
        setCellWithStyle(row, 7, materialIssueListDTO.getApplyContents(), workbook);
        setCellWithStyle(row, 8, materialIssueListDTO.getReponsibilityClassification(), workbook);
        setCellWithStyle(row, 9, materialIssueListDTO.getReponsibilityClassification(), workbook);
        setCellWithStyle(row, 10, materialIssueListDTO.getStockQTY(), workbook);
        setCellWithStyle(row, 11, materialIssueListDTO.getBeforeOccurrenceUsingQTY(), workbook);
        setCellWithStyle(row, 12, materialIssueListDTO.getAfterOccurrenceUsingQTY(), workbook);
        setCellWithStyle(row, 13, materialIssueListDTO.getLongTermInventory(), workbook);
        setCellWithStyle(row, 14, materialIssueListDTO.getInsolvencyStock(), workbook);
        setCellWithStyle(row, 15, materialIssueListDTO.getResale(), workbook);
        setCellWithStyle(row, 16, materialIssueListDTO.getDisuse(), workbook);
    }

    private void sumData(List<MaterialIssueListDTO> issueListDTOS, int rowIndex, Sheet sheet, Workbook workbook) {
        Row row = sheet.createRow(rowIndex);
        double stockQty = issueListDTOS.stream().mapToDouble(MaterialIssueListDTO::getStockQTY).sum();
        double before = issueListDTOS.stream().mapToDouble(MaterialIssueListDTO::getBeforeOccurrenceUsingQTY).sum();
        double after = issueListDTOS.stream().mapToDouble(MaterialIssueListDTO::getAfterOccurrenceUsingQTY).sum();
        double longTerm = issueListDTOS.stream().mapToDouble(MaterialIssueListDTO::getLongTermInventory).sum();
        double insolvency = issueListDTOS.stream().mapToDouble(MaterialIssueListDTO::getInsolvencyStock).sum();
        double resale = issueListDTOS.stream().mapToDouble(MaterialIssueListDTO::getResale).sum();
        double disuse = issueListDTOS.stream().mapToDouble(MaterialIssueListDTO::getDisuse).sum();
        setCellWithStyle(row, 1, "합계", workbook);
        setCellWithStyle(row, 2, "", workbook);
        setCellWithStyle(row, 3, "", workbook);
        setCellWithStyle(row, 4, "", workbook);
        setCellWithStyle(row, 5, "", workbook);
        setCellWithStyle(row, 6, "", workbook);
        setCellWithStyle(row, 7, "", workbook);
        setCellWithStyle(row, 8, "", workbook);
        setCellWithStyle(row, 9, "", workbook);
        setCellWithStyle(row, 10, stockQty, workbook);
        setCellWithStyle(row, 11, before, workbook);
        setCellWithStyle(row, 12, after, workbook);
        setCellWithStyle(row, 13, longTerm, workbook);
        setCellWithStyle(row, 14, insolvency, workbook);
        setCellWithStyle(row, 15, resale, workbook);
        setCellWithStyle(row, 16, disuse, workbook);
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
