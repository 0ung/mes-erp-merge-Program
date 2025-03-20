package codehows.com.daehoint.controller;

import codehows.com.daehoint.annotation.DownloadLogAnnotation;
import codehows.com.daehoint.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <b>ExcelController 클래스</b><br>
 * 엑셀 파일 다운로드를 처리하는 REST 컨트롤러입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - 다양한 데이터 유형에 대한 엑셀 파일 생성 및 다운로드.<br>
 * - 공정 생산일보, 일일 생산일보, LOSS 현황 등의 데이터를 엑셀 형식으로 제공.<br>
 * - 다운로드 요청 시 관련 로그를 기록하기 위해 `@DownloadLogAnnotation`을 사용.<br><br>
 *
 * <b>핵심 엔드포인트:</b><br>
 * 1. <b>`/download/dailyProduction`</b>:<br>
 * - 일일 생산일보 데이터를 엑셀로 다운로드.<br>
 * 2. <b>`/download/process/{processType}`</b>:<br>
 * - 공정별 생산일보 데이터를 엑셀로 다운로드.<br>
 * 3. <b>`/download/loss`</b>:<br>
 * - LOSS 현황 데이터를 엑셀로 다운로드.<br>
 * 4. <b>`/download/purchase`</b>:<br>
 * - 구매자재 현황 데이터를 엑셀로 다운로드.<br>
 * 5. <b>`/download/processStock`</b>:<br>
 * - 공정자재 현황 데이터를 엑셀로 다운로드.<br>
 * 6. <b>`/download/lt`</b>:<br>
 * - 장기불용자재 ISSUE 데이터를 엑셀로 다운로드.<br><br>
 *
 * <b>구성 요소:</b><br>
 * - <b>서비스 의존성:</b> `ExcelServiceImplMain`, `ExcelServiceImplProcess`, `ExcelServiceImplLoss`, `ExcelParchase`, `ExcelProcessStock`, `ExcelUnused`.<br>
 * - <b>애노테이션:</b> `@RestController`, `@RequestMapping`, `@DownloadLogAnnotation`.<br><br>
 *
 * <b>특징:</b><br>
 * - HTTP 응답에 직접 엑셀 파일을 작성하여 전송.<br>
 * - 공정별로 다른 템플릿 파일을 사용하여 엑셀 생성.<br><br>
 *
 * <b>예외 처리:</b><br>
 * - 파일 생성 중 예외 발생 시 HTTP 상태 코드 500 반환.<br>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/download")
public class ExcelController {

    private final ExcelServiceImplMain mainExcelService;
    private final ExcelServiceImplProcess processExcelService;
    private final ExcelServiceImplLoss lossExcelService;
    private final ExcelParchase excelParchase;
    private final ExcelProcessStock excelProcessStock;
    private final ExcelUnused excelUnused;

