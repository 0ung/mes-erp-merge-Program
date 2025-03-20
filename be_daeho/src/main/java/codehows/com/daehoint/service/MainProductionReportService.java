package codehows.com.daehoint.service;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.dto.MainProductionReportResponse;
import codehows.com.daehoint.dto.SearchReportResponse;
import codehows.com.daehoint.entity.MainProductionReport;
import codehows.com.daehoint.entity.ProcessProductionReport;
import codehows.com.daehoint.entity.mes.EstimatedExpenses;
import codehows.com.daehoint.entity.mes.WorkerRetention;
import codehows.com.daehoint.formula.DailyMainReportAttendanceFormula;
import codehows.com.daehoint.formula.DailyMainReportMFGCost;
import codehows.com.daehoint.formula.DailyMainReportManInputFormula;
import codehows.com.daehoint.formula.DailyMainReportProdCostFormula;
import codehows.com.daehoint.repository.MainProductionReportRepo;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>메인 생산 일보 서비스</p>
 *
 * <p>이 클래스는 일일 메인 생산 보고서를 생성, 병합, 저장, 조회하는 역할을 담당합니다.
 * 근태, 공수 입력, 생산 비용, 제조 비용 등의 데이터를 계산하고,
 * 이를 기반으로 메인 생산 일보를 생성합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>근태, 공수 입력, 생산 비용, 제조 비용 등의 계산</li>
 *   <li>계산된 데이터를 병합하여 일일 메인 생산 일보 생성</li>
 *   <li>생성된 보고서를 데이터베이스에 저장</li>
 *   <li>캐싱된 데이터를 활용한 성능 최적화</li>
 *   <li>특정 기간 및 ID를 기반으로 데이터 검색</li>
 * </ul>
 *
 * <p>의존성:</p>
 * <ul>
 *   <li>{@code MainProductionReportRepo}: 메인 생산 일보 데이터 저장소</li>
 *   <li>{@code CachedDataService}: 캐시된 데이터 관리</li>
 *   <li>{@code Formula 클래스들}: 계산 처리 (근태, 생산 비용 등)</li>
 * </ul>
 *
 * <p>참고 사항:</p>
 * <ul>
 *   <li>캐싱을 통해 데이터 조회 성능을 향상시킵니다.</li>
 *   <li>생성된 보고서를 데이터베이스에 저장하거나 검색할 수 있습니다.</li>
 *   <li>{@code isSnapshot} 설정 여부에 따라 데이터가 마감 데이터인지 결정됩니다.</li>
 * </ul>
 */

@Service
@RequiredArgsConstructor
public class MainProductionReportService {

    private final MainProductionReportRepo mainProductionReportRepo;
    private final DailyMainReportAttendanceFormula attendanceFormula;
    private final DailyMainReportManInputFormula manInputFormula;
    private final DailyMainReportProdCostFormula prodCostFormula;
    private final DailyMainReportMFGCost mfgCost;
    private final CachedDataService cachedDataService;

    @Setter
    public boolean isSnapshot;

    public void saveMainProductionReport(MainProductionReport mainProductionReport) {
        mainProductionReport.setSnapShot(isSnapshot);
        mainProductionReportRepo.save(mainProductionReport);
    }

    @CacheEvict(cacheNames = {"report_mainProductionReport", "processReportResponse",
            "processStockResponse", "lossData", "flowData", "performanceData", "purchaseData"},
            cacheManager = "longLivedCacheManager", allEntries = true)
    public MainProductionReport createMainProductionReport() {
        MainProductionReport attendance = createAttendanceData();
        MainProductionReport manInput = createManInputManageData();
        MainProductionReport productionCost = createProductionCost();
        MainProductionReport mfgCost = createManufacturingCost(productionCost.getTotalProductionDirectInputCost());

        return mergeMainProductionReport(attendance, manInput, productionCost, mfgCost);
    }

