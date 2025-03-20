package codehows.com.daehoint.formula;

import codehows.com.daehoint.constants.Category;
import codehows.com.daehoint.entity.ProcessProductionReport;
import codehows.com.daehoint.entity.ProductionPerformanceStatus;
import codehows.com.daehoint.entity.mes.DailyWorkLoss;
import codehows.com.daehoint.entity.mes.ProductionDailyList;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <b>DailyProcessReportManInputFormula</b><br />
 * <p></p>
 * 공정 생산일보의 인력 투입 및 공수와 관련된 다양한 지표를 계산하는 유틸리티 클래스입니다.<br />
 * 공수 투입, 가동율, LOSS율, 작업 능률 등 생산성과 효율성을 평가하기 위한 메서드를 제공합니다.<br />
 *
 * <p><b>주요 기능:</b></p><br />
 * - 공정 생산일보 데이터를 바탕으로 가용 공수, 작업 공수, LOSS율 등 다양한 지표를 계산.<br />
 * - 작업 능률 및 공수 종합 효율성 평가.<br />
 * - 정지, 재작업, 비생산 공수 등을 포함한 상세한 공수 분석.<br />
 */
@Component
public class DailyProcessReportManInputFormula {


    //가용인원
    public double availablePersonnel(ProcessProductionReport report) {
        return report == null ? 0.0 : report.getAvailablePersonnel();
    }

    //표준 공수
    public double standardManHours(List<ProductionPerformanceStatus> status) {
        return status.stream().mapToDouble(data -> Optional.ofNullable(data.getManHours()).orElse(0.0)
                * Optional.ofNullable(data.getCompletedQuantity()).orElse(0.0)
        ).sum();
    }

    //가용공수
    public double availableManHours(ProcessProductionReport report) {
        return report == null ? 0.0 : report.getAvailableManHours();
    }

    //정지공수
    public double stopManHours(List<DailyWorkLoss> dailyWorkLoss) {
        return dailyWorkLoss.stream().mapToDouble(value -> {
            if (!value.getLossReason().equals("재작업")) {
                return Optional.ofNullable(value.getLossAmount()).orElse(0.0);
            }
            return 0.0;
        }).sum();
    }

    //재작업공수
    public double reworkManHours(List<DailyWorkLoss> dailyWorkLoss) {
        return dailyWorkLoss.stream().mapToDouble(value -> {
            if (value.getLossReason().equals("재작업")) {
                return Optional.ofNullable(value.getLossAmount()).orElse(0.0);
            }
            return 0.0;
        }).sum();
    }

    //비생산공수
    public double nonProductiveManHours(double stop, double rework) {
        return stop + rework;
    }

    //부하공수
    public double workloadManHours(double availableManHours, double nonProductiveManHours) {
        return availableManHours != 0.0 ? availableManHours - nonProductiveManHours : 0.0;
    }

    //실동공수
    public double actualManHours(double workloadManHours, double stopManHours) {
        return workloadManHours != 0.0 ? workloadManHours - stopManHours : 0.0;
    }

    //작업공수
    public double workingManHours(double actualManHours, double reworkManHours) {
        return actualManHours != 0.0 ? actualManHours - reworkManHours : 0.0;
    }

    //작업 능률
    public double workEfficiency(double workloadManHours, double standardManHours) {
        return workloadManHours > 0.0 ? (standardManHours / workloadManHours) * 100 : 0.0;
    }

    //실동 효율
    public double actualEfficiency(double actualManHours, double standardManHours) {
        return actualManHours != 0.0 ? (standardManHours / actualManHours) * 100 : 0.0;
    }

    //LOSS율
    public double lossRate(double availableManHours, double stopManHours, double reworkManHours) {
        return availableManHours != 0.0 ? ((stopManHours + reworkManHours) / availableManHours) * 100 : 0.0;
    }

    //공수투입율
    public double manHourInputRate(double workloadManHours, double availableManHours) {
        return workloadManHours == 0.0 ? 100.0 : (workloadManHours / availableManHours) * 100;
    }

    //공수 가동율
    public double manHourOperationRate(double workloadManHours, double workingManHours) {
        return workloadManHours != 0.0 ? (workingManHours / workloadManHours) * 100 : 0.0;
    }

    //공수 종합효율
    public double overallManHourEfficiency(double workEfficiency, double manHourInputRate, double manHourOperationRate) {
        return (workEfficiency > 0.0 && manHourInputRate > 0.0 && manHourOperationRate > 0.0)
                ? (workEfficiency + manHourInputRate + manHourOperationRate) / 3
                : 0.0;
    }

    //추가 확장
    //데이터 미정
    public double overtimePersonnel(ProcessProductionReport report) {
        return report == null ? 0.0 : report.getOvertimePersonnel();
    }

    public double overtimeManHours(ProcessProductionReport report) {
        return report == null ? 0.0 : report.getOvertimeManHours();
    }

    public double additionalInputRate(double availableManHours, double overtimeManHours) {
        return overtimeManHours == 0.0 ? 0.0 : overtimeManHours / availableManHours * 100;
    }

    public double fluxOnTime(LocalDateTime time, Category category) {
        if (category == Category.DIP) {
            DayOfWeek dayOfWeek = time.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.FRIDAY) {
                return 4.0;
            } else {
                return 9.0;
            }
        }
        return 0.0;
    }

    public double fluxOperationTime(List<ProductionDailyList> dailyList, Category category) {
        if (category == Category.DIP) {
            return dailyList.stream()
                    .mapToDouble(production -> Optional.ofNullable(production.getEquipmentUseTime()).orElse(0.0)).sum();
        }
        return 0.0;
    }

    public double fluxOperationRate(double fluxOnTime, double fluxOperationTime, Category category) {
        if (category == Category.DIP) {
            return fluxOperationTime == 0 ? 0.0 : fluxOperationTime / fluxOnTime * 100;
        }
        return 0.0;
    }
}
