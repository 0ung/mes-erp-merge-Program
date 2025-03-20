package codehows.com.daehoint.service;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.dto.FlowDataResponse;
import codehows.com.daehoint.entity.ProductionPerformanceStatus;
import codehows.com.daehoint.entity.mes.LotResultList;
import codehows.com.daehoint.repository.LotResultListRepo;
import codehows.com.daehoint.repository.ProductionPerformanceStatusRepo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * <b>FlowService</b><br>
 *
 * <p><b>설명:</b></p>
 * 이 클래스는 LOT 데이터를 기반으로 공정 진행률, 투입 비용, 완료 상태 등의 흐름 데이터를 생성, 관리, 조회하는 서비스를 제공합니다.
 *
 * <p><b>주요 기능:</b></p>
 * <ul>
 *   <li>LOT 데이터를 기반으로 공정 데이터를 생성 및 계산</li>
 *   <li>진행률 데이터를 생성 및 통계화</li>
 *   <li>조건부 검색을 통한 데이터 필터링</li>
 *   <li>중복 제거 및 데이터 정렬</li>
 * </ul>
 *
 * <p><b>주요 메서드:</b></p>
 * <ul>
 *   <li>{@code getFlowDataDTO(boolean isSnapshot)}: 스냅샷 여부에 따른 현재 흐름 데이터를 조회합니다.</li>
 *   <li>{@code searchFlowDataDTOS(LocalDate startDate, LocalDate endDate, String modelNames, String partNumber)}:
 *       검색 조건에 따라 흐름 데이터를 조회합니다.</li>
 *   <li>{@code createFlowData(LotResultList lotResultList, LocalDateTime time, LocalDateTime end, boolean isSnapshot)}:
 *       특정 LOT 데이터를 기반으로 흐름 데이터를 생성합니다.</li>
 *   <li>{@code calcLotProgress(Map<String, FlowDataResponse.ProcessDataDTO> map)}: 공정 진행률을 계산합니다.</li>
 *   <li>{@code distinctByKey(Function<? super T, ?> keyExtractor)}: 중복 데이터를 제거하기 위한 헬퍼 메서드입니다.</li>
 * </ul>
 *
 * <p><b>주요 의존성:</b></p>
 * <ul>
 *   <li>{@code LotResultListRepo}: LOT 결과 데이터를 조회합니다.</li>
 *   <li>{@code ProductionPerformanceStatusRepo}: 공정 성과 데이터를 조회합니다.</li>
 * </ul>
 *
 * <p><b>구현 상세:</b></p>
 * - 데이터는 스냅샷 여부를 기준으로 분리하여 조회합니다.<br>
 * - 공정별 데이터를 매핑 및 가공하여, 진행률 및 비용 계산 데이터를 생성합니다.<br>
 * - 검색 조건에 따른 데이터 필터링 및 결과 반환 기능을 제공합니다.<br>
 * - 캐싱을 활용하여 자주 조회되는 데이터를 성능 최적화를 위해 메모리에 저장합니다.<br>
 *
 * <p><b>참고:</b></p>
 * - {@code createProcessData()} 메서드는 공정 데이터를 개별적으로 생성합니다.<br>
 * - {@code findByCondition()} 메서드는 모델명, 부품 번호 등 다양한 조건을 적용하여 데이터를 조회합니다.<br>
 * - 스냅샷 데이터와 실시간 데이터를 구분하여 처리하는 로직을 포함하고 있습니다.<br>
 */
@Service
public class FlowService {
    private final LotResultListRepo lotResultListRepo;
    private final ProductionPerformanceStatusRepo productionPerformanceStatusRepo;

    public FlowService(LotResultListRepo lotResultListRepo, ProductionPerformanceStatusRepo productionPerformanceStatusRepo) {
        this.lotResultListRepo = lotResultListRepo;
        this.productionPerformanceStatusRepo = productionPerformanceStatusRepo;
    }


    private FlowDataResponse.ProcessDataDTO createProcessData(LotResultList request, LocalDateTime time, boolean isSnapshot) {
        List<ProductionPerformanceStatus> status = productionPerformanceStatusRepo.findByLotNoAndCreateDateTimeAfterAndSnapShot(
                request.getLotId(), time, isSnapshot);

        double inputCost = (status != null && !status.isEmpty() && status.get(0).getTotalPerformanceAmount() != null)
                ? status.get(0).getTotalPerformanceAmount()
                : 0.0;

        double completedRate = (request.getOutQty() == null || request.getInQty() == null || request.getInQty() == 0)
                ? 0.0
                : (double) request.getOutQty() / request.getInQty() * 100;
        return FlowDataResponse.ProcessDataDTO.builder()
                .planQty(request.getLotQty() != null ? request.getLotQty() : 0)
                .inputQty(request.getInQty() != null ? request.getInQty() : 0)
                .completedQty(request.getOutQty() != null ? request.getOutQty() : 0)
                .inputCost(inputCost)
                .completedRate(completedRate)
                .build();
    }