    //일일생산일보
    @DownloadLogAnnotation(fileName = "일일생산일보")
    @GetMapping("/dailyProduction")
    public void dailyProduction(HttpServletResponse response) throws IOException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String fileName = "dailyProduction" + LocalDateTime.now().format(formatter) + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename= \"" + fileName + "\";");
            response.setCharacterEncoding("UTF-8"); // 응답 인코딩 설정

            mainExcelService.excelDownload(response, "classpath:excel/MainProduction.xlsx", null);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @DownloadLogAnnotation(fileName = "일일생산일보")
    @GetMapping("/dailyProduction/{id}")
    public void dailyProductionById(HttpServletResponse response, @PathVariable(name = "id", required = false) Long id) throws IOException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String fileName = "dailyProduction" + LocalDateTime.now().format(formatter) + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename= \"" + fileName + "\";");
            response.setCharacterEncoding("UTF-8"); // 응답 인코딩 설정

            mainExcelService.excelDownload(response, "classpath:excel/MainProduction.xlsx", id);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    //공정 생산일보
    @DownloadLogAnnotation(fileName = "공정생산일보")
    @GetMapping("/process/{processType}/{id}")
    public void downloadProcess(@PathVariable("processType") String processType, HttpServletResponse response, @PathVariable(name = "id", required = false) Long id) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String fileName = processType + LocalDateTime.now().format(formatter) + ".xlsx";
            String filePath = null;
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename= \"" + fileName + "\";");
            switch (processType) {
                case "ACCY" -> {
                    filePath = "classpath:excel/accy.xlsx";
                }
                case "CASE ASSY" -> {
                    filePath = "classpath:excel/case.xlsx";
                }
                case "DIP ASSY" -> {
                    filePath = "classpath:excel/dip.xlsx";
                }
                case "IM ASSY" -> {
                    filePath = "classpath:excel/im.xlsx";
                }
                case "MANUAL ASSY" -> {
                    filePath = "classpath:excel/manual.xlsx";
                }
                case "PACKING ASSY" -> {
                    filePath = "classpath:excel/packing.xlsx";
                }
                case "PCB ASSY" -> {
                    filePath = "classpath:excel/pcb.xlsx";
                }
                case "SM ASSY" -> {
                    filePath = "classpath:excel/SmProcess.xlsx";
                }
            }
            processExcelService.excelDownload(response, filePath, processType, false,id);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    //공정 생산일보
    @DownloadLogAnnotation(fileName = "공정생산일보")
    @GetMapping("/process/{processType}")
    public void downloadProcess(@PathVariable("processType") String processType, HttpServletResponse response) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String fileName = processType + LocalDateTime.now().format(formatter) + ".xlsx";
            String filePath = null;
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename= \"" + fileName + "\";");
            switch (processType) {
                case "ACCY" -> {
                    filePath = "classpath:excel/accy.xlsx";
                }
                case "CASE ASSY" -> {
                    filePath = "classpath:excel/case.xlsx";
                }
                case "DIP ASSY" -> {
                    filePath = "classpath:excel/dip.xlsx";
                }
                case "IM ASSY" -> {
                    filePath = "classpath:excel/im.xlsx";
                }
                case "MANUAL ASSY" -> {
                    filePath = "classpath:excel/manual.xlsx";
                }
                case "PACKING ASSY" -> {
                    filePath = "classpath:excel/packing.xlsx";
                }
                case "PCB ASSY" -> {
                    filePath = "classpath:excel/pcb.xlsx";
                }
                case "SM ASSY" -> {
                    filePath = "classpath:excel/sm.xlsx";
                }
            }
            processExcelService.excelDownload(response, filePath, processType,false, null);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    //loss
    @DownloadLogAnnotation(fileName = "LOSS현황")
    @GetMapping("/loss")
    public void loss(HttpServletResponse response) throws IOException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String fileName = "loss" + LocalDateTime.now().format(formatter) + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename= \"" + fileName + "\";");

            lossExcelService.excelDownload(response, "classpath:excel/Loss.xlsx", false);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    //loss
    @DownloadLogAnnotation(fileName = "구매자재현황")
    @GetMapping("/purchase")
    public void purchase(HttpServletResponse response) throws IOException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String fileName = "purchase" + LocalDateTime.now().format(formatter) + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename= \"" + fileName + "\";");
            excelParchase.excelDownload(response, false);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @DownloadLogAnnotation(fileName = "공정자재현황")
    @GetMapping("/processStock")
    public void stock(HttpServletResponse response) throws IOException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String fileName = "processStock" + LocalDateTime.now().format(formatter) + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename= \"" + fileName + "\";");
            excelProcessStock.excelDownload(response, false);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @DownloadLogAnnotation(fileName = "장기불용자재 ISSUE")
    @GetMapping("/lt")
    public void ltUnused(HttpServletResponse response) throws IOException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String fileName = "ltUnused" + LocalDateTime.now().format(formatter) + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename= \"" + fileName + "\";");
            excelUnused.excelDownload(response);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