    private MainProductionReport mergeMainProductionReport(
            MainProductionReport attendanceData,
            MainProductionReport manInputData,
            MainProductionReport productionCost,
            MainProductionReport mfgCost
    ) {
        MainProductionReport first = Util.mergeMainProductionReport(attendanceData, manInputData);
        MainProductionReport second = Util.mergeMainProductionReport(productionCost, mfgCost);
        return Util.mergeMainProductionReport(first, second);
    }

    private MainProductionReport createAttendanceData() {
        List<WorkerRetention> list = cachedDataService.getWorkerRetentions(isSnapshot);
        return calcAttendanceData(list);
    }

    private MainProductionReport calcAttendanceData(List<WorkerRetention> list) {
        int productionPersonnel = attendanceFormula.productionPersonnel(list);
        int directPersonnel = attendanceFormula.directPersonnel(list);
        int supportPersonnel = attendanceFormula.supportPersonnel(list);
        int etcPersonnel = attendanceFormula.etcPersonnel(list);
        int totalPersonnel = attendanceFormula.totalPersonnel(productionPersonnel, directPersonnel, supportPersonnel, etcPersonnel);
        double indirectManHours = attendanceFormula.indirectManHours(list);
        double directManHours = attendanceFormula.directManHours(list);
        double totalManHours = attendanceFormula.totalManHours(indirectManHours, directManHours);
        double directYearlyLeavePersonnel = attendanceFormula.directYearlyLeavePersonnel(list);
        double directYearlyLeaveHours = attendanceFormula.directYearlyLeaveHours(list);
        double directPartTimePersonnel = attendanceFormula.directPartTimePersonnel(list);
        double directPartTimeHours = attendanceFormula.directPartTimeHours(list);
        double directEtcPersonnel = attendanceFormula.directEtcPersonnel(list);
        double directEtcPersonnelTime = attendanceFormula.directEtcPersonnelTime(list);
        double directTotalPersonnel = attendanceFormula.directTotalPersonnel(directYearlyLeavePersonnel, directPartTimePersonnel, directEtcPersonnel);
        double directTotalPersonnelTime = attendanceFormula.directTotalPersonnelTime(directYearlyLeaveHours
                , directPartTimeHours, directEtcPersonnel);
        double subYearlyLeavePersonnel = attendanceFormula.subYearlyLeavePersonnel(list);
        double subYearlyLeaveHours = attendanceFormula.subYearlyLeaveHours(list);
        double subPartTimePersonnel = attendanceFormula.subPartTimePersonnel(list);
        double subPartTimeHours = attendanceFormula.subPartTimeHours(list);
        double subTotalPersonnel = attendanceFormula.subTotalPersonnel(subYearlyLeavePersonnel, subPartTimePersonnel);
        double subTotalHours = attendanceFormula.subTotalHours(subYearlyLeaveHours, subPartTimeHours);
        double directMan = attendanceFormula.directMan(directPersonnel, directTotalPersonnel);
        double directTime = attendanceFormula.directTime(directManHours, directTotalPersonnelTime);
        double subMan = attendanceFormula.subMan(productionPersonnel, subTotalPersonnel);
        double subTime = attendanceFormula.subTime(indirectManHours, subTotalHours);
        double etcMan = attendanceFormula.etcMan(supportPersonnel, etcPersonnel);
        double etcTime = attendanceFormula.etcTime(list);
        double totalMan = attendanceFormula.totalMan(directMan, subMan, etcMan);
        double totalTime = attendanceFormula.totalTime(directTime, subTime, etcTime);
        return MainProductionReport.builder()
                .productionPersonnel(productionPersonnel)
                .directPersonnel(directPersonnel)
                .supportPersonnel(supportPersonnel)
                .etcPersonnel(etcPersonnel)
                .totalPersonnel(totalPersonnel)
                .indirectManHours(indirectManHours)
                .directManHours(directManHours)
                .totalManHours(totalManHours)
                .directYearlyLeavePersonnel(directYearlyLeavePersonnel)
                .directYearlyLeaveHours(directYearlyLeaveHours)
                .directPartTimePersonnel(directPartTimePersonnel)
                .directPartTimeHours(directPartTimeHours)
                .directEtcPersonnel(directEtcPersonnel)
                .directEtcPersonnelTime(directEtcPersonnelTime)
                .directTotalPersonnel(directTotalPersonnel)
                .directTotalPersonnelTime(directTotalPersonnelTime)
                .subYearlyLeavePersonnel(subYearlyLeavePersonnel)
                .subYearlyLeaveHours(subYearlyLeaveHours)
                .subPartTimePersonnel(subPartTimePersonnel)
                .subPartTimeHours(subPartTimeHours)
                .subTotalPersonnel(subTotalPersonnel)
                .subTotalHours(subTotalHours)
                .directMan(directMan)
                .directTime(directTime)
                .subMan(subMan)
                .subTime(subTime)
                .etcMan(etcMan)
                .etcTime(etcTime)
                .totalMan(totalMan)
                .totalTime(totalTime)
                .availablePersonnel(totalMan)
                .attendanceRemark("")
                .build();

    }

