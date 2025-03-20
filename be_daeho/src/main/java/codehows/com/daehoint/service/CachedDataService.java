package codehows.com.daehoint.service;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.constants.Category;
import codehows.com.daehoint.dto.MainProductionReportResponse;
import codehows.com.daehoint.entity.MainProductionReport;
import codehows.com.daehoint.entity.ProcessProductionReport;
import codehows.com.daehoint.entity.ProductionPerformanceStatus;
import codehows.com.daehoint.entity.mes.EstimatedExpenses;
import codehows.com.daehoint.entity.mes.ProductionDailyList;
import codehows.com.daehoint.entity.mes.WorkerRetention;
import codehows.com.daehoint.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * <b>CachedDataService</b><br />
 * <p></p>
 * 데이터베이스의 다양한 데이터를 캐싱하여 성능을 향상시키는 서비스 클래스입니다.<br />
 * 생산 관련 데이터, 비용 데이터 및 기타 정보를 효율적으로 처리하고 응답 시간을 단축합니다.<br />
 *
 * <p><b>주요 기능:</b></p><br />
 * - `@Cacheable` 어노테이션을 활용하여 데이터를 캐싱.<br />
 * - 생산 보고서, 인력 데이터 및 비용 데이터를 캐싱된 값으로 반환.<br />
 * - 캐시의 생명 주기를 관리하여 데이터의 신뢰성을 유지.<br />
 *
 * <p><b>캐싱 데이터:</b></p><br />
 * - `main_workerRetentions`: 작업자 유지 데이터 (짧은 캐시 수명).<br />
 * - `main_processProductionReport`: 공정 생산 보고 데이터 (짧은 캐시 수명).<br />
 * - `main_estimatedExpenses`: 추정 비용 데이터 (짧은 캐시 수명).<br />
 * - `process_productionPerformanceStatus`: 공정 생산 성능 상태 데이터 (짧은 캐시 수명).<br />
 * - `report_mainProductionReport`: 메인 생산 보고 데이터 (긴 캐시 수명).<br />
 *
 * <p><b>사용 예:</b></p><br />
 * - 생산성과 관련된 데이터를 캐싱하여 대량 요청 시 서버 부하를 감소.<br />
 * - 데이터를 즉시 반환하여 빠른 사용자 경험 제공.<br />
 */
@Service
@RequiredArgsConstructor
public class CachedDataService {
    private final WorkerRetentionRepo workerRetentionRepo;
    private final EstimatedExpensesRepo estimatedExpensesRepo;
    private final ProcessProductionReportRepo processProductionReportRepo;
    private final ProductionPerformanceStatusRepo productionPerformanceStatusRepo;
    private final MainProductionReportRepo mainProductionReportRepo;
    private final ProductionDailyListRepo productionDailyListRepo;

    //TODO
    @Cacheable(value = "main_workerRetentions", cacheManager = "shortLivedCacheManager")
    public List<WorkerRetention> getWorkerRetentions(boolean isSnapshot) {
        LocalDateTime time = Util.getTime();
        return Optional.ofNullable(
                workerRetentionRepo.findByCreateDateTimeAfterAndSnapShot(time, isSnapshot)).orElse(List.of());
    }

    @Cacheable(value = "main_processProductionReport", cacheManager = "shortLivedCacheManager")
    public List<ProcessProductionReport> getProcessProductionReport(boolean isSnapshot) {
        LocalDateTime time = Util.getTime();
        return Optional.ofNullable(
                processProductionReportRepo.findByCreateDateTimeAfterAndSnapShot(time, isSnapshot)).orElse(List.of());
    }

    @Cacheable(value = "main_estimatedExpenses", cacheManager = "shortLivedCacheManager")
    public EstimatedExpenses getEstimatedExpenses(boolean isSnapshot) {
        LocalDateTime time = Util.getTime();
        return estimatedExpensesRepo.findByCreateDateTimeAfterAndSnapshot(time, isSnapshot);
    }

    @Cacheable(value = "process_productionPerformanceStatus", cacheManager = "shortLivedCacheManager")
    public List<ProductionPerformanceStatus> getProductionPerformanceStatus(Category category, boolean isSnapshot) {
        LocalDateTime time = Util.getTime();
        return productionPerformanceStatusRepo.findByCategoryAndCreateDateTimeAfterAndSnapShot(
                category.getDescription(), time, isSnapshot);
    }

    //상시 메인 생산일보
    @Cacheable(value = "report_mainProductionReport", cacheManager = "longLivedCacheManager")
    public MainProductionReportResponse getMainProductionReportResponse() {
        LocalDateTime time = Util.getTime();
        MainProductionReport mainProductionReport = mainProductionReportRepo.findByCreateDateTimeAfterAndSnapShot(time, false);
        return MainProductionReportResponse.convertToDTO(mainProductionReport);
    }

    @Cacheable(value = "flux_soldering", cacheManager = "shortLivedCacheManager")
    public List<ProductionDailyList> getProductionDailyLists(Category category, boolean isSnapshot) {
        LocalDateTime time = Util.getTime();
        return productionDailyListRepo.findByCreateDateTimeAfterAndGubunNameAndItemNameAndSnapShot(
                time, "투입수량", category.getDescription(), isSnapshot
        );
    }
}
