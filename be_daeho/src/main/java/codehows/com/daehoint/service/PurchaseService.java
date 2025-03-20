package codehows.com.daehoint.service;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.dto.PurchaseAndReceiptResponse;
import codehows.com.daehoint.dto.SearchReportResponse;
import codehows.com.daehoint.entity.erp.VLGWHStockAmtList;
import codehows.com.daehoint.entity.erp.VLGWHStockWHAmtList;
import codehows.com.daehoint.entity.erp.VPUORDAmtList;
import codehows.com.daehoint.entity.mes.MaterialPurchasePlanMonthly;
import codehows.com.daehoint.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PurchaseService 클래스
 *
 * <p>이 클래스는 구매 및 입고 현황 데이터를 관리하는 서비스입니다. ERP 및 MES 시스템에서 데이터를 조회하고
 * 이를 가공하여 클라이언트에 적합한 DTO 형식으로 반환합니다. 주요 데이터는 구매, 재고, 창고 자재 상태 정보를 포함하며,
 * 일간 데이터 및 주간 데이터를 계산하고 병합하여 제공합니다.</p>
 *
 * 주요 기능:
 * <ul>
 *   <li>구매 및 입고 현황 데이터 조회 및 캐싱: {@code getPurchaseAndReceiptResponse(boolean isSnapShot)}</li>
 *   <li>특정 조건으로 구매 및 입고 데이터 검색: {@code searchPurchase(LocalDate startDate, LocalDate endDate)}</li>
 *   <li>구체적인 ID를 기반으로 구매 및 입고 데이터 검색: {@code searchPurchaseById(Long id)}</li>
 * </ul>
 *
 * <p>주요 메서드 설명:</p>
 * <ul>
 *   <li>일간 자재 비용 데이터 생성 및 병합: {@code createDailyMaterialCostDTOS(List<VPUORDAmtList> vpuordAmtLists, MaterialPurchasePlanMonthly materialPurchasePlanMonthly)}</li>
 *   <li>재고 상태 데이터 생성 및 병합: {@code createStockStatusDTOS(List<VLGWHStockAmtList> list)}</li>
 *   <li>창고 자재 상태 데이터 생성: {@code createWarehouseMaterialStatusDTOS(List<VLGWHStockWHAmtList> whAmtLists)}</li>
 * </ul>
 *
 * <p>특징:</p>
 * <ul>
 *   <li>ERP 및 MES 데이터베이스에서 데이터를 가져와 가공합니다.</li>
 *   <li>카테고리별 데이터 병합 기능을 제공합니다, ACCY, 제품 데이터를 기타로 병합</li>
 *   <li>기간 검색 기능을 통해 특정 날짜 범위의 데이터를 조회할 수 있습니다.</li>
 *   <li>데이터 캐싱을 활용하여 성능을 최적화합니다.</li>
 * </ul>
 *
 * <p>기술 스택 및 의존성:</p>
 * <ul>
 *   <li>{@code VLGWHStockAmtListRepo}: 구매 및 입고 상태 데이터 관리</li>
 *   <li>{@code VLGWHStockWHAmtListRepo}: 창고 자재 상태 데이터 관리</li>
 *   <li>{@code VPUORDAmtListDayRepo}: 구매 주문 데이터 관리</li>
 *   <li>{@code MaterialPurchasePlanMonthlyRepo}: 월별 자재 구매 계획 데이터 관리</li>
 * </ul>
 *

 */
