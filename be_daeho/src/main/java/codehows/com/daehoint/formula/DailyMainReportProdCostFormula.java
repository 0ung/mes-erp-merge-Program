package codehows.com.daehoint.formula;

import codehows.com.daehoint.entity.ProcessProductionReport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * <b>DailyMainReportProdCostFormula</b><br />
 * <p></p>
 * 메인 생산일보의 생산 비용과 관련된 다양한 비용 항목을 계산하는 유틸리티 클래스입니다.<br />
 * 원재료비, 부자재비, 생산 가공비, 손실 비용 등을 포함한 총 생산 직접 투입 비용을 계산합니다.<br />
 *
 * <p><b>주요 기능:</b></p><br />
 * - 원재료비 및 부자재비 합계를 계산.<br />
 * - 생산 가공비, SM&IM 비용, 외주 비용 등을 구분하여 합산.<br />
 * - 불량, 정지/비생산, 재작업 등 손실 비용 항목과 합계 계산.<br />
 * - 총 생산 실적 금액과 총 생산 직접 투입 비용 계산.<br />
 */
@Component
public class DailyMainReportProdCostFormula {
    //원재료비
    public double rawMaterialCost(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process-> Optional.ofNullable(process.getMaterialTotalSum()).orElse(0.0)).sum();
    }

    //부자재
    public double subsidiaryMaterialCost(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process -> Optional.ofNullable(process.getProcessUsageSubMaterialSum()).orElse(0.0)).sum();
    }

    //합계
    public double totalMaterialCost(double rawMaterialCost, double subsidiaryMaterialCost) {
        return rawMaterialCost + subsidiaryMaterialCost;
    }

    //생산가공비
    public double productionCost(List<ProcessProductionReport> productionReports) {
        return productionReports.stream().filter(report -> {
            String category = report.getCategory();
            return category != null && (category.equals("DIP ASSY") || category.equals("PCB ASSY")
                    || category.equals("CASE ASSY")
                    || category.equals("PACKING ASSY"));
        }).mapToDouble(process -> Optional.ofNullable(process.getTotalProductionProcessingCostSum()).orElse(0.0)).sum();
    }

    //SM&IM
    public double smImCost(List<ProcessProductionReport> productionReports) {
        return productionReports.stream().filter(report -> {
            String category = report.getCategory();
            return category != null && (category.equals("IM ASSY") || category.equals("SM ASSY"));
        }).mapToDouble(process -> Optional.ofNullable(process.getTotalProductionProcessingCostSum()).orElse(0.0)).sum();
    }

    //공정내 외주
    public double externalProcessingCost(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .filter(report -> {
                    String category = report.getCategory();
                    return category != null && (category.equals("DIP ASSY") || category.equals("PCB ASSY")
                            || category.equals("CASE ASSY")
                            || category.equals("PACKING ASSY"));
                })
                .mapToDouble(process -> Optional.ofNullable(process.getProcessInOutsourcingWorkSum()).orElse(0.0)).sum();
    }

    //합계
    public double totalProductionCost(double productionCost, double smImCost, double externalProcessingCost) {
        return productionCost + smImCost + externalProcessingCost;
    }

    //총생산실적 금액
    public double totalProductionAmount(double totalMaterialCost, double totalProductionCost) {
        return totalMaterialCost + totalProductionCost;
    }

    //손실비용 -> 불량 -> 수량
    public double lossHandlingCnt(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process -> Optional.ofNullable(process.getDefectiveQuantity()).orElse(0.0)).sum();
    }

    //손실비용 -> 불량 -> 비용
    public double lossHandlingCost(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process -> Optional.ofNullable(process.getDefectiveCost()).orElse(0.0)).sum();
    }

    //손실비용 -> 정지 & 비생산 -> 시간
    public double nonProductiveHour(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process -> Optional.ofNullable(process.getStopAndNonproductiveHours()).orElse(0.0)).sum();
    }

    //손실비용 -> 정지 & 비생산 -> 비용
    public double nonProductiveCost(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process -> Optional.ofNullable(process.getStopAndNonproductiveCost()).orElse(0.0)).sum();
    }

    //손실비용 -> 재작업 -> 시간
    public double reworkCnt(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process -> Optional.ofNullable(process.getReworkManHours()).orElse(0.0)).sum();
    }

    //손실비용 -> 재작업 -> 비용
    public double reworkCost(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process -> Optional.ofNullable(process.getReworkCost()).orElse(0.0)).sum();
    }
    //손실비용 -> 합계 -> 비용
    public double totalLossCost(double lossHandingCost, double nonProductiveCost, double reworkCost) {
        return lossHandingCost + nonProductiveCost + reworkCost;
    }
    //총 생산 직접 투입 비용
    public double totalProductionDirectInputCost(double totalLossCost, double totalProductionAmount){
        return totalLossCost + totalProductionAmount;
    }

}
