package codehows.com.daehoint.service;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.constants.Category;
import codehows.com.daehoint.dto.AdditionalDataRequest;
import codehows.com.daehoint.dto.DailyProcessReportResponse;
import codehows.com.daehoint.dto.SearchReportResponse;
import codehows.com.daehoint.entity.ProcessProductionReport;
import codehows.com.daehoint.entity.ProductionPerformanceStatus;
import codehows.com.daehoint.entity.erp.VPDPartsList;
import codehows.com.daehoint.entity.erp.VPDSPFWorkReportQC;
import codehows.com.daehoint.entity.mes.DailyWorkLoss;
import codehows.com.daehoint.entity.mes.LotResultList;
import codehows.com.daehoint.entity.mes.ProductionDailyList;
import codehows.com.daehoint.formula.DailyProcessReportManInputFormula;
import codehows.com.daehoint.formula.DailyProcessReportProductionCostFormula;
import codehows.com.daehoint.formula.DailyProcessReportStatusFormula;
import codehows.com.daehoint.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 공정 생산 일보 서비스 클래스
 *
 * <p>이 클래스는 공정 생산 일보 데이터를 생성, 계산, 병합, 저장하는 서비스입니다.
 * 주요 데이터로는 공정 실적 현황, 생산 비용, 공수 투입 관리 등이 있으며,
 * 데이터베이스와 상호작용하여 데이터를 관리하고, 캐싱을 활용해 조회 성능을 최적화합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>공정 실적 현황 데이터 생성 및 저장</li>
 *   <li>생산 비용 및 공수 투입 관리 데이터 계산 및 병합</li>
 *   <li>공정 생산 일보 데이터 조회 및 캐싱</li>
 * </ul>
 *
 * <p>의존성:</p>
 * <ul>
 *   <li>{@code LotResultListRepo}: Lot 결과 데이터 조회</li>
 *   <li>{@code ProductionDailyListRepo}: 생산 일일 리스트 데이터 조회</li>
 *   <li>{@code VPDPartsListRepo}: 부품 리스트 데이터 조회</li>
 *   <li>{@code ProcessProductionReportRepo}: 공정 생산 리포트 데이터 관리</li>
 *   <li>{@code ProductionPerformanceStatusRepo}: 공정 실적 상태 데이터 관리</li>
 *   <li>{@code DailyWorkLossRepo}: 작업 손실 데이터 관리</li>
 *   <li>{@code VPDSPFWorkReportQCRepo}: 품질 관리 데이터 조회</li>
 *   <li>{@code DailyProcessReportStatusFormula}: 공정 실적 관련 계산 공식</li>
 *   <li>{@code DailyProcessReportManInputFormula}: 공수 투입 관리 계산 공식</li>
 *   <li>{@code DailyProcessReportProductionCostFormula}: 생산 비용 계산 공식</li>
 *   <li>{@code CachedDataService}: 캐싱된 데이터 관리</li>
 * </ul>
 *
 * <p>참고:</p>
 * <ul>
 *   <li>캐싱을 통해 자주 조회되는 데이터를 메모리에 저장해 성능을 향상시킵니다.</li>
 *   <li>{@code Category}와 {@code isSnapshot} 값을 설정해 카테고리별로 데이터를 처리합니다.</li>
 *   <li>공정 실적 데이터를 생성, 병합, 저장하며, 조회 시 필요에 따라 캐싱된 데이터를 활용합니다.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DailyProcessReportService {

    private final LotResultListRepo lotResultListRepo;
    private final ProductionDailyListRepo productionDailyListRepo;
    private final VPDPartsListRepo vpdPartsListRepo;
    private final ProcessProductionReportRepo processProductionReportRepo;
    private final ProductionPerformanceStatusRepo productionPerformanceStatusRepo;
    private final DailyWorkLossRepo dailyWorkLossRepo;
    private final VPDSPFWorkReportQCRepo vpdspfWorkReportQCRepo;
    private final DailyProcessReportStatusFormula formulaStatus;
    private final DailyProcessReportManInputFormula formulaManInput;
    private final DailyProcessReportProductionCostFormula formulaProductionCost;
    private final CachedDataService cachedDataService;

    @Setter
    private Category category;

    @Setter
    private boolean isSnapshot;

    public void createProductionPerformanceReport() {
        List<ProductionPerformanceStatus> list = createProductionData();
        saveProductionPerformanceStatus(list);
    }

    public void createProcessProductionReport() {
        ProcessProductionReport manage = createManInputManageData();
        ProcessProductionReport cost = createProductionCost(manage);
        ProcessProductionReport report = mergeProcessProductionReport(cost, manage);
        report.setSnapShot(isSnapshot);
        saveProcessProductionReport(report);
    }

    private List<ProductionPerformanceStatus> createProductionData() {
        final LocalDateTime time = Util.getTime();
        List<ProductionPerformanceStatus> state = new ArrayList<>();
        // 시간, 카테고리, 스냅샷 기준으로 생산 데이터 조회
        List<ProductionDailyList> productionDailyList = cachedDataService.getProductionDailyLists(category, isSnapshot);

        List<LotResultList> lotResultLists = lotResultListRepo.findByResultDateAndItemNameAndCreateDateTimeAfterAndSnapShot
                (time.toLocalDate().toString(), category.getDescription(), time, false);

        List<String> compareLotId = getExistLotId(productionDailyList, lotResultLists);

        // 조회된 데이터 처리
        productionDailyList.forEach(productionDaily -> {
            if (productionDaily.getLotState().equals("작업대기")) {
                return; // 작업 대기 상태는 제외
            }
            // 공정 실적 상태 생성 및 저장
            ProductionPerformanceStatus status = createProductionPerformanceStatus(productionDaily);
            status.setSnapShot(isSnapshot);
            state.add(status);
        });

        //ProductionDailyList에 없는 LOTID 가져와 공정실적현황 생성
        lotResultLists.forEach(lotResultList -> {
            if (compareLotId.contains(lotResultList.getLotId())) {
                return;
            } else {
                ProductionPerformanceStatus status = createProductionPerformanceStatus(lotResultList);
                state.add(status);
            }
        });

        return state;
    }

    private List<String> getExistLotId(List<ProductionDailyList> dailyLists, List<LotResultList> lotResultLists) {
        Set<String> dailyListIds = new HashSet<>();
        for (ProductionDailyList productionDailyList : dailyLists) {
            dailyListIds.add(productionDailyList.getLotId());
        }
        List<String> list = new ArrayList<>();
        for (LotResultList lotResultList : lotResultLists) {
            if (dailyListIds.contains(lotResultList.getLotId())) {
                list.add(lotResultList.getLotId());
            }
        }

        return list;
    }

    private void saveProductionPerformanceStatus(List<ProductionPerformanceStatus> productionPerformanceStatus) {
        if (productionPerformanceStatus == null || productionPerformanceStatus.isEmpty()) {
            return;
        }
        productionPerformanceStatusRepo.saveAll(productionPerformanceStatus);
    }

    private ProductionPerformanceStatus createProductionPerformanceStatus(
            ProductionDailyList dailyList
    ) {
        LocalDateTime time = Util.getTime();
        //복수 LotResultList가 존재 가능 해당 데이터는 결과 값만 병합해서 처리
        List<LotResultList> lotResultList = lotResultListRepo.findByLotIdAndCreateDateTimeAfterAndSnapShot(dailyList.getLotId(), time, isSnapshot);


        VPDPartsList vpdPartsList = vpdPartsListRepo.findByDaehoCodeAndRecent(dailyList.getItemCd(), true);
        List<VPDSPFWorkReportQC> vpdspfWorkReportQC = vpdspfWorkReportQCRepo.findByOperationInstructionNumber(dailyList.getLotId());
        Long monthlyCnt = lotResultListRepo.sumByItemCd(dailyList.getItemCd(), Util.getMonth().format(new Date()),
                time, isSnapshot);
        LotResultList list = null;
        if (lotResultList.size() > 1) {
            list = mergeLotResult(lotResultList);
        } else {
            list = lotResultList.isEmpty() ? null : lotResultList.get(0);
        }


        VPDSPFWorkReportQC reportQC = vpdspfWorkReportQC.isEmpty() ? null : vpdspfWorkReportQC.get(vpdspfWorkReportQC.size() - 1);
        ProductionPerformanceStatus status = calcProductionPerformanceStatus(list, dailyList,
                vpdPartsList, reportQC, monthlyCnt);

        status.setCategory(category.getDescription());
        status.setSnapShot(isSnapshot);
        return status;
    }

    private LotResultList mergeLotResult(List<LotResultList> lotResultLists) {
        double outQty = lotResultLists.stream().mapToDouble(value -> {
            if (value.getOutQty() == null) {
                return 0;
            } else {
                return value.getOutQty();
            }
        }).sum();
        lotResultLists.get(0).setOutQty(outQty);
        return lotResultLists.get(0);
    }

    private ProductionPerformanceStatus createProductionPerformanceStatus(LotResultList lotResultList) {
        LocalDateTime time = Util.getTime();
        VPDPartsList vpdPartsList = vpdPartsListRepo.findByDaehoCodeAndRecent(lotResultList.getItemCd(), true);
        List<VPDSPFWorkReportQC> vpdspfWorkReportQC = vpdspfWorkReportQCRepo.findByOperationInstructionNumber(lotResultList.getLotId());
        Long monthlyCnt = lotResultListRepo.sumByItemCd(lotResultList.getItemCd(), Util.getMonth().format(new Date()),
                time, isSnapshot);
        VPDSPFWorkReportQC reportQC = vpdspfWorkReportQC.isEmpty() ? null : vpdspfWorkReportQC.get(vpdspfWorkReportQC.size() - 1);
        ProductionPerformanceStatus status = calcProductionPerformanceStatus(lotResultList, null, vpdPartsList, reportQC, monthlyCnt);
        status.setCategory(category.getDescription());
        status.setSnapShot(isSnapshot);
        return status;
    }

    private double getOutQty(LotResultList lotResultList) {
        if (lotResultList.getLotState().equals("생산완료") && lotResultList.getOutQty() == null) {
            return lotResultList.getQty();
        } else if (lotResultList.getLotState().equals("생산중") && lotResultList.getOutQty() == null) {
            return 0.0;
        } else {
            return lotResultList.getOutQty();
        }
    }

    private ProductionPerformanceStatus calcProductionPerformanceStatus(LotResultList lotResultList, ProductionDailyList productionDailyList, VPDPartsList vpdPartsList, VPDSPFWorkReportQC vpdspfWorkReportQC, Long monthly) {
        double qty = formulaStatus.qty(productionDailyList, lotResultList);
        double outQty = formulaStatus.outQty(productionDailyList, lotResultList);
        double defectQty = formulaStatus.defeatQty(productionDailyList, lotResultList);
        double inQty = formulaStatus.inputQty(productionDailyList, outQty, defectQty);


        // 각 필드별 계산 수행 (null이면 모두 0으로 처리)
        double defectRate = formulaStatus.defectRate(inQty, defectQty);
        double achievementRate = formulaStatus.achievementRate(qty, outQty);
        double workInProgressQty = formulaStatus.workInProgressQty(outQty, inQty);

        // vpdPartsList가 null일 경우 기본값 0으로 처리
        double costRawMaterials =
                Util.checkNullAndSetDefault(
                        vpdPartsList, vp -> vpdPartsList.getCostRawMaterials(), 0.0
                );

        double processingCost = formulaStatus.calcProcessingCost(vpdPartsList, lotResultList);

        double estimatedUnitPrice = Util.checkNullAndSetDefault(
                vpdPartsList, vp -> vpdPartsList.getEstimatedUnitPrice(), 0.0
        );

        double subTotal = formulaStatus.subTotal(costRawMaterials, processingCost);
        double totalProduction = formulaStatus.totalProduction(outQty, estimatedUnitPrice);
        double performanceMaterialCost = formulaStatus.performanceMaterialCost(costRawMaterials, outQty);
        double performanceProcessingCost = formulaStatus.performanceProcessingCost(processingCost, outQty);
        double totalPerformanceAmount = formulaStatus.totalPerformanceAmount(subTotal, outQty);

        // ProductionDataDTO 생성
        return ProductionPerformanceStatus.builder()
                .lotNo(productionDailyList == null ? lotResultList.getLotId() : productionDailyList.getLotId())
                .productName(lotResultList == null ? "실적 입력 전" : lotResultList.getCategoryItemValue02())
                .modelNo(productionDailyList == null ? lotResultList.getCategoryItemValue03() : productionDailyList.getCategoryItemValue03())
                .specification(vpdspfWorkReportQC == null ? "WorkReportQC 데이터 없음" : vpdspfWorkReportQC.getModelName())
                .depart(productionDailyList == null ? lotResultList.getDepartment() : productionDailyList.getDepartment())
                .itemName(productionDailyList == null ? lotResultList.getItemName() : productionDailyList.getItemName())
                .itemCd(productionDailyList == null ? lotResultList.getItemCd() : productionDailyList.getItemCd())
                .unit("ea")
                .plannedQuantity(qty)
                .inputQuantity(inQty)
                .defectiveQuantity(defectQty)
                .defectRate(defectRate)
                .completedQuantity(outQty)
                .achievementRate(achievementRate)
                .workInProgressQuantity(workInProgressQty)
                .materialCost(costRawMaterials)
                .manHours(lotResultList == null ? 0 : lotResultList.getStandardTime())
                .processingCost(processingCost)
                .subtotal(subTotal)
                .pricePerSet(estimatedUnitPrice)
                .totalProduction(totalProduction)
                .performanceMaterialCost(performanceMaterialCost)
                .performanceProcessingCost(performanceProcessingCost)
                .totalPerformanceAmount(totalPerformanceAmount)
                .monthlyCumulativeProduction(monthly != null ? Double.valueOf(monthly) : 0.0)
                .build();
    }

    private ProcessProductionReport createManInputManageData() {
        LocalDateTime time = Util.getTime();
        List<ProductionPerformanceStatus> status = cachedDataService.getProductionPerformanceStatus(category, isSnapshot);
        List<DailyWorkLoss> dailyWorkLosses = dailyWorkLossRepo.findByCategoryAndSnapShotAndCreateDateTimeAfter(
                category.getDescription(), isSnapshot, time);
        ProcessProductionReport previousReport = processProductionReportRepo.findByAvailableHour(category.getDescription(),
                time.minusHours(1), time, false);
        List<ProductionDailyList> list = cachedDataService.getProductionDailyLists(category, isSnapshot);
        return calcManInputManageData(previousReport, status, dailyWorkLosses, list);
    }

    private ProcessProductionReport mergeProcessProductionReport(ProcessProductionReport manage, ProcessProductionReport cost) {
        return Util.mergeProcessProductionReport(manage, cost);
    }

    private void saveProcessProductionReport(ProcessProductionReport processProductionReport) {
        if (processProductionReport == null) {
            return;
        }
        processProductionReport.setCategory(category.getDescription());
        processProductionReportRepo.save(processProductionReport);
    }

    private ProcessProductionReport calcManInputManageData(
            ProcessProductionReport previousReport,
            List<ProductionPerformanceStatus> status,
            List<DailyWorkLoss> dailyWorkLoss,
            List<ProductionDailyList> productionDailyLists) {

        double availablePersonnel = formulaManInput.availablePersonnel(previousReport);
        double standardManHours = formulaManInput.standardManHours(status);
        double availableManHours = formulaManInput.availableManHours(previousReport);
        double stopManHours = formulaManInput.stopManHours(dailyWorkLoss);
        double reworkManHours = formulaManInput.reworkManHours(dailyWorkLoss);
        double nonProductiveManHours = formulaManInput.nonProductiveManHours(stopManHours, reworkManHours);
        double workloadManHours = formulaManInput.workloadManHours(availableManHours, nonProductiveManHours);
        double actualManHours = formulaManInput.actualManHours(workloadManHours, stopManHours);
        double workingManHours = formulaManInput.workingManHours(actualManHours, reworkManHours);
        double workEfficiency = formulaManInput.workEfficiency(actualManHours, standardManHours);
        double actualEfficiency = formulaManInput.actualEfficiency(actualManHours, standardManHours);
        double lossRate = formulaManInput.lossRate(availableManHours, stopManHours, reworkManHours);
        double manHoursInputRate = formulaManInput.manHourInputRate(workloadManHours, availableManHours);
        double manHoursOperationRate = formulaManInput.manHourOperationRate(workloadManHours, workingManHours);
        double overallManHourEfficiency = formulaManInput.overallManHourEfficiency(workEfficiency, manHoursInputRate, manHoursOperationRate);
        //데이터 미정
        double overtimePersonnel = formulaManInput.overtimePersonnel(previousReport);
        double overtimeManHours = formulaManInput.overtimeManHours(previousReport);
        double additionalInputRate = formulaManInput.additionalInputRate(overtimePersonnel, overtimeManHours);
        //Soldering 장비는 동일하게 flux와 동일하게 작동되기때문에 구별 X 그래서 soldering 데이터는 FLUX 데이터와 일치
        double fluxOnTime = formulaManInput.fluxOnTime(LocalDateTime.now(), category);
        double fluxOperationTime = formulaManInput.fluxOperationTime(productionDailyLists, category);
        double fluxOperationRate = formulaManInput.fluxOperationRate(fluxOnTime, fluxOperationTime, category);

        return ProcessProductionReport.builder()
                .availablePersonnel(availablePersonnel)
                .standardManHours(standardManHours)
                .availableManHours(availableManHours)
                .stopManHours(stopManHours)
                .reworkManHours(reworkManHours)
                .nonProductiveManHours(nonProductiveManHours)
                .workloadManHours(workloadManHours)
                .actualManHours(actualManHours)
                .workingManHours(workingManHours)
                .workEfficiency(workEfficiency)
                .actualEfficiency(actualEfficiency)
                .lossRate(lossRate)
                .manHourInputRate(manHoursInputRate)
                .manHourOperationRate(manHoursOperationRate)
                .overallManHourEfficiency(overallManHourEfficiency)
                .overtimePersonnel(overtimePersonnel)
                .overtimeManHours(overtimeManHours)
                .additionalInputRate(additionalInputRate)
                .fluxOnTime(fluxOnTime)
                .fluxOperatingTime(fluxOperationTime)
                .fluxOperatingRate(fluxOperationRate)
                .solderingOnTime(fluxOnTime)
                .solderingOperatingTime(fluxOperationTime)
                .solderingOperatingRate(fluxOperationRate)
                .build();
    }

    public ProcessProductionReport createProductionCost(ProcessProductionReport report) {
        LocalDateTime time = Util.getTime();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // 이전 Report 가져오기 (null 체크)
        ProcessProductionReport previousReport = processProductionReportRepo.findByAvailableHour(category.getDescription(),
                time.minusHours(1), time, false);

        // 생산 실적 리스트 가져오기 (null 체크)
        List<ProductionPerformanceStatus> list = cachedDataService.getProductionPerformanceStatus(category, isSnapshot);

        // Lot Result List 가져오기 (null 체크)
        List<LotResultList> lotResultLists = Optional.ofNullable(
                        lotResultListRepo.findByResultDateAndItemNameAndCreateDateTimeAfterAndSnapShot(date, category.getDescription(), time,
                                isSnapshot))
                .orElse(List.of());
        return calcProductionCost(previousReport, report, list, lotResultLists);
    }

    private ProcessProductionReport calcProductionCost(
            ProcessProductionReport previousReport,
            ProcessProductionReport presentReport,
            List<ProductionPerformanceStatus> status,
            List<LotResultList> lotResultLists
    ) {

        double manRate = formulaProductionCost.manRate();
        double totalProductionMaterialCostSum = formulaProductionCost.totalProductionMaterialCostSum(status);
        double processUsageSubMaterialSum = formulaProductionCost.processUsageSubMaterialSum(lotResultLists);
        double materialTotalSum = formulaProductionCost.materialTotalSum(totalProductionMaterialCostSum, processUsageSubMaterialSum);
        double totalProductionProcessingCostSum = formulaProductionCost.totalProductionProcessingCostSum(status);
        double processInOutsourcingWorkSum = formulaProductionCost.processInOutsourcingWorkSum(previousReport);
        double processTotalSum = formulaProductionCost.processTotalSum(totalProductionProcessingCostSum, processInOutsourcingWorkSum);
        double totalProductionActualSum = formulaProductionCost.totalProductionActualSum(materialTotalSum, processTotalSum);
        double defectiveQuantity = formulaProductionCost.defectiveQuantity(status);
        double defectiveCost = formulaProductionCost.defectiveCost(status);
        double stopAndNonproductiveHours = formulaProductionCost.stopAndNonproductiveHours(presentReport);
        double stopAndNonproductiveCost = formulaProductionCost.stopAndNonproductiveCost(stopAndNonproductiveHours, manRate);
        double reworkHours = formulaProductionCost.reworkHours(presentReport);
        double reworkCost = formulaProductionCost.reworkCost(reworkHours, manRate);
        double totalCost = formulaProductionCost.totalCost(defectiveCost, stopAndNonproductiveCost, reworkCost);
        double manufacturingExpenseIndirect = formulaProductionCost.manufacturingExpenseIndirect();
        double manufacturingExpenseGeneralAdmin = formulaProductionCost.manufacturingExpenseGeneralAdmin();
        double manufacturingExpenseSellingAndAdmin = formulaProductionCost.manufacturingExpenseSellingAndAdmin();
        double manufacturingExpenseDepreciationEtc = formulaProductionCost.manufacturingExpenseDepreciationEtc();
        double manufacturingExpenseTotal = formulaProductionCost.manufacturingExpenseTotal();
        double estimateCostTotal = formulaProductionCost.estimateCostTotal(status);
        double processTotalProductionInputAmount = formulaProductionCost.processTotalProductionInputAmount(
                presentReport, processInOutsourcingWorkSum, totalProductionProcessingCostSum, manRate);
        double processTotalProductionActualProfit = formulaProductionCost.processTotalProductionActualProfit(estimateCostTotal, processTotalProductionInputAmount);
        double processTotalProductionProfitRate = formulaProductionCost.processTotalProductionProfitRate(processTotalProductionActualProfit, estimateCostTotal);
        double processTotalProductionLossRate = formulaProductionCost.processTotalProductionLossRate(totalProductionActualSum, totalCost);
        double processTotalProductionMaterialRate = formulaProductionCost.processTotalProductionMaterialRate(totalProductionActualSum, materialTotalSum);
        double processTotalProductionProcessingRate = formulaProductionCost.processTotalProductionProcessingRate(totalProductionActualSum, processTotalSum);
        return ProcessProductionReport.builder()
                .totalProductionMaterialCostSum(totalProductionMaterialCostSum)
                .processUsageSubMaterialSum(processUsageSubMaterialSum)
                .materialTotalSum(materialTotalSum)
                .totalProductionProcessingCostSum(totalProductionProcessingCostSum)
                .processInOutsourcingWorkSum(processInOutsourcingWorkSum)
                .processTotalSum(processTotalSum)
                .totalProductionActualSum(totalProductionActualSum)
                .defectiveQuantity(defectiveQuantity)
                .defectiveCost(defectiveCost)
                .stopAndNonproductiveHours(stopAndNonproductiveHours)
                .stopAndNonproductiveCost(stopAndNonproductiveCost)
                .reworkHours(reworkHours)
                .reworkCost(reworkCost)
                .totalCost(totalCost)
                .manufacturingExpenseIndirect(manufacturingExpenseIndirect)
                .manufacturingExpenseGeneralAdmin(manufacturingExpenseGeneralAdmin)
                .manufacturingExpenseSellingAndAdmin(manufacturingExpenseSellingAndAdmin)
                .manufacturingExpenseDepreciationEtc(manufacturingExpenseDepreciationEtc)
                .manufacturingExpenseTotal(manufacturingExpenseTotal)
                .estimateCostTotal(estimateCostTotal)
                .processTotalProductionInputAmount(processTotalProductionInputAmount)
                .processTotalProductionActualProfit(processTotalProductionActualProfit)
                .processTotalProductionProfitRate(processTotalProductionProfitRate)
                .processTotalProductionLossRate(processTotalProductionLossRate)
                .processTotalProductionMaterialRate(processTotalProductionMaterialRate)
                .processTotalProductionProcessingRate(processTotalProductionProcessingRate)
                .build();
    }


    //기간 검색
    public List<SearchReportResponse> searchProcessReport(String category, LocalDate startDate, LocalDate endDate) {
        List<SearchReportResponse> searchReportResponses = new ArrayList<>();

        List<ProcessProductionReport> report = processProductionReportRepo.findByCreateDateTimeBetweenAndSnapShotAndCategory(
                startDate.atStartOfDay(),
                endDate.atStartOfDay(), true, category);

        report.forEach(report1 -> {
            String formattedDate = report1.getCreateDateTime().format(Util.formatter);

            searchReportResponses.add(SearchReportResponse.builder()
                    .name(category + " 공정 생산 일보")
                    .createDate(formattedDate)
                    .id(report1.getId())
                    .build());
        });

        return searchReportResponses;
    }

    //상시 조회
    @Cacheable(value = "processReportResponse", key = "#category", cacheManager = "longLivedCacheManager")
    public DailyProcessReportResponse getProcessProductionDailyReport(String category) {
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        ProcessProductionReport report = processProductionReportRepo.findByCategoryAndCreateDateTimeAfterAndSnapShot(
                category, date, isSnapshot);
        List<ProductionPerformanceStatus> statusList = productionPerformanceStatusRepo.findByCategoryAndCreateDateTimeAfterAndSnapShot(
                category, date, isSnapshot);
        List<DailyWorkLoss> dailyWorkLosses = dailyWorkLossRepo.findByCategoryAndSnapShotAndCreateDateTimeAfter(
                category, isSnapshot, date);
        return createDailyProcessReportResponse(report, statusList, dailyWorkLosses);
    }

    //검색 조회
    @Cacheable(value = "processReportResponse", key = "#id", cacheManager = "shortLivedCacheManager")
    public DailyProcessReportResponse getProcessProductionDailyReportById(Long id) {
        ProcessProductionReport report = processProductionReportRepo.findById(id).orElse(null);
        assert report != null;
        LocalDate request = report.getCreateDateTime().toLocalDate();
        LocalDateTime startOfDay = request.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = request.atTime(23, 59, 59); // 23:59:59
        List<ProductionPerformanceStatus> statusList = productionPerformanceStatusRepo.findByCreateDateTimeBetweenAndSnapShotAndCategory(
                startOfDay, endOfDay, true, report.getCategory());
        List<DailyWorkLoss> dailyWorkLosses = dailyWorkLossRepo.findByCreateDateTimeBetweenAndSnapShotAndCategory(
                startOfDay, endOfDay, true,
                report.getCategory());
        DailyProcessReportResponse result = createDailyProcessReportResponse(report, statusList, dailyWorkLosses);
        result.setCreateDate(report.getCreateDateTime().format(Util.formatter));
        result.setId(report.getId());
        return result;
    }

    private DailyProcessReportResponse createDailyProcessReportResponse(
            ProcessProductionReport report,
            List<ProductionPerformanceStatus> statusList,
            List<DailyWorkLoss> dailyWorkLosses
    ) {
        DailyProcessReportResponse dailyReport = new DailyProcessReportResponse();
        List<DailyProcessReportResponse.ProductionDataDTO> list = new ArrayList<>();
        statusList.forEach(status -> {
            list.add(Util.mapper.toDTO(status));
        });
        List<DailyProcessReportResponse.ManInputManageDataDTO> manInputManageDataDTOS = new ArrayList<>();
        manInputManageDataDTOS.add(Util.mapper.toManDTO(report));
        List<DailyProcessReportResponse.ProductionCostDataDTO> productionCostDataDTOS = new ArrayList<>();
        productionCostDataDTOS.add(Util.mapper.toCostDTO(report));
        List<DailyProcessReportResponse.TechProblem> techProblems = new ArrayList<>();
        List<DailyProcessReportResponse.StopRisks> stopRisks = new ArrayList<>();
        dailyWorkLosses.forEach(dailyWorkLoss -> {
            if (dailyWorkLoss.getLossReason().equals("ISSUE")) {
                techProblems.add(Util.mapper.toTechProblem(dailyWorkLoss));
            } else {
                stopRisks.add(Util.mapper.toStopRisks(dailyWorkLoss));
            }
        });
        dailyReport.setProductionDataDTO(list);
        dailyReport.setManInputManageDataDTO(manInputManageDataDTOS);
        dailyReport.setProductionCostDataDTO(productionCostDataDTOS);
        dailyReport.setTechProblem(techProblems);
        dailyReport.setStopRisks(stopRisks);
        return dailyReport;
    }

    //공정관련 데이터 업데이트
    @Transactional
    @CacheEvict(cacheNames = "processReportResponse", key = "#dataRequest.category", cacheManager = "longLivedCacheManager")
    public void updateAdditionalData(AdditionalDataRequest dataRequest) {
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        ProcessProductionReport processProductionReport = processProductionReportRepo.findByCategoryAndCreateDateTimeAfterAndSnapShot(
                dataRequest.getCategory(), date, false);

        if (!(dataRequest.getAvailableManHours() == null)) {
            processProductionReport.updateAvailableManHour(dataRequest.getAvailableManHours());
        }
        if (!(dataRequest.getAvailablePersonnel() == null)) {
            processProductionReport.updateAvailablePersonnel(dataRequest.getAvailablePersonnel());
        }
        if (!(dataRequest.getOutSouringCost() == null)) {
            processProductionReport.updateProcessInOutsourcingWorkSum(dataRequest.getOutSouringCost());
        }
        if (!(dataRequest.getOvertimePersonnel() == null)) {
            processProductionReport.updateOvertimePersonnel(dataRequest.getOvertimePersonnel());
        }
        if (!(dataRequest.getOvertimeHours() == null)) {
            processProductionReport.updateOvertimeManHours(dataRequest.getOvertimeHours());
        }
    }

}
