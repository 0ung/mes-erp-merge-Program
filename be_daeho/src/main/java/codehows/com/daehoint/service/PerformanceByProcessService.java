package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.PerformanceByProcessResponse;
import codehows.com.daehoint.entity.mes.LotResultList;
import codehows.com.daehoint.entity.mes.ProductionDailyList;
import codehows.com.daehoint.repository.LotResultListRepo;
import codehows.com.daehoint.repository.ProductionDailyListRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * PerformanceByProcessService 클래스
 *
 * <p>이 클래스는 공정별 생산 실적 데이터를 처리하고, 해당 데이터를 기반으로 DTO를 생성하여
 * 클라이언트에 반환하는 서비스를 제공합니다. 데이터는 공정별로 그룹화하여 처리하며, 각 공정에 대한
 * 계획, 투입, 완료, 불량 수량 등을 계산하여 상세한 실적 정보를 제공합니다.</p>
 *
 * 주요 기능:
 * <ul>
 *   <li>공정별 생산 실적 데이터 조회 및 캐싱: {@code getPerformanceByProcessDTOS(boolean isSnapShot)}</li>
 *   <li>특정 조건으로 생산 실적 데이터 검색: {@code searchPerformanceByProcessDTO(LocalDate startDate, LocalDate endDate, String modelNames, String processes)}</li>
 * </ul>
 *
 * <p>주요 메서드 설명:</p>
 * <ul>
 *   <li>공정 데이터 그룹화 및 실적 계산: {@code createPerformanceDTO(String lotId, List<ProductionDailyList> dailyLists, boolean isSnapShot, LocalDateTime date)}</li>
 *   <li>계획, 투입, 완료 수량 계산: {@code extractQuantityData(List<ProductionDailyList> dailyLists)}</li>
 *   <li>공정 정보와 모델명 추출: {@code fetchProcessInfo(String lotId, String modelName, String lotState, boolean isSnapshot, LocalDateTime date)}</li>
 * </ul>
 *
 * <p>특징:</p>
 * <ul>
 *   <li>공정별 데이터는 LOT ID를 기준으로 그룹화하여 처리됩니다.</li>
 *   <li>정규식을 사용하여 모델명에서 공정 정보를 추출합니다.</li>
 *   <li>데이터 조회 결과는 캐싱을 활용하여 조회 성능을 최적화합니다.</li>
 *   <li>스냅샷 여부에 따라 데이터 조회 방식이 달라집니다.</li>
 * </ul>
 *
 * <p>기술 스택 및 의존성:</p>
 * <ul>
 *   <li>{@code ProductionDailyListRepo}: 생산 일일 데이터 조회</li>
 *   <li>{@code LotResultListRepo}: LOT 결과 데이터 조회</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PerformanceByProcessService {
    private final String pattern = "\\b(SM ASSY|IM ASSY|DIP ASSY|MANUAL ASSY|PCB ASSY|CASE ASSY|ACCY|PACKING ASSY|BOARD_DIP ASSY)\\b";
    private final ProductionDailyListRepo productionDailyListRepo;
    private final LotResultListRepo lotResultListRepo;
    @AllArgsConstructor
    private static class QuantityData {
        List<String> planData, inputData, completedData;
        int planQty, inputQty, completedQty, defectQty;
    }

    @AllArgsConstructor
    @Getter
    private static class ProcessInfo {
        private final String depart, process, modelName;
    }

    private List<String> setTimeList(ProductionDailyList productionDailyList) {
        return Arrays.asList(
                productionDailyList.getTime01(),
                productionDailyList.getTime02(),
                productionDailyList.getTime03(),
                productionDailyList.getTime04(),
                productionDailyList.getTime05(),
                productionDailyList.getTime06(),
                productionDailyList.getTime07(),
                productionDailyList.getTime08(),
                productionDailyList.getTime09(),
                productionDailyList.getTime10(),
                productionDailyList.getTime11()
        );
    }

    private int setTimeTotal(ProductionDailyList productionDailyList) {
        return parseOrDefault(productionDailyList.getTime01()) +
                parseOrDefault(productionDailyList.getTime02()) +
                parseOrDefault(productionDailyList.getTime03()) +
                parseOrDefault(productionDailyList.getTime04()) +
                parseOrDefault(productionDailyList.getTime05()) +
                parseOrDefault(productionDailyList.getTime06()) +
                parseOrDefault(productionDailyList.getTime07()) +
                parseOrDefault(productionDailyList.getTime08()) +
                parseOrDefault(productionDailyList.getTime09()) +
                parseOrDefault(productionDailyList.getTime10()) +
                parseOrDefault(productionDailyList.getTime11());
    }

    private int parseOrDefault(String value) {
        return (value == null || value.isEmpty()) ? 0 : Integer.parseInt(value);
    }

    private ProcessInfo fetchProcessInfo(String lotId, String modelName, String lotState, boolean isSnapshot, LocalDateTime date) {
        List<LotResultList> resultLists = lotResultListRepo.findByLotIdAndCreateDateTimeAfterAndSnapShot(lotId,
                date, isSnapshot);

        String depart = "";
        String process = "";

        if (!resultLists.isEmpty()) {
            LotResultList result = resultLists.get(0);
            depart = result.getDepartment();
            process = result.getItemName();
        } else {
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(modelName);
            depart = switch (lotState) {
                case "작업대기" -> "작업대기";
                case "생산중" -> "데이터 추가중";
                case "생산완료" -> "실적 반영 대기 중";
                default -> depart;
            };
            if (matcher.find()) {
                process = matcher.group();
                modelName = matcher.replaceAll("");
            }
        }

        return new ProcessInfo(depart, process, modelName);
    }

    private QuantityData extractQuantityData(List<ProductionDailyList> dailyLists) {
        List<String> planData = new ArrayList<>();
        List<String> inputData = new ArrayList<>();
        List<String> completedData = new ArrayList<>();
        int planQty = 0, inputQty = 0, completedQty = 0, defectQty = 0;

        for (ProductionDailyList productionDailyList : dailyLists) {
            switch (productionDailyList.getGubunName()) {
                case "계획수량":
                    planData = setTimeList(productionDailyList);
                    planQty = Integer.parseInt(productionDailyList.getQty());
                    break;
                case "투입수량":
                    inputData = setTimeList(productionDailyList);
                    inputQty = setTimeTotal(productionDailyList);
                    break;
                case "완료수량":
                    completedData = setTimeList(productionDailyList);
                    completedQty = Integer.parseInt(productionDailyList.getOutQty());
                    defectQty = Integer.parseInt(productionDailyList.getDefectQty());
                    break;
                default:
                    break;
            }
        }
        return new QuantityData(planData, inputData, completedData, planQty, inputQty, completedQty, defectQty);
    }

    private PerformanceByProcessResponse createPerformanceDTO(String lotId, List<ProductionDailyList> dailyLists, boolean isSnapShot, LocalDateTime date) {
        QuantityData quantityData = extractQuantityData(dailyLists);
        String modelName = dailyLists.get(0).getCategoryItemValue03();
        String lotState = dailyLists.get(0).getLotState();

        ProcessInfo processInfo = fetchProcessInfo(lotId, modelName, lotState, isSnapShot, date);

        return PerformanceByProcessResponse.builder()
                .depart(processInfo.getDepart())
                .process(processInfo.getProcess())
                .modelName(processInfo.getModelName())
                .processStatus(lotState)
                .quantityData(PerformanceByProcessResponse.QuantityDataDTO.builder()
                        .planQuantity(quantityData.planQty)
                        .inputQuantity(quantityData.inputQty)
                        .completedQuantity(quantityData.completedQty)
                        .defectQuantity(quantityData.defectQty)
                        .build())
                .planData(quantityData.planData)
                .inputData(quantityData.inputData)
                .completedData(quantityData.completedData)
                .build();
    }
    @Cacheable(value = "performanceData", cacheManager = "longLivedCacheManager")
    public List<PerformanceByProcessResponse> getPerformanceByProcessDTOS(boolean isSnapShot) {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        List<ProductionDailyList> productionDailyLists = productionDailyListRepo.findByCreateDateTimeAfterAndSnapShot(time, isSnapShot);
        Map<String, List<ProductionDailyList>> groupedData = productionDailyLists.stream()
                .collect(Collectors.groupingBy(ProductionDailyList::getLotId));
        return groupedData.entrySet().stream()
                .map(entry -> createPerformanceDTO(entry.getKey(), entry.getValue(), isSnapShot, time))
                .collect(Collectors.toList());
    }

    public List<PerformanceByProcessResponse> searchPerformanceByProcessDTO(LocalDate startDate, LocalDate endDate,
                                                                            String modelNames,
                                                                            String processes) {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        List<ProductionDailyList> productionDailyLists = null;

        if (!modelNames.isEmpty() && !processes.isEmpty()) {
            // 모델명과 공정이 모두 있는 경우
            productionDailyLists = productionDailyListRepo.findByCreateDateTimeBetweenAndSnapShotAndCategoryItemValue03AndItemName(
                    startDate.atStartOfDay(), endDate.atStartOfDay(), true, modelNames, processes);
        } else if (!modelNames.isEmpty()) {
            // 모델명만 있는 경우
            productionDailyLists = productionDailyListRepo.findByCreateDateTimeBetweenAndSnapShotAndCategoryItemValue03(
                    startDate.atStartOfDay(), endDate.atStartOfDay(), true, modelNames);
        } else if (!processes.isEmpty()) {
            // 공정만 있는 경우
            productionDailyLists = productionDailyListRepo.findByCreateDateTimeBetweenAndSnapShotAndItemName(
                    startDate.atStartOfDay(), endDate.atStartOfDay(), true, processes);
        } else {
            // 모델명과 공정 모두 없는 경우 (기간만 조회)
            productionDailyLists = productionDailyListRepo.findByCreateDateTimeBetweenAndSnapShot(
                    startDate.atStartOfDay(), endDate.atStartOfDay(), true);
        }


        Map<String, List<ProductionDailyList>> groupedData = productionDailyLists.stream()
                .collect(Collectors.groupingBy(ProductionDailyList::getLotId));
        return groupedData.entrySet().stream()
                .map(entry -> createPerformanceDTO(entry.getKey(), entry.getValue(), true, time))
                .collect(Collectors.toList());
    }
}
