package codehows.com.daehoint.formula;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.entity.ProductionPerformanceStatus;
import codehows.com.daehoint.entity.erp.VPDSPFWorkReportQC;
import codehows.com.daehoint.entity.mes.ProductionDailyList;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <b>ProcessStockFormula</b><br />
 * <p></p>
 * 공정 재고와 관련된 다양한 계산을 수행하는 유틸리티 클래스입니다.<br />
 * 제품 비용 및 수량 계산, 공정 진행 상태, 불량률 계산 등 재고와 관련된 데이터를 처리합니다.<br />
 *
 * <p><b>주요 기능:</b></p><br />
 * - 제품별 가공비, 자재비 및 총 비용 계산.<br />
 * - 공정 재공(WIP), QC 검사 대기 및 완료 상태의 수량 및 비용 계산.<br />
 * - 불량 수량 및 비용 분석.<br />
 * - 전체 재고 수량 및 비용 합산.<br />
 *
 */
@Component
public class ProcessStockFormula {

    //세트당 가공비
    public double pricePerSet(ProductionPerformanceStatus status) {
        return Util.checkNullAndSetDefault(status, ProductionPerformanceStatus::getPricePerSet, 0.0);
    }

    //제품명
    public String productName(ProductionPerformanceStatus status) {
        return Util.checkNullAndSetDefault(status, ProductionPerformanceStatus::getProductName, "실적 입력전");
    }

    //modelNo
    public String modelNo(ProductionPerformanceStatus status) {
        return Util.checkNullAndSetDefault(status, ProductionPerformanceStatus::getModelNo, "Unknown modelNo");
    }

    //사양
    public String specification(ProductionPerformanceStatus status) {
        return Util.checkNullAndSetDefault(status, ProductionPerformanceStatus::getSpecification, "");
    }

    //자재비
    public double materialCost(ProductionPerformanceStatus status) {
        return Util.checkNullAndSetDefault(status, ProductionPerformanceStatus::getMaterialCost, 0.0);
    }

    //가공비
    public double processingCost(ProductionPerformanceStatus status) {
        return Util.checkNullAndSetDefault(status, ProductionPerformanceStatus::getProcessingCost, 0.0);
    }

    //합계
    public double totalCost(ProductionPerformanceStatus status) {
        return Util.checkNullAndSetDefault(status, ProductionPerformanceStatus::getMaterialCost, 0.0)
                + Util.checkNullAndSetDefault(status, ProductionPerformanceStatus::getProcessingCost, 0.0);
    }

    //공정재공 -> 수량
    public double wipQuantity(ProductionDailyList dailyList) {
        return Util.checkNullAndSetDefault(dailyList, dailyList1 -> Double.parseDouble(dailyList1.getInQty()), 0.0);
    }

    //공정재공 -> 비용
    public double wipCost(double wipQuantity, double pricePerSet) {
        return wipQuantity * pricePerSet;
    }

    //공정완료(QC검사대기) -> 수량
    public double qcPendingQuantity(ProductionPerformanceStatus status) {
        return Util.checkNullAndSetDefault(status, ProductionPerformanceStatus::getCompletedQuantity, 0.0);
    }

    //공정완료(QC검사대기) -> 비용
    public double qcPendingCost(double qcPendingQuantity, double pricePerSet) {
        return qcPendingQuantity * pricePerSet;
    }

    //공정투입대기(QC검사완료) -> 비용
    public double qcPassedQuantity(List<VPDSPFWorkReportQC> qcList) {
        return qcList.stream().filter(v -> v != null && v.getPassQuantityInspection() != null)
                .mapToDouble(VPDSPFWorkReportQC::getPassQuantityInspection)
                .sum();
    }

    //공정투입대기(QC검사완료) -> 비용
    public double qcPassedCost(double qcPassedQuantity, double pricePerSet) {
        return qcPassedQuantity * pricePerSet;
    }

    //불량 -> 수량
    public double defectiveQuantity(List<VPDSPFWorkReportQC> qcList, ProductionPerformanceStatus status) {
        return qcList.stream()
                .filter(v -> v != null && v.getDefectiveQuantityInspection() != null)
                .mapToDouble(VPDSPFWorkReportQC::getDefectiveQuantityInspection)
                .sum() + Util.checkNullAndSetDefault(status, ProductionPerformanceStatus::getDefectiveQuantity, 0.0);
    }

    //불량 -> 비용
    public double defectiveCost(double defectiveQuantity, double pricePerSet) {
        return defectiveQuantity * pricePerSet;
    }

    //합계 -> 수량
    public double totalQuantity(double wipQuantity, double qcPendingQuantity, double qcPassedQuantity) {
        return wipQuantity + qcPendingQuantity + qcPassedQuantity;
    }

    //합계 -> 비용
    public double totalCostSummary(double wipCost, double qcPendingCost, double qcPassedCost) {
        return wipCost + qcPendingCost + qcPassedCost;
    }

}