    private MainProductionReport createManInputManageData() {
        List<ProcessProductionReport> productionReports = cachedDataService.getProcessProductionReport(isSnapshot);
        return calcManInputManageData(productionReports);
    }

    private MainProductionReport calcManInputManageData(List<ProcessProductionReport> productionReports) {
        double availableManHours = manInputFormula.availableManHours(productionReports);
        double standardManHours = manInputFormula.standardManHours(productionReports);
        double nonProductiveManHours = manInputFormula.nonProductiveManHours(productionReports);
        double loadManHours = manInputFormula.loadManHours(productionReports);
        double stoppedManHours = manInputFormula.stoppedManHours(productionReports);
        double reworkManHours = manInputFormula.reworkManHours(productionReports);
        double actualManHours = manInputFormula.actualManHours(productionReports);
        double workingManHours = manInputFormula.workingManHours(productionReports);

        double workEfficiency = manInputFormula.workEfficiency(workingManHours, standardManHours);
        double actualEfficiency = manInputFormula.actualEfficiency(workingManHours, actualManHours);
        double lossRate = manInputFormula.lossRate(availableManHours, stoppedManHours, reworkManHours);
        double manHourInputRate = manInputFormula.manHourInputRate(availableManHours, loadManHours);
        double manHourOperationRate = manInputFormula.manHourOperationRate(loadManHours, workingManHours);
        double totalEfficiency = manInputFormula.totalEfficiency(workEfficiency, manHourInputRate, manHourOperationRate);

        int specialSupportPersonnel = manInputFormula.specialSupportPersonnel(productionReports);
        double specialSupportManHours = manInputFormula.specialSupportManHours(productionReports);
        double additionalInputRate = manInputFormula.additionalInputRate(availableManHours, specialSupportManHours);
        double fluxEquipmentRunningTime = manInputFormula.fluxEquipmentRunningTime(productionReports);
        double fluxEquipmentRunningRate = manInputFormula.fluxEquipmentRunningRate(productionReports);

        return MainProductionReport.builder()
                .availableManHours(availableManHours)                      // 가용 공수
                .standardManHours(standardManHours)                        // 표준 공수
                .nonProductiveManHours(nonProductiveManHours)              // 비생산 공수
                .loadManHours(loadManHours)                                // 부하 공수
                .stoppedManHours(stoppedManHours)                          // 정지 공수
                .reworkManHours(reworkManHours)                            // 재작업 공수
                .actualManHours(actualManHours)                            // 실동 공수
                .workingManHours(workingManHours)                          // 작업 공수
                .workEfficiency(workEfficiency)                            // 작업 능률
                .actualEfficiency(actualEfficiency)                        // 실동 효율
                .lossRate(lossRate)                                        // Loss율
                .manHourInputRate(manHourInputRate)                        // 공수 투입율
                .manHourOperationRate(manHourOperationRate)                // 공수 가동율
                .totalEfficiency(totalEfficiency)                          // 공수 종합 효율
                .specialSupportPersonnel(specialSupportPersonnel)          // 지원 인원
                .specialSupportManHours(specialSupportManHours)            // 지원 공수
                .additionalInputRate(additionalInputRate)                  // 추가 투입율
                .fluxEquipmentRunningTime(fluxEquipmentRunningTime)
                .fluxEquipmentRunningRate(fluxEquipmentRunningRate)
                .solderingEquipmentRunningRate(fluxEquipmentRunningRate)
                .solderingEquipmentRunningTime(fluxEquipmentRunningTime)
                .costRemark("")
                .manPowerRemark("")
                .attendanceRemark("")
                .build();
    }

