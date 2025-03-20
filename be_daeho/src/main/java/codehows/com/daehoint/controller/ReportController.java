package codehows.com.daehoint.controller;

import codehows.com.daehoint.annotation.AccessLogAnnotation;
import codehows.com.daehoint.dto.AdditionalDataRequest;
import codehows.com.daehoint.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * <b>ReportController 클래스</b><br>
 * 생산 및 공정 데이터 관련 보고서를 처리하는 REST 컨트롤러입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - 메인 생산일보, 공정 생산일보, LOSS 현황, 공정 자재, 실적 및 생산 흐름, 장기 불용 자재, 구매 자재 등의 데이터 조회 및 검색.<br>
 * - 관련 데이터의 업데이트 처리.<br>
 * - 스냅샷 데이터를 기반으로 한 보고서 제공.<br><br>
 *
 * <b>핵심 엔드포인트:</b><br>
 * - <b>메인 생산일보:</b><br>
 * - `GET /report/main`: 메인 생산일보 조회.<br>
 * - `GET /report/main/{startDate}/{endDate}`: 날짜 범위 내 메인 생산일보 검색.<br>
 * - `GET /report/main/{id}`: ID로 메인 생산일보 검색.<br>
 * - <b>공정 생산일보:</b><br>
 * - `GET /report/process/{category}`: 특정 공정 카테고리의 생산일보 조회.<br>
 * - `GET /report/process/{category}/{startDate}/{endDate}`: 날짜 범위 및 카테고리 기준으로 공정 생산일보 검색.<br>
 * - `GET /report/process/detail/{id}`: ID로 공정 생산일보 세부 데이터 조회.<br>
 * - `PUT /report/process`: 공정 데이터 업데이트.<br>
 * - <b>LOSS 현황:</b><br>
 * - `GET /report/loss`: LOSS 현황 조회.<br>
 * - `GET /report/loss/{startDate}/{endDate}`: 날짜 범위 및 LOSS 사유로 LOSS 데이터 검색.<br>
 * - <b>공정 자재:</b><br>
 * - `GET /report/stock`: 공정 자재 데이터 조회.<br>
 * - `GET /report/stock/{startDate}/{endDate}`: 날짜 범위 내 공정 자재 데이터 검색.<br>
 * - `GET /report/stock/{id}`: ID로 공정 자재 데이터 검색.<br>
 * - <b>실적 및 생산 흐름:</b><br>
 * - `GET /report/performance`: 실적 데이터 조회.<br>
 * - `GET /report/performance/{startDate}/{endDate}`: 날짜 범위 및 모델/공정 조건으로 실적 데이터 검색.<br>
 * - `GET /report/flow`: 생산 흐름 데이터 조회.<br>
 * - `GET /report/flow/{startDate}/{endDate}`: 날짜 범위 및 모델/부품 번호 조건으로 생산 흐름 데이터 검색.<br>
 * - <b>장기 불용 자재:</b><br>
 * - `GET /report/lt`: 장기 불용 자재 데이터 조회.<br>
 * - `GET /report/lt/{id}`: ID로 장기 불용 자재 데이터 검색.<br>
 * - `GET /report/lt/{startDate}/{endDate}`: 날짜 범위 내 장기 불용 자재 데이터 검색.<br>
 * - <b>구매자재 발주 및 입고:</b><br>
 * - `GET /report/purchase`: 구매자재 데이터 조회.<br>
 * - `GET /report/purchase/{startDate}/{endDate}`: 날짜 범위 내 구매자재 데이터 검색.<br>
 * - `GET /report/purchase/{id}`: ID로 구매자재 데이터 검색.<br>
 * - <b>기타:</b><br>
 * - `GET /report/partList`: 부품 리스트 갱신.<br><br>
 *
 * <b>구성 요소:</b><br>
 * - <b>서비스 의존성:</b> `MainProductionReportService`, `DailyProcessReportService`, `ProcessStockService`, `LossDateService`,<br>
 * `FlowService`, `PerformanceByProcessService`, `PurchaseService`, `LtUnusedMaterialService`, `StandardService`.<br>
 * - <b>애노테이션:</b> `@RestController`, `@RequestMapping`, `@RequiredArgsConstructor`, `@AccessLogAnnotation`.<br><br>
 *
 * <b>특징:</b><br>
 * - 보고서 조회 시 스냅샷(`isSnapShot`) 지원.<br>
 * - 날짜 범위, ID, 카테고리 등 다양한 조건에 따른 검색 기능 제공.<br>
 * - 데이터 업데이트와 관련된 PUT 메서드 구현.<br>
 * - 페이지 접근 로그 기록(`@AccessLogAnnotation`).<br><br>
 *
 * <b>예외 처리:</b><br>
 * - 데이터 처리 중 발생하는 오류는 `GlobalExceptionHandler`에서 처리.<br>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final MainProductionReportService mainProductionReportService;
    private final DailyProcessReportService dailyProcessReportService;
    private final ProcessStockService processStockService;
    private final LossDateService lossDateService;
    private final FlowService flowService;
    private final PerformanceByProcessService performanceByProcessService;
    private final PurchaseService purchaseService;
    private final LtUnusedMaterialService ltUnusedMaterialService;
    private final StandardService standardService;

    @AccessLogAnnotation(accessPage = "메인생산일보")
    @GetMapping("/main")

    public ResponseEntity<?> getMain() {
        return new ResponseEntity<>(mainProductionReportService.getMainProductionReportResponse(), HttpStatus.OK);
    }

    @GetMapping("/main/{startDate}/{endDate}")
    public ResponseEntity<?> searchMain(@PathVariable("startDate") LocalDate startDate,
                                        @PathVariable("endDate") LocalDate endDate) {
        return new ResponseEntity<>(mainProductionReportService.searchMainReport(startDate, endDate), HttpStatus.OK
        );
    }

    @GetMapping("/main/{id}")
    public ResponseEntity<?> findMain(@PathVariable("id") Long id) {
        return new ResponseEntity<>(mainProductionReportService.searchByIdDailyMainReport(id), HttpStatus.OK);
    }

    @AccessLogAnnotation(accessPage = "일일생산일보")
    @GetMapping("/process/{category}")
    public ResponseEntity<?> getProcess(@PathVariable("category") String category) {
        return new ResponseEntity<>(dailyProcessReportService.getProcessProductionDailyReport(category), HttpStatus.OK);
    }

    @GetMapping("/process/{category}/{startDate}/{endDate}")
    public ResponseEntity<?> searchProcess(@PathVariable("category") String category,
                                           @PathVariable("startDate") LocalDate startDate,
                                           @PathVariable("endDate") LocalDate endDate) {
        return new ResponseEntity<>(dailyProcessReportService.searchProcessReport(category, startDate, endDate), HttpStatus.OK
        );
    }

    @GetMapping("/process/detail/{id}")
    public ResponseEntity<?> findProcess(@PathVariable("id") Long id) {
        return new ResponseEntity<>(dailyProcessReportService.getProcessProductionDailyReportById(id), HttpStatus.OK);
    }

    @PutMapping("/process")
    public ResponseEntity<?> updateData(@RequestBody AdditionalDataRequest dataRequest) {
        dailyProcessReportService.updateAdditionalData(dataRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AccessLogAnnotation(accessPage = "LOSS현황")
    @GetMapping("/loss")
    public ResponseEntity<?> getLOSS(boolean isSnapShot) {
        return new ResponseEntity<>(lossDateService.getLossReport(isSnapShot), HttpStatus.OK);
    }

    @GetMapping("/loss/{startDate}/{endDate}")
    public ResponseEntity<?> searchLoss(@PathVariable("startDate") LocalDate startDate,
                                        @PathVariable("endDate") LocalDate endDate, @RequestParam("lossReason") String lossReason) {
        return new ResponseEntity<>(lossDateService.searchLossReport(startDate, endDate, lossReason), HttpStatus.OK);
    }

    @AccessLogAnnotation(accessPage = "공정 자재")
    @GetMapping("/stock")
    public ResponseEntity<?> getProcessStock(boolean isSnapShot) {
        return new ResponseEntity<>(processStockService.getProcessStock(isSnapShot), HttpStatus.OK);
    }

    @GetMapping("/stock/{startDate}/{endDate}")
    public ResponseEntity<?> searchProcessStock(@PathVariable("startDate") LocalDate startDate,
                                                @PathVariable("endDate") LocalDate endDate) {
        return new ResponseEntity<>(processStockService.searchProcessStockReport(startDate, endDate), HttpStatus.OK
        );
    }

    @GetMapping("/stock/{id}")
    public ResponseEntity<?> searchProcessStock(@PathVariable("id") Long id) {
        return new ResponseEntity<>(processStockService.searchProcessStockReportById(id), HttpStatus.OK);
    }

    @GetMapping("/performance")
    public ResponseEntity<?> getPerformance(boolean isSnapShot) {
        return new ResponseEntity<>(performanceByProcessService.getPerformanceByProcessDTOS(isSnapShot), HttpStatus.OK);
    }

    @GetMapping("/performance/{startDate}/{endDate}")
    public ResponseEntity<?> searchPerformance(@RequestParam(name = "modelName") String modelName,
                                               @RequestParam(name = "process") String process,
                                               @PathVariable("startDate") LocalDate startDate,
                                               @PathVariable("endDate") LocalDate endDate) {
        return new ResponseEntity<>(performanceByProcessService.searchPerformanceByProcessDTO(startDate, endDate, modelName, process),
                HttpStatus.OK
        );
    }

    @AccessLogAnnotation(accessPage = "실적 및 생산 흐름")
    @GetMapping("/flow")
    public ResponseEntity<?> getFlowData(boolean isSnapShot) {
        return new ResponseEntity<>(flowService.getFlowDataDTO(isSnapShot), HttpStatus.OK);
    }

    @GetMapping("/flow/{startDate}/{endDate}")
    public ResponseEntity<?> searchFlowData(@RequestParam(name = "modelName") String modelName,
                                            @RequestParam(name = "partNumber") String partNumber,
                                            @PathVariable("startDate") LocalDate startDate,
                                            @PathVariable("endDate") LocalDate endDate) {
        return new ResponseEntity<>(flowService.searchFlowDataDTOS(startDate, endDate, modelName, partNumber),
                HttpStatus.OK);
    }

    @AccessLogAnnotation(accessPage = "장기 불용 자재 ISSUE")
    @GetMapping("/lt")
    public ResponseEntity<?> getLTUnusedMaterial() {
        return new ResponseEntity<>(ltUnusedMaterialService.getIssueListDTOS(true), HttpStatus.OK);
    }

    @GetMapping("/lt/{id}")
    public ResponseEntity<?> searchLTUnusedMaterial(@PathVariable("id") Long id) {
        return new ResponseEntity<>(ltUnusedMaterialService.searchLTUnusedMaterialReportById(id), HttpStatus.OK);
    }

    @GetMapping("/lt/{startDate}/{endDate}")
    public ResponseEntity<?> searchLTUnusedMaterial(@PathVariable("startDate") LocalDate startDate,
                                                    @PathVariable("endDate") LocalDate endDate) {
        return new ResponseEntity<>(ltUnusedMaterialService.searchLTUnusedMaterialReport(startDate, endDate), HttpStatus.OK);
    }

    @AccessLogAnnotation(accessPage = "구매자재 발주 및 입고")
    @GetMapping("/purchase")
    public ResponseEntity<?> getPurchase(boolean isSnapShot) {
        return new ResponseEntity<>(purchaseService.getPurchaseAndReceiptResponse(isSnapShot), HttpStatus.OK);
    }

    @GetMapping("/purchase/{startDate}/{endDate}")
    public ResponseEntity<?> searchPurchase(@PathVariable("startDate") LocalDate startDate,
                                            @PathVariable("endDate") LocalDate endDate) {
        return new ResponseEntity<>(purchaseService.searchPurchase(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/purchase/{id}")
    public ResponseEntity<?> searchPurchase(@PathVariable("id") Long id) {
        return new ResponseEntity<>(purchaseService.searchPurchaseById(id), HttpStatus.OK);
    }

    @GetMapping("/partList")
    public ResponseEntity<?> updatePartList() {
        standardService.updatePartList();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
