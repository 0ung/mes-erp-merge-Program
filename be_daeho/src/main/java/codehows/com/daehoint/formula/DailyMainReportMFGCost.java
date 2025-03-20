package codehows.com.daehoint.formula;

import codehows.com.daehoint.entity.ProcessProductionReport;
import codehows.com.daehoint.entity.mes.EstimatedExpenses;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <b>DailyMainReportMFGCost</b><br />
 * <p></p>
 * 메인 생산일보의 제조원가와 관련된 다양한 비용 계산을 수행하는 유틸리티 클래스입니다.<br />
 * 직접비, 간접비, 설비 감가 비용 등을 포함한 총 제조원가 및 생산 이익을 계산합니다.<br />
 *
 * <p><b>주요 기능:</b></p><br />
 * - 직접비, 간접비, 일반 관리비, 설비 감가 비용 등 제조 원가 항목 계산.<br />
 * - 총 제조원가와 생산 이익 및 이익률을 계산.<br />
 * - `EstimatedExpenses`와 `ProcessProductionReport` 데이터를 활용한 비용 분석.<br />
 */
@Component
public class DailyMainReportMFGCost {
    //직접비
    public double directPersonnelCost() {
        return 0.0;
    }

    //간접비
    public double indirectPersonnelCost(EstimatedExpenses estimatedExpenses) {
        return estimatedExpenses == null ? 0.0 : estimatedExpenses.getOverheadCost();
    }

    //일반관리비
    public double generalManagementCost(EstimatedExpenses estimatedExpenses) {
        return estimatedExpenses == null ? 0.0 : estimatedExpenses.getGeneralManagementExpense();
    }

    //판관비
    public double salesCost(EstimatedExpenses estimatedExpenses) {
        return estimatedExpenses == null ? 0.0 : estimatedExpenses.getSalesManagementExpense();
    }

    //설비감가
    public double equipmentDepreciationCost(EstimatedExpenses estimatedExpenses) {
        return estimatedExpenses == null ? 0.0 : estimatedExpenses.getDepreciationCost();
    }

    //기타
    public double otherCost(EstimatedExpenses estimatedExpenses) {
        return estimatedExpenses == null ? 0.0 : estimatedExpenses.getEtc();
    }

    //합계
    public double totalManufacturingCost(double directPersonnelCost, double indirectPersonnelCost, double generalManagementCost, double salesCost, double equipmentDepreciationCost, double otherCost) {
        return directPersonnelCost + indirectPersonnelCost + generalManagementCost + salesCost + equipmentDepreciationCost + otherCost;
    }

    //총 생산 비용
    public double totalProductCost(double totalManufacturingCost, double totalProductionDirectInputCost) {
        return totalManufacturingCost + totalProductionDirectInputCost;
    }

    //총 생산 견적금액
    public double totalEstimateCost(List<ProcessProductionReport> productionReports) {
        return productionReports.stream().mapToDouble(ProcessProductionReport::getEstimateCostTotal).sum();
    }

    //총 생산 이익
    public double totalProfit(double totalEstimateCost, double totalProductCost){
        return totalEstimateCost - totalProductCost;
    }

    //총생산 이익율
    public double netProfit(double totalProductCost, double totalProfit){
        return totalProductCost == 0.0 ? 0.0 : totalProfit / totalProductCost * 100;
    }

    //치구,설비,환경
    public double investCost(){
        return 0.0;
    }

    //지출(인건비)
    public double totalExpenditure(){
        return 0.0;
    }

}