    private MainProductionReport createProductionCost() {
        List<ProcessProductionReport> productionReports = cachedDataService.getProcessProductionReport(isSnapshot);
        return calcProductionCost(productionReports);
    }

    private MainProductionReport calcProductionCost(List<ProcessProductionReport> productionReports) {
        // 재료비 관련 계산
        double rawMaterialCost = prodCostFormula.rawMaterialCost(productionReports);                     // 원자재비
        double subsidiaryMaterialCost = prodCostFormula.subsidiaryMaterialCost(productionReports);       // 부자재비
        double totalMaterialCost = prodCostFormula.totalMaterialCost(rawMaterialCost, subsidiaryMaterialCost); // 총 재료비

        // 생산 가공비 관련 계산
        double productionCost = prodCostFormula.productionCost(productionReports);                      // 생산 가공비
        double smImCost = prodCostFormula.smImCost(productionReports);                                  // SM & IM 비용
        double externalProcessingCost = prodCostFormula.externalProcessingCost(productionReports);      // 외주 비용
        double totalProductionCost = prodCostFormula.totalProductionCost(productionCost, smImCost, externalProcessingCost); // 총 생산 비용

        // 총 생산 실적 금액
        double totalProductionAmount = prodCostFormula.totalProductionAmount(totalMaterialCost, totalProductionCost);

        // 손실 비용 계산
        double lossHandlingCnt = prodCostFormula.lossHandlingCnt(productionReports);                    // 불량 수량
        double lossHandlingCost = prodCostFormula.lossHandlingCost(productionReports);                  // 불량 비용
        double nonProductiveHour = prodCostFormula.nonProductiveHour(productionReports);                // 정지/비생산 시간
        double nonProductiveCost = prodCostFormula.nonProductiveCost(productionReports);                // 정지/비생산 비용
        double reworkCnt = prodCostFormula.reworkCnt(productionReports);                                // 재작업 시간
        double reworkCost = prodCostFormula.reworkCost(productionReports);                              // 재작업 비용
        double totalLossCost = prodCostFormula.totalLossCost(lossHandlingCost, nonProductiveCost, reworkCost); // 손실 비용 합계

        // 총 생산 직접 투입 비용
        double totalProductionDirectInputCost = prodCostFormula.totalProductionDirectInputCost(totalLossCost, totalProductionAmount);

        return MainProductionReport.builder()
                .rawMaterialCost(rawMaterialCost)                           // 원자재비
                .subsidiaryMaterialCost(subsidiaryMaterialCost)             // 부자재비
                .totalMaterialCost(totalMaterialCost)                       // 총 재료비
                .productionCost(productionCost)                             // 생산 가공비
                .smImCost(smImCost)                                         // SM & IM 비용
                .externalProcessingCost(externalProcessingCost)             // 외주 비용
                .totalProductionCost(totalProductionCost)                   // 총 생산 비용
                .totalProductionAmount(totalProductionAmount)               // 총 생산 실적 금액
                .lossHandlingCnt(lossHandlingCnt)                           // 불량 수량
                .lossHandlingCost(lossHandlingCost)                         // 불량 비용
                .nonProductiveTimeHour(nonProductiveHour)                       // 정지/비생산 시간
                .nonProductiveTimeCost(nonProductiveCost)                       // 정지/비생산 비용
                .reworkTimeCnt(reworkCnt)                                       // 재작업 시간
                .reworkTimeCost(reworkCost)                                     // 재작업 비용
                .totalLossCost(totalLossCost)                               // 손실 비용 합계
                .totalProductionDirectInputCost(totalProductionDirectInputCost) // 총 생산 직접 투입 비용
                .build();
    }

