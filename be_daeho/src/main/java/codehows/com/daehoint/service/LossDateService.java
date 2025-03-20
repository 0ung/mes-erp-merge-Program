package codehows.com.daehoint.service;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.dto.sync.DailyWorkLossResponse;
import codehows.com.daehoint.entity.ProductionPerformanceStatus;
import codehows.com.daehoint.entity.mes.DailyWorkLoss;
import codehows.com.daehoint.repository.DailyWorkLossRepo;
import codehows.com.daehoint.repository.ProductionPerformanceStatusRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * LossDateService
 *
 * <p>이 서비스 클래스는 일일 손실 데이터를 관리하는 역할을 합니다.
 * 주로 레포지토리와 상호작용하여 손실 데이터를 가져오거나 업데이트하며, 캐싱을 통해 성능을 최적화합니다.</p>
 *
 * <p>주요 역할:</p>
 * <ul>
 *   <li>생산 성과 상태를 기반으로 손실 데이터를 생성 및 업데이트</li>
 *   <li>손실 데이터를 조회 및 캐싱</li>
 *   <li>날짜 범위와 손실 사유에 따른 손실 보고서 검색 기능 제공</li>
 * </ul>
 *
 * <p>의존성:</p>
 * <ul>
 *   <li>{@link ProductionPerformanceStatusRepo}: 생산 성과 상태 데이터를 관리하는 레포지토리</li>
 *   <li>{@link DailyWorkLossRepo}: 일일 작업 손실 데이터를 관리하는 레포지토리</li>
 * </ul>
 *
 * <p>주요 특징:</p>
 * <ul>
 *   <li>스냅샷 데이터를 처리하여 과거 데이터와 비교 가능</li>
 *   <li>캐싱을 활용하여 자주 조회되는 데이터를 효율적으로 제공</li>
 *   <li>손실 데이터를 다양한 조건으로 검색할 수 있는 유연한 검색 기능</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class LossDateService {

    private final ProductionPerformanceStatusRepo productionPerformanceStatusRepo;
    private final DailyWorkLossRepo dailyWorkLossRepo;

    public void createLossData(Boolean isSnapShot) {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        List<ProductionPerformanceStatus> list = productionPerformanceStatusRepo.
                findByCreateDateTimeAfterAndSnapShot(time, isSnapShot);
        if (list == null) {
            return;
        }
        list.forEach(status -> {
            List<DailyWorkLoss> dailyWorkLosses = dailyWorkLossRepo.findByCreateDateTimeAfterAndSnapShot(time,
                    isSnapShot);
            dailyWorkLosses.forEach(loss -> {
                if (!loss.getLotNo().isEmpty()) {
                    loss.updateDailyWorkLoss(status.getCategory());
                }
            });
        });
    }

    @Cacheable(value = "lossData", cacheManager = "longLivedCacheManager")
    public List<DailyWorkLossResponse> getLossReport(boolean isSnapShot) {
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        List<DailyWorkLoss> dailyWorkLosses = dailyWorkLossRepo.findByCreateDateTimeAfterAndSnapShot(date, isSnapShot);

        List<DailyWorkLossResponse> lossDTOList = new ArrayList<>();

        dailyWorkLosses.forEach(dailyWorkLoss -> {
            lossDTOList.add(Util.mapper.toDailyWorkLossDto(dailyWorkLoss));
        });

        return lossDTOList;
    }


    public List<DailyWorkLossResponse> searchLossReport(LocalDate startDate, LocalDate endDate, String lossReason) {
        List<DailyWorkLoss> dailyWorkLosses = null;
        if (lossReason.equals("flag")) {
            dailyWorkLosses = dailyWorkLossRepo.findByCreateDateTimeBetweenAndSnapShot(startDate.atStartOfDay(),
                    endDate.atStartOfDay(), true);
        } else {
            dailyWorkLosses = dailyWorkLossRepo.findByCreateDateTimeBetweenAndSnapShotAndLossReason(
                    startDate.atStartOfDay(), endDate.atStartOfDay(), true, lossReason);
        }
        List<DailyWorkLossResponse> lossDTOList = new ArrayList<>();
        dailyWorkLosses.forEach(dailyWorkLoss -> {
            lossDTOList.add(Util.mapper.toDailyWorkLossDto(dailyWorkLoss));
        });
        return lossDTOList;
    }

}
