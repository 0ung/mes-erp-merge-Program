package codehows.com.daehoint.service;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.dto.ProcessStockResponse;
import codehows.com.daehoint.dto.SearchReportResponse;
import codehows.com.daehoint.entity.ProcessStock;
import codehows.com.daehoint.entity.ProductionPerformanceStatus;
import codehows.com.daehoint.entity.erp.VPDSPFWorkReportQC;
import codehows.com.daehoint.entity.mes.ProductionDailyList;
import codehows.com.daehoint.formula.ProcessStockFormula;
import codehows.com.daehoint.repository.ProcessStockRepo;
import codehows.com.daehoint.repository.ProductionDailyListRepo;
import codehows.com.daehoint.repository.ProductionPerformanceStatusRepo;
import codehows.com.daehoint.repository.VPDSPFWorkReportQCRepo;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 공정 재고 서비스 클래스
 *
 * <p>이 클래스는 공정 재고 데이터를 생성, 계산, 저장하는 역할을 수행합니다.
 * 공정 재고 데이터는 생산 일일 리스트, 품질 관리 보고서, 생산 실적 현황 데이터를 활용하여 계산됩니다.
 * 계산된 데이터는 데이터베이스에 저장되며, 캐시를 통해 성능 최적화도 지원합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>공정 재고 데이터 생성 및 계산</li>
 *   <li>생성된 공정 재고 데이터를 데이터베이스에 저장</li>
 *   <li>공정 재고 데이터를 캐싱하여 빠른 조회 지원</li>
 * </ul>
 *
 * <p>의존성:</p>
 * <ul>
 *   <li>{@code ProductionDailyListRepo}: 생산 일일 리스트 데이터 조회</li>
 *   <li>{@code VPDSPFWorkReportQCRepo}: 품질 관리 보고서 데이터 조회</li>
 *   <li>{@code ProductionPerformanceStatusRepo}: 생산 실적 현황 데이터 조회</li>
 *   <li>{@code ProcessStockFormula}: 공정 재고 계산 로직</li>
 *   <li>{@code ProcessStockRepo}: 공정 재고 데이터 저장 및 조회</li>
 * </ul>
 *
 * <p>참고:</p>
 * <ul>
 *   <li>공정 재고 데이터는 캐싱을 통해 반복적인 조회 시 성능을 향상시킵니다.</li>
 *   <li>{@code isSnapShot} 설정 여부에 따라 스냅샷 데이터를 처리합니다.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ProcessStockService {

    private final ProductionDailyListRepo productionDailyListRepo;
    private final VPDSPFWorkReportQCRepo vpdSPFWorkReportQCRepo;
    private final ProductionPerformanceStatusRepo productionPerformanceStatusRepo;
    private final ProcessStockFormula processStockFormula;
    private final ProcessStockRepo processStockRepo;

    @Setter
    private boolean isSnapShot;

    public void saveProcessStock(List<ProcessStock> processStocks) {
        processStockRepo.saveAll(processStocks);
    }

    //    @CacheEvict(value = "processStockResponse", allEntries = true, cacheResolver = "customCacheResolver")
    public List<ProcessStock> createProcessStock() {
        LocalDateTime time = Util.getTime();
        String lotStartTime = LocalDate.now().toString();
        List<ProcessStock> processStocks = new ArrayList<>();

        List<ProductionDailyList> lists = productionDailyListRepo.findByCreateDateTimeAfterAndStartTimeAndGubunName(
                time, lotStartTime, "투입수량");
        lists.forEach(dailyList -> {
            String lotCode = dailyList.getLotId();
            // VPDSPFWorkReportQC 레포지토리 조회
            List<VPDSPFWorkReportQC> vpdspfWorkReportQC = vpdSPFWorkReportQCRepo.findByOperationInstructionNumberAndCreateDateTimeAfterAndSnapShot(
                    lotCode, time, isSnapShot);

            // ProductionPerformanceStatus 레포지토리 조회
            List<ProductionPerformanceStatus> statusList = productionPerformanceStatusRepo.findByLotNoAndCreateDateTimeAfterAndSnapShot(
                    lotCode, time, isSnapShot);

            if (statusList == null || statusList.isEmpty()) {
                return;
            }
            ProcessStock processStock = calcProcessStock(vpdspfWorkReportQC, statusList.get(0), dailyList);
            processStock.setSnapShot(isSnapShot);
            processStocks.add(processStock);
        });
        return processStocks;
    }

    private ProcessStock calcProcessStock(List<VPDSPFWorkReportQC> qcList,
                                          ProductionPerformanceStatus status,
                                          ProductionDailyList dailyList) {
        double pricePerSet = processStockFormula.pricePerSet(status);
        String productName = processStockFormula.productName(status);
        String modelNo = processStockFormula.modelNo(status);
        String specification = processStockFormula.specification(status);
        double materialCost = processStockFormula.materialCost(status);
        double processingCost = processStockFormula.processingCost(status);
        double totalCost = processStockFormula.totalCost(status);
        double wipQuantity = processStockFormula.wipQuantity(dailyList);
        double wipCost = processStockFormula.wipCost(wipQuantity, pricePerSet);
        double qcPendingQuantity = processStockFormula.qcPendingQuantity(status);
        double qcPendingCost = processStockFormula.qcPendingCost(qcPendingQuantity, pricePerSet);
        double qcPassedQuantity = processStockFormula.qcPassedQuantity(qcList);
        double qcPassedCost = processStockFormula.qcPassedCost(qcPassedQuantity, pricePerSet);
        double defectiveQuantity = processStockFormula.defectiveQuantity(qcList, status);
        double defectiveCost = processStockFormula.defectiveCost(defectiveQuantity, pricePerSet);
        double totalQuantity = processStockFormula.totalQuantity(wipQuantity, qcPendingQuantity, qcPassedQuantity);
        double totalCostSummary = processStockFormula.totalCostSummary(wipCost, qcPendingCost, qcPassedCost);

        return ProcessStock.builder()
                .category(status.getCategory())
                .snapShot(isSnapShot)
                .productName(productName)
                .modelNo(modelNo)
                .specification(specification)
                .materialCost(materialCost)
                .processingCost(processingCost)
                .totalCost(totalCost)
                .wipQuantity(wipQuantity)
                .wipCost(wipCost)
                .qcPendingQuantity(qcPendingQuantity)
                .qcPendingCost(qcPendingCost)
                .qcPassedQuantity(qcPassedQuantity)
                .qcPassedCost(qcPassedCost)
                .defectiveQuantity(defectiveQuantity)
                .defectiveCost(defectiveCost)
                .totalQuantity(totalQuantity)
                .totalCostSummary(totalCostSummary)
                .build();
    }

    private List<ProcessStockResponse> convertToProcessStockResponse(List<ProcessStock> processStocks) {
        return processStocks.stream()
                .map(Util.mapper::toProcessStockResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "processStockResponse", key = "#isSnapShot", cacheManager = "longLivedCacheManager")
    public List<ProcessStockResponse> getProcessStock(boolean isSnapShot) {
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        List<ProcessStock> processStocks = processStockRepo.findByCreateDateTimeAfterAndSnapShot(date, isSnapShot);
        return convertToProcessStockResponse(processStocks);
    }

    @Cacheable(value = "processStockResponse", key = "#id", cacheManager = "shortLivedCacheManager")
    public List<ProcessStockResponse> searchProcessStockReportById(Long id) {
        ProcessStock processStock = processStockRepo.findById(id).orElse(null);
        if (processStock == null) {
            return Collections.emptyList(); // null이면 빈 리스트 반환
        }

        // 해당 ID의 날짜를 기준으로 그 날짜의 모든 데이터를 가져오기 위한 시간 설정
        LocalDateTime startDateTime = processStock.getCreateDateTime().toLocalDate().atStartOfDay();
        LocalDateTime endDateTime = startDateTime.withHour(23).withMinute(59).withSecond(59);

        // 해당 날짜의 모든 ProcessStock을 조회
        List<ProcessStock> processStocksByDate = processStockRepo.findByCreateDateTimeBetweenAndSnapShot(startDateTime,
                endDateTime, true);
        return convertToProcessStockResponse(processStocksByDate);
    }

    public List<SearchReportResponse> searchProcessStockReport(LocalDate startDate, LocalDate endDate) {
        List<ProcessStock> processStocks = processStockRepo.findByCreateDateTimeBetweenAndSnapShot(
                startDate.atStartOfDay(), endDate.atStartOfDay(), true);

        Map<LocalDate, ProcessStock> groupedStocks = processStocks.stream()
                .collect(Collectors.toMap(
                        stock -> stock.getCreateDateTime().toLocalDate(), // Key: 날짜
                        stock -> stock,                                   // Value: ProcessStock 객체
                        (existing, replacement) -> existing              // 중복 키 발생 시 기존 값 유지
                ));
        // DTO 변환
        return groupedStocks.values().stream()
                .map(stock -> SearchReportResponse.builder()
                        .id(stock.getId())
                        .name("공정 재고")
                        .createDate(stock.getCreateDateTime().format(Util.formatter))
                        .build())
                .collect(Collectors.toList());
    }

}
