package codehows.com.daehoint.formula;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.entity.ProcessProductionReport;
import codehows.com.daehoint.entity.ProductionPerformanceStatus;
import codehows.com.daehoint.entity.StandardInfo;
import codehows.com.daehoint.entity.erp.VPDPartsList;
import codehows.com.daehoint.entity.mes.LotResultList;
import codehows.com.daehoint.repository.StandardInfoRepo;
import codehows.com.daehoint.repository.VPDPartsListRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


/**
 * <b>DailyProcessReportProductionCostFormula</b><br />
 * <p></p>
 * 공정 생산일보의 생산 비용과 관련된 다양한 지표를 계산하는 유틸리티 클래스입니다.<br />
 * 표준 공수 비율, 총 생산 재료비, 공정 가공비 합계 등 생산성과 비용 분석을 위한 메서드를 제공합니다.<br />
 *
 * <p><b>주요 기능:</b></p><br />
 * - 생산성과 비용 데이터를 기반으로 재료비, 가공비, 손실 비용 등 다양한 지표를 계산.<br />
 * - 공정별 실제 비용, 이익률, 손실률 등을 평가.<br />
 * - 견적가 합계 및 투입 금액 계산을 통한 생산 성과 분석.<br />
 */
@Component
@RequiredArgsConstructor
public class DailyProcessReportProductionCostFormula {

    private final VPDPartsListRepo vpdPartsListRepo;
    private final StandardInfoRepo standardInfoRepo;

    //표준공수 비율
    public double manRate() {
        StandardInfo standardInfo = standardInfoRepo.findById(1L).orElse(null);
        if (standardInfo != null) {
            return standardInfo.getPower();
        }
        return 4.5;
    }

    //총 생산 재료비 합계
    public double totalProductionMaterialCostSum(List<ProductionPerformanceStatus> statusList) {
        return statusList.stream()
                .map(production -> Optional.ofNullable(production.getMaterialCost()).orElse(0.0))
                .reduce(0.0, Double::sum);
    }

    //공정사용 부자재 합계
    public double processUsageSubMaterialSum(List<LotResultList> statusList) {
        return statusList.stream()
                .map(status -> Optional.ofNullable(vpdPartsListRepo.findByDaehoCodeAndRecent(status.getItemCd(), true)).map(
                partLists -> Optional.ofNullable(partLists.getSubExpenses()).orElse(0.0))
                .orElse(0.0)).reduce(0.0, Double::sum);
    }

    //공정 총 재료비 합계
    public double materialTotalSum(double totalMaterialCost, double processUsageSubMaterialSum) {
        return totalMaterialCost + processUsageSubMaterialSum;
    }

    //총 생산 처리 비용 합산
    public double totalProductionProcessingCostSum(List<ProductionPerformanceStatus> statusList) {
        return statusList.stream()
                .map(production -> Optional.ofNullable(production.getPerformanceProcessingCost()).orElse(0.0)).reduce(0.0, Double::sum);
    }

    //외주 작업 비용
    public double processInOutsourcingWorkSum(ProcessProductionReport previousReport) {
        return Util.checkNullAndSetDefault(previousReport, ProcessProductionReport::getProcessInOutsourcingWorkSum, 0.0);
    }

    //공정 총 가공비 합계
    public double processTotalSum(double processingCost, double outsourcingCost) {
        return processingCost + outsourcingCost;
    }

    //총 생산 실적 합계
    public double totalProductionActualSum(double materialCostSum, double processTotalSum) {
        return materialCostSum + processTotalSum;
    }

    //불량 수량 합산
    public double defectiveQuantity(List<ProductionPerformanceStatus> statusList) {
        return statusList.stream()
                .map(production -> Optional.ofNullable(production.getDefectiveQuantity()).orElse(0.0)).reduce(0.0, Double::sum);
    }

    //불량 비용 합산
    public double defectiveCost(List<ProductionPerformanceStatus> statusList) {
        return statusList.stream().map(status ->
                Optional.ofNullable(status.getDefectiveQuantity()).orElse(0.0)
                        * Optional.ofNullable(status.getSubtotal()).orElse(0.0)
        ).reduce(0.0, Double::sum);
    }

    //정지 및 비생산 공수 시간
    public double stopAndNonproductiveHours(ProcessProductionReport presentReport) {
        return presentReport.getStopManHours() == null || presentReport.getNonProductiveManHours() == null ?
                0.0 : presentReport.getStopManHours() + presentReport.getNonProductiveManHours();
    }

    //정지 및 비생산 공수 비용
    public double stopAndNonproductiveCost(double stopAndNonProductiveHours, double manRate) {
        return stopAndNonProductiveHours * manRate;
    }

    //재작업 공수
    public double reworkHours(ProcessProductionReport presentReport) {
        return presentReport.getReworkHours() == null ? 0.0 : presentReport.getReworkHours();
    }

    //재작업 비용
    public double reworkCost(double reworkHours, double manRate) {
        return reworkHours * manRate;
    }

    //총 손실 비용
    public double totalCost(double defectCost,
                            double stopAndNonProductiveCost,
                            double reworkCost) {
        return stopAndNonProductiveCost + reworkCost + defectCost;
    }

    //제조 간접비
    public double manufacturingExpenseIndirect() {
        return 0.0;
    }

    //일반 관리비
    public double manufacturingExpenseGeneralAdmin() {
        return 0.0;
    }

    //판관비
    public double manufacturingExpenseSellingAndAdmin() {
        return 0.0;
    }

    //설비 감가 및 기타
    public double manufacturingExpenseDepreciationEtc() {
        return 0.0;
    }

    //합계
    public double manufacturingExpenseTotal() {
        return 0.0;
    }

    //견적가 합계
    public double estimateCostTotal(List<ProductionPerformanceStatus> statusList) {
        return statusList.stream()
                .map(production -> Optional.ofNullable(production.getTotalProduction()).orElse(0.0)).reduce(0.0, Double::sum);
    }

    //투입 금액
    public double processTotalProductionInputAmount(ProcessProductionReport presentReport
            , double processInOutsourcingWorkSum
            , double totalProductionProcessingCostSum
            , double manRate) {
        return presentReport == null ? 0.0 : Optional.ofNullable(presentReport.getAvailableManHours()).orElse(0.0) * manRate + processInOutsourcingWorkSum + totalProductionProcessingCostSum;
    }

    //실적 이익
    public double processTotalProductionActualProfit(double estimateCost, double inputAmount) {
        return estimateCost - inputAmount;
    }

    //이익률
    public double processTotalProductionProfitRate(double actualProfit, double estimateCost) {
        return estimateCost != 0.0 ? actualProfit / estimateCost * 100 : 0.0;
    }

    //손실률
    public double processTotalProductionLossRate(double totalProductionActualSum, double totalCost) {
        return totalProductionActualSum != 0.0 ? (totalCost / totalProductionActualSum) * 100 : 0.0;
    }

    //재료비율
    public double processTotalProductionMaterialRate(double totalProductionActualSum, double materialTotalSum) {
        return totalProductionActualSum != 0.0 ? (materialTotalSum / totalProductionActualSum) * 100 : 0.0;
    }

    //가공비율
    public double processTotalProductionProcessingRate(double totalProductionActualSum, double processTotalSum) {
        return totalProductionActualSum != 0.0 ? processTotalSum / totalProductionActualSum * 100 : 0.0;
    }

}

