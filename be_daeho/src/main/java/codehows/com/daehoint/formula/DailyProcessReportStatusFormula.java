package codehows.com.daehoint.formula;

import codehows.com.daehoint.entity.erp.VPDPartsList;
import codehows.com.daehoint.entity.mes.LotResultList;
import codehows.com.daehoint.entity.mes.ProductionDailyList;
import codehows.com.daehoint.repository.LotResultListRepo;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <b>DailyProcessReportStatusFormula</b><br />
 * <p></p>
 * 공정 생산일보의 상태와 관련된 다양한 계산을 수행하는 유틸리티 클래스입니다.<br />
 * 불량율, 달성율, 가공비 등의 주요 생산 지표를 계산하며 생산성과 비용 데이터를 분석하는 데 사용됩니다.<br />
 *
 * <p><b>주요 기능:</b></p><br />
 * - 불량율, 달성율, 재공 수량 등의 생산 상태를 계산.<br />
 * - 가공비 및 실적 재료비를 계산하여 비용 효율성을 평가.<br />
 * - 총 생산 금액 및 실적 금액 계산을 통해 생산성과 재무 성과를 분석.<br />
 */
@Component
public class DailyProcessReportStatusFormula {

    //계획수량
    public double qty(ProductionDailyList productionDailyList, LotResultList lotResultList) {
        if (productionDailyList == null) {
            return lotResultList.getQty();
        } else {
            return Double.parseDouble(productionDailyList.getQty());
        }
    }

    //투입수량
    public double inputQty(ProductionDailyList productionDailyList, double outQty, double defeatQty) {
        if (productionDailyList == null) {
            return outQty + defeatQty;
        } else {
            return Double.parseDouble(productionDailyList.getInQty());
        }
    }

    //불량수량
    public double defeatQty(ProductionDailyList productionDailyList, LotResultList lotResultList) {
        if (productionDailyList == null) {
            return lotResultList.getDefectQty();
        } else {
            return Double.parseDouble(productionDailyList.getDefectQty());
        }
    }

    //생산완료수량
    public double outQty(ProductionDailyList productionDailyList, LotResultList lotResultList) {
        if (productionDailyList == null) {
            if (lotResultList.getLotState().equals("생산완료") && (lotResultList.getOutQty() == null || lotResultList.getOutQty() == 0)) {
                return lotResultList.getQty();
            } else if (lotResultList.getLotState().equals("생산중")) {
                return 0.0;
            } else {

                return lotResultList.getOutQty();
            }
        } else {
            return Double.parseDouble(productionDailyList.getOutQty());
        }
    }

    //불량율 계산
    public double defectRate(double inQty, double defectQty) {
        return inQty != 0 ? Math.round((defectQty / inQty) * 100.0) : 0.0;
    }

    //달성율 계산
    public double achievementRate(double qty, double outQty) {
        return qty != 0 ? Math.round((outQty / qty) * 100.0) : 0.0;
    }

    //재공수량
    public double workInProgressQty(double outQty, double inQty) {
        return outQty - inQty;
    }

    //가공비 계산
    public double calcProcessingCost(VPDPartsList vpdPartsList, LotResultList lotResultList) {
        double processingCost = 0.0;

        if (vpdPartsList != null && vpdPartsList.getProcessingCost() == 0.0 && lotResultList != null) {
            processingCost = lotResultList.getStandardTime() * 4.5;
        } else if (vpdPartsList == null && lotResultList == null) {
            processingCost = 0.0;
        } else if (vpdPartsList != null && vpdPartsList.getProcessingCost() != 0.0) {
            processingCost = vpdPartsList.getProcessingCost();
        }

        return processingCost;
    }

    //소계
    public double subTotal(double costRawMaterials, double processingCost) {
        return costRawMaterials + processingCost;
    }

    //총 생산 금액
    public double totalProduction(double outQty, double estimateUnitPrice) {
        return outQty * estimateUnitPrice;
    }

    //실적 재료비
    public double performanceMaterialCost(double costRawMaterials, double outQty) {
        return costRawMaterials * outQty;
    }

    //실적 가공비
    public double performanceProcessingCost(double processingCost, double outQty) {
        return processingCost * outQty;
    }

    //총 실적 금액
    public double totalPerformanceAmount(double subTotal, double outQty) {
        return subTotal * outQty;
    }

}