@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final VLGWHStockAmtListRepo vlgwhStockAmtListRepo;
    private final VLGWHStockWHAmtListRepo vlgwhStockWHAmtListRepo;
    private final VPUORDAmtListDayRepo vpuordAmtListDayRepo;
    private final MaterialPurchasePlanMonthlyRepo materialPurchasePlanMonthlyRepo;


    private List<PurchaseAndReceiptResponse.DailyMaterialCostDTO> createDailyMaterialCostDTOS(
            List<VPUORDAmtList> vpuordAmtLists,
            MaterialPurchasePlanMonthly materialPurchasePlanMonthly) {

        LocalDateTime time = Util.getTime();
        LocalDate weeklyStartDate = getFirstDayOfWeek(time.toLocalDate());

        List<PurchaseAndReceiptResponse.DailyMaterialCostDTO> list = initializeDailyMaterialCostDTOs(vpuordAmtLists, materialPurchasePlanMonthly, weeklyStartDate);
        mergeDailyMaterialCostDTOs(list);
        return list;
    }

    private List<PurchaseAndReceiptResponse.DailyMaterialCostDTO> initializeDailyMaterialCostDTOs(
            List<VPUORDAmtList> vpuordAmtLists,
            MaterialPurchasePlanMonthly materialPurchasePlanMonthly,
            LocalDate weeklyStartDate) {

        return new ArrayList<>(vpuordAmtLists.stream().map(vpuordAmtList -> {
            PurchaseAndReceiptResponse.DailyMaterialCostDTO dto = new PurchaseAndReceiptResponse.DailyMaterialCostDTO(
                    vpuordAmtList, materialPurchasePlanMonthly);
            List<VPUORDAmtList> amtListDays = vpuordAmtListDayRepo.findByCreateDateTimeAfterAndSnapShot(
                    weeklyStartDate.atStartOfDay(), true);
            dto.monthly(amtListDays);
            return dto;
        }).toList());
    }

    private void mergeDailyMaterialCostDTOs(List<PurchaseAndReceiptResponse.DailyMaterialCostDTO> list) {
        PurchaseAndReceiptResponse.DailyMaterialCostDTO mergedDTO = new PurchaseAndReceiptResponse.DailyMaterialCostDTO();
        list.removeIf(dailyMaterialCostDTO -> {
            boolean shouldMerge =
                    dailyMaterialCostDTO.getCategory().equals("ACCY") || dailyMaterialCostDTO.getCategory().equals("제품");
            if (shouldMerge) {
                mergedDTO.merge(dailyMaterialCostDTO);
                mergedDTO.setCategory("기타");
                return true;
            }
            return false;
        });
        list.add(mergedDTO);
    }

    private List<PurchaseAndReceiptResponse.StockStatusDTO> createStockStatusDTOS(
            List<VLGWHStockAmtList> list

    ) {
        List<PurchaseAndReceiptResponse.StockStatusDTO> statusDTOS = initializeStockStatusDTOS(list);
        mergeStockStatusDTOs(statusDTOS);
        return statusDTOS;
    }

    private List<PurchaseAndReceiptResponse.StockStatusDTO> initializeStockStatusDTOS(List<VLGWHStockAmtList> list) {
        return list.stream().map(vlgwhStockAmtList ->
                PurchaseAndReceiptResponse.StockStatusDTO.builder()
                        .category(vlgwhStockAmtList.getSortation())
                        .directPurchaseMaterial(vlgwhStockAmtList.getDirectPurchasingMaterials())
                        .subcontractMaterial(vlgwhStockAmtList.getPrivateSupplyMaterial())
                        .totalMaterial(vlgwhStockAmtList.getTotal())
                        .build()
        ).collect(Collectors.toList());
    }

    private void mergeStockStatusDTOs(List<PurchaseAndReceiptResponse.StockStatusDTO> list) {
        PurchaseAndReceiptResponse.StockStatusDTO mergedDTO = new PurchaseAndReceiptResponse.StockStatusDTO();
        list.removeIf(statusDTO -> {
            boolean shouldMerge = statusDTO.getCategory().equals("ACCY") || statusDTO.getCategory().equals("제품");
            if (shouldMerge) {
                mergedDTO.merge(statusDTO);
                return true;
            }
            return false;
        });
        list.add(mergedDTO);
    }

    private List<PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO> createWarehouseMaterialStatusDTOS(List<VLGWHStockWHAmtList> whAmtLists) {
        return whAmtLists.stream().map(PurchaseAndReceiptResponse.WarehouseMaterialStatusDTO::new).collect(Collectors.toList());
    }

    @Cacheable(value = "purchaseData", cacheManager = "longLivedCacheManager")
    public PurchaseAndReceiptResponse getPurchaseAndReceiptResponse(boolean isSnapShot) {
        LocalDateTime time = Util.getTime();

        List<VLGWHStockAmtList> vlgwhStockAmtLists = vlgwhStockAmtListRepo.findByCreateDateTimeAfterAndSnapShot(time,
                isSnapShot);
        List<VPUORDAmtList> vpuordAmtLists = vpuordAmtListDayRepo.findByCreateDateTimeAfterAndSnapShot(time,
                isSnapShot);
        List<VLGWHStockWHAmtList> vlgwhStockWHAmtLists = vlgwhStockWHAmtListRepo.findByCreateDateTimeAfterAndSnapShot(
                time, isSnapShot);
        MaterialPurchasePlanMonthly materialPurchasePlanMonthlies = materialPurchasePlanMonthlyRepo.findByCreateDateTimeAfterAndSnapShot(
                time, isSnapShot);


        return PurchaseAndReceiptResponse.builder()
                .dailyMaterialCost(createDailyMaterialCostDTOS(vpuordAmtLists, materialPurchasePlanMonthlies))
                .stockStatus(createStockStatusDTOS(vlgwhStockAmtLists))
                .warehouseMaterialStatus(createWarehouseMaterialStatusDTOS(vlgwhStockWHAmtLists))
                .build();
    }

    public PurchaseAndReceiptResponse searchPurchaseById(Long id) {    // 먼저 해당 ID로 VLGWHStockAmtList 데이터를 조회
        VLGWHStockAmtList vlgwhStockAmtList = vlgwhStockAmtListRepo.findById(id).orElse(null);

        if (vlgwhStockAmtList == null) {
            return null;
        }
        LocalDateTime startDateTime = vlgwhStockAmtList.getCreateDateTime().toLocalDate().atStartOfDay();
        LocalDateTime endDateTime = startDateTime.withHour(23).withMinute(59).withSecond(59);

        List<VLGWHStockAmtList> vlgwhStockAmtLists = vlgwhStockAmtListRepo.findByCreateDateTimeBetweenAndSnapShot(
                startDateTime, endDateTime, true
        );
        List<VPUORDAmtList> vpuordAmtLists = vpuordAmtListDayRepo.findByCreateDateTimeBetweenAndSnapShot(
                startDateTime, endDateTime, true
        );
        List<VLGWHStockWHAmtList> vlgwhStockWHAmtLists = vlgwhStockWHAmtListRepo.findByCreateDateTimeBetweenAndSnapShot(
                startDateTime, endDateTime, true
        );
        MaterialPurchasePlanMonthly materialPurchasePlanMonthlies = materialPurchasePlanMonthlyRepo.findByCreateDateTimeBetweenAndSnapShot(
                startDateTime, endDateTime, true
        );

        return PurchaseAndReceiptResponse.builder()
                .dailyMaterialCost(createDailyMaterialCostDTOS(vpuordAmtLists, materialPurchasePlanMonthlies))
                .stockStatus(createStockStatusDTOS(vlgwhStockAmtLists))
                .warehouseMaterialStatus(createWarehouseMaterialStatusDTOS(vlgwhStockWHAmtLists))
                .build();
    }

    public List<SearchReportResponse> searchPurchase(LocalDate startDate, LocalDate endDate) {
        List<SearchReportResponse> searchReportResponses = new ArrayList<>();
        List<VLGWHStockAmtList> vlgwhStockAmtLists = vlgwhStockAmtListRepo.findByCreateDateTimeBetweenAndSnapShot(
                startDate.atStartOfDay(), endDate.atStartOfDay(), true);

        // 중복 방지를 위해 Set을 사용해 처리된 날짜를 저장
        Set<LocalDate> processedDates = new HashSet<>();

        vlgwhStockAmtLists.forEach(vlgwhStockAmtList -> {
            if (vlgwhStockAmtList.getCreateDateTime() != null) {
                LocalDate createDate = vlgwhStockAmtList.getCreateDateTime().toLocalDate();
                if (!createDate.isBefore(startDate) && !createDate.isAfter(endDate)) {
                    // 이미 처리된 날짜는 제외
                    if (!processedDates.contains(createDate)) {
                        String formattedDate = createDate.format(Util.formatter);
                        searchReportResponses.add(SearchReportResponse.builder()
                                .id(vlgwhStockAmtList.getId())
                                .name("구매 및 입고 현황")
                                .createDate(formattedDate)
                                .build());

                        // 처리된 날짜를 Set에 추가
                        processedDates.add(createDate);
                    }
                }
            }
        });

        return searchReportResponses;
    }

    private LocalDate getFirstDayOfWeek(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return date.with(weekFields.dayOfWeek(), 1);  // 1은 ISO 기준에서 월요일을 의미
    }

}