    private Map<String, FlowDataResponse.ProcessDataDTO> getProcessDataDTOMap(List<LotResultList> list, LocalDateTime time, boolean isSnapshot) {
        return list.stream()
                .collect(Collectors.toMap(
                        LotResultList::getItemName,
                        request -> createProcessData(request, time, isSnapshot),
                        (existing, replacement) -> replacement));
    }

    private Double calcLotProgress(Map<String, FlowDataResponse.ProcessDataDTO> map) {
        return map.values().stream()
                .mapToDouble(FlowDataResponse.ProcessDataDTO::getCompletedRate
                    )
                .average().orElse(0.0);
    }

    private FlowDataResponse searchFlowData(LotResultList lotResultList, LocalDateTime start, LocalDateTime end) {
        return createFlowData(lotResultList, start, end, true);
    }

    private FlowDataResponse normalFlowData(LotResultList lotResultList, LocalDateTime start, LocalDateTime end) {
        return createFlowData(lotResultList, start, end, false);
    }

    private FlowDataResponse createFlowData(LotResultList lotResultList, LocalDateTime time, LocalDateTime end, boolean isSnapshot) {
        List<LotResultList> request = null;
        if (isSnapshot) {
            request = lotResultListRepo.findByProductionRequestNoAndSnapShotAndCreateDateTimeBetween(lotResultList.getProductionRequestNo(), true, time, end);
        } else {
            request = lotResultListRepo.findByProductionRequestNoAndSnapShotAndCreateDateTimeAfter(lotResultList.getProductionRequestNo(), false, time);
        }

        Map<String, FlowDataResponse.ProcessDataDTO> progressData = getProcessDataDTOMap(request, time, isSnapshot);
        Double lotProgress = calcLotProgress(progressData);

        return FlowDataResponse.builder()
                .lotProgress(lotProgress + "")
                .modelName(lotResultList.getCategoryItemValue02())
                .specification(lotResultList.getItemSpec())
                .productionRequestNo(lotResultList.getProductionRequestNo())
                .partNumber(lotResultList.getItemCd())
                .processData(progressData)
                .progressTypeDTO(FlowDataResponse.ProgressTypeDTO
                        .builder()
                        .processProgress(new ArrayList<>(progressData.values()
                                .stream()
                                .map(FlowDataResponse.ProcessDataDTO::getCompletedRate)
                                .collect(Collectors.toList())))
                        .build())
                .build();
    }

    @Cacheable(value = "flowData", cacheManager = "longLivedCacheManager")
    public List<FlowDataResponse> getFlowDataDTO(boolean isSnapshot) {
        LocalDateTime time = Util.getTime();
        List<LotResultList> lotResultLists = lotResultListRepo.findByCreateDateTimeAfterAndSnapShot(time, isSnapshot);
        return lotResultLists.stream().map(lotResultList -> normalFlowData(lotResultList, time, LocalDateTime.now())
        ).collect(Collectors.toList());
    }

    public List<FlowDataResponse> searchFlowDataDTOS(LocalDate startDate, LocalDate endDate, String modelNames,
                                                     String partNumber) {
        List<LotResultList> lotResultLists = findByCondition(startDate, endDate, modelNames, partNumber);
        return lotResultLists.stream().map(lotResultList -> searchFlowData(lotResultList, startDate.atStartOfDay(), endDate.atStartOfDay())
        ).filter(distinctByKey(FlowDataResponse::getProductionRequestNo)).collect(Collectors.toList());
    }

    // 중복 제거를 위한 헬퍼 메서드
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    private List<LotResultList> findByCondition(LocalDate startDate, LocalDate endDate,
                                                String modelNames, String partNumber) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atStartOfDay();

        if (modelNames.isEmpty() && partNumber.isEmpty()) {
            // 모델명과 부품 번호가 모두 비어있는 경우
            return lotResultListRepo.findByCreateDateTimeBetweenAndSnapShot(
                    start, end, true);
        } else if (!modelNames.isEmpty() && partNumber.isEmpty()) {
            // 모델명만 있을 때
            return lotResultListRepo.findByCreateDateTimeBetweenAndSnapShotAndCategoryItemValue02(
                    start, end, true, modelNames);
        } else if (modelNames.isEmpty()) {
            // 부품 번호만 있을 때
            return lotResultListRepo.findByCreateDateTimeBetweenAndSnapShotAndItemCd(
                    start, end, true, partNumber);
        } else {
            // 모델명과 부품 번호가 모두 있을 때
            return lotResultListRepo.findByCreateDateTimeBetweenAndSnapShotAndCategoryItemValue02AndItemCd(
                    start, end, true, modelNames, partNumber);
        }
    }
}