    private MainProductionReport createManufacturingCost(double totalProductionDirectInputCost) {
        EstimatedExpenses estimatedExpenses = cachedDataService.getEstimatedExpenses(isSnapshot);
        List<ProcessProductionReport> productionReports = cachedDataService.getProcessProductionReport(isSnapshot);

        return calcManufacturingCost(estimatedExpenses, productionReports, totalProductionDirectInputCost);
    }

    private MainProductionReport calcManufacturingCost(EstimatedExpenses estimatedExpenses, List<ProcessProductionReport> productionReports, double totalProductionDirectInputCost) {
        double directPersonnelCost = mfgCost.directPersonnelCost();
        double indirectPersonnelCost = mfgCost.indirectPersonnelCost(estimatedExpenses);
        double generalManagementCost = mfgCost.generalManagementCost(estimatedExpenses);
        double salesCost = mfgCost.salesCost(estimatedExpenses);
        double equipmentDepreciationCost = mfgCost.equipmentDepreciationCost(estimatedExpenses);
        double otherCost = mfgCost.otherCost(estimatedExpenses);
        double totalManufacturingCost = mfgCost.totalManufacturingCost(
                directPersonnelCost, indirectPersonnelCost, generalManagementCost, salesCost, equipmentDepreciationCost, otherCost
        );
        double totalProductCost = mfgCost.totalProductCost(totalManufacturingCost, totalProductionDirectInputCost);
        double totalEstimateCost = mfgCost.totalEstimateCost(productionReports);
        double totalProfit = mfgCost.totalProfit(totalEstimateCost, totalProductCost);
        double netProfit = mfgCost.netProfit(totalProductCost, totalProfit);
        double investCost = mfgCost.investCost();
        double totalExpenditure = mfgCost.totalExpenditure();

        return MainProductionReport.builder()
                .directPersonnelCost(directPersonnelCost)
                .indirectPersonnelCost(indirectPersonnelCost)
                .generalManagementCost(generalManagementCost)
                .salesCost(salesCost)
                .equipmentDepreciationCost(equipmentDepreciationCost)
                .otherCost(otherCost)
                .totalManufacturingCost(totalManufacturingCost)
                .totalProductCost(totalProductCost)
                .totalEstimateCost(totalEstimateCost)
                .totalProfit(totalProfit)
                .netProfit(netProfit)
                .investCost(investCost)
                .totalExpenditure(totalExpenditure)
                .build();
    }

    //접속 시 검색
    public MainProductionReportResponse getMainProductionReportResponse() {
        LocalDateTime time = Util.getTime();
        return cachedDataService.getMainProductionReportResponse();
    }

    //ID 검색
    public MainProductionReportResponse searchByIdDailyMainReport(Long id) {
        MainProductionReport report = mainProductionReportRepo.findById(id).orElse(null);
        return MainProductionReportResponse.convertToDTO(report);
    }

    //기간 검색
    public List<SearchReportResponse> searchMainReport(LocalDate startDate, LocalDate endDate) {
        List<MainProductionReport> mainProductionReports = mainProductionReportRepo.findByCreateDateTimeBetweenAndSnapShot(
                startDate.atStartOfDay(), endDate.atStartOfDay(), true);
        List<SearchReportResponse> searchReportResponses = new ArrayList<>();
        mainProductionReports.forEach(mainProductionReport -> {
            String formattedDate = mainProductionReport.getCreateDateTime().format(Util.formatter);
            searchReportResponses.add(SearchReportResponse.builder()
                    .id(mainProductionReport.getId())
                    .name("메인 생산 일보")
                    .createDate(formattedDate)
                    .build());
        });

        return searchReportResponses;
    }
}
