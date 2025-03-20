package codehows.com.daehoint.service;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExcelService {

	//엑셀 파일마다 데이터 제공
	abstract ExcelData provideExcelData(int row,String category);

	//셀에 맞게 데이터 삽입
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

	//테이블에 데이터 추가하는 메서드
	public int addDataToTable(Sheet sheet, List<List<Object>> tableData, int startRow, Workbook workbook,
		Map<Integer, Integer> mergeCol) {

		if (mergeCol == null) {
			mergeCol = new HashMap<>();
		}
		// tableData가 비어있거나 첫 번째 리스트가 비어있는지 확인
		if (tableData == null || tableData.isEmpty() || tableData.get(0).isEmpty()) {
			return 0; // 데이터가 없을 경우 바로 종료
		}
		int rowSize = tableData.get(0).size();

		sheet.shiftRows(startRow, sheet.getLastRowNum(), rowSize);

		for (int rowIdx = 0; rowIdx < rowSize; rowIdx++) {
			Row row = sheet.getRow(startRow + rowIdx);
			if (row == null) {
				row = sheet.createRow(startRow + rowIdx);
			}

			for (int colIdx = 0; colIdx < tableData.size(); colIdx++) {

				//셀 병합
				if (mergeCol.containsKey(colIdx)) {
					CellRangeAddress cellAddresses = new CellRangeAddress( //첫행, 마지막행, 첫열, 마지막열
						startRow + rowIdx,
						startRow + rowIdx,
						colIdx,
						colIdx + 1
					);
					sheet.addMergedRegion(cellAddresses);
				}

				List<Object> currentList = tableData.get(colIdx);
				Cell cell = row.createCell(colIdx);

				if (currentList.get(rowIdx) instanceof Double) {
					cell.setCellValue((Double)currentList.get(rowIdx));
				} else if (currentList.get(rowIdx) instanceof String) {
					cell.setCellValue((String)currentList.get(rowIdx));
				}

				cellStyle(cell, workbook);
			}
		}

		return rowSize;
	}

	public void addDataToCol(Sheet sheet, List<Object> tableData, int startRow, Workbook workbook,
		Map<Integer, Integer> mergeCol, int colIdx) {

		Row row = sheet.getRow(startRow); //행 가져옴

		if (row == null) {
			row = sheet.createRow(startRow);
		}

		for (int col = 0; col < tableData.size(); col++) {
			Cell cell = row.createCell(col + colIdx);
			Object value = tableData.get(col);
			if (value instanceof String) {
				cell.setCellValue((String)value);
			} else if (value instanceof Double) {
				cell.setCellValue((Double)value);
			}
			cellStyle2(cell, workbook);

		}

	}

	//엑셀 다운로드
	void excelDownload(HttpServletResponse response, String filePath, boolean isSnapshot) throws IOException {

	}

	;

	void excelDownload(HttpServletResponse response, String filePath, String category, boolean isSnapshot) throws
		IOException {

	}

	;

	protected abstract void cellStyle(Cell cell, Workbook workbook);

	protected abstract void cellStyle2(Cell cell, Workbook workbook);

}
