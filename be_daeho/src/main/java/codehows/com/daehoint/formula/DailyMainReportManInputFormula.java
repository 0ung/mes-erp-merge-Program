package codehows.com.daehoint.formula;

import codehows.com.daehoint.constants.Category;
import codehows.com.daehoint.entity.ProcessProductionReport;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <b>DailyMainReportManInputFormula</b><br />
 * <p></p>
 * 메인 생산일보의 공수 투입 데이터를 기반으로 다양한 계산을 수행하는 유틸리티 클래스입니다.<br />
 * 가용 공수, 작업 공수, 효율성, Loss율 등을 계산하여 생산성과 관련된 주요 지표를 제공합니다.<br />
 *
 * <p><b>주요 기능:</b></p><br />
 * - `ProcessProductionReport` 데이터를 기반으로 공수 관련 계산.<br />
 * - 작업 능률, Loss율, 공수 투입율 등 다양한 생산성 지표를 계산.<br />
 * - 특수 지원 인원 및 공수 관련 데이터 계산.<br />
 */
@Component
public class DailyMainReportManInputFormula {

    //가용공수
    public double availableManHours(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process-> Optional.ofNullable(process.getAvailableManHours()).orElse(0.0)).sum();
    }

    //표준공수
    public double standardManHours(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process->Optional.ofNullable(process.getStandardManHours()).orElse(0.0)).sum();
    }

    //비생산공수
    public double nonProductiveManHours(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process->Optional.ofNullable(process.getNonProductiveManHours()).orElse(0.0)).sum();
    }

    //부하공수
    public double loadManHours(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process->Optional.ofNullable(process.getWorkloadManHours()).orElse(0.0)).sum();
    }

    //정지공수
    public double stoppedManHours(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process->Optional.ofNullable(process.getStopManHours()).orElse(0.0)).sum();
    }

    //재작업공수
    public double reworkManHours(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process->Optional.ofNullable(process.getReworkManHours()).orElse(0.0)).sum();
    }

    //실동공수
    public double actualManHours(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process -> Optional.ofNullable(process.getActualManHours()).orElse(0.0)).sum();
    }

    //작업공수
    public double workingManHours(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process->Optional.ofNullable(process.getWorkingManHours()).orElse(0.0)).sum();
    }

    //작업능률
    public double workEfficiency(double workingManHours, double standardManHours) {
        return workingManHours == 0.0 ? 0.0 : standardManHours / workingManHours * 100;
    }

    //실동효율
    public double actualEfficiency(double workingManHours, double actualManHours) {
        return workingManHours == 0.0 ? 0.0 : actualManHours / workingManHours * 100;
    }

    //Loss율
    public double lossRate(double availableManHours, double stoppedManHours, double reworkManHours) {
        return availableManHours == 0.0 ? 0.0 : (stoppedManHours + reworkManHours) / availableManHours * 100;
    }

    //공수투입율
    public double manHourInputRate(double availableManHours, double loadManHours) {
        return availableManHours == 0.0 ? 0.0 : loadManHours / availableManHours * 100;
    }

    //공수가동율
    public double manHourOperationRate(double loadManHours, double workingManHours) {
        return loadManHours == 0.0 ? 0.0 : workingManHours / loadManHours * 100;
    }

    //공수종합효율
    public double totalEfficiency(double workEfficiency, double manHourInputRate, double manHourOperationRate) {
        return (workEfficiency + manHourInputRate + manHourOperationRate) / 3;
    }

    //인원
    public int specialSupportPersonnel(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .map(production->Optional.ofNullable(production.getOvertimePersonnel()).orElse(0.0))
                .reduce(0.0, Double::sum).intValue();
    }

    //공수
    public double specialSupportManHours(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(production -> Optional.ofNullable(production.getOvertimeManHours()).orElse(0.0)).sum();
    }

    //추가투입율
    public double additionalInputRate(double availableManHours, double specialSupportManHours) {
        return availableManHours == 0.0 ? 0.0 : specialSupportManHours / availableManHours * 100;
    }

    //flux 가동시간
    public double fluxEquipmentRunningTime(List<ProcessProductionReport> productionReports) {
        return productionReports.stream()
                .mapToDouble(process -> Optional.ofNullable(process.getFluxOperatingTime()).orElse(0.0)).sum();
    }

    //flux 가동율
    public double fluxEquipmentRunningRate(List<ProcessProductionReport> productionReports) {
        double fluxOperatingTime = productionReports.stream()
                .mapToDouble(process->Optional.ofNullable(process.getFluxOperatingTime()).orElse(0.0)).sum();
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if (dayOfWeek == DayOfWeek.FRIDAY) {
            return fluxOperatingTime / 4.0 * 100;
        } else {
            return fluxOperatingTime / 9.0 * 100;
        }
    }
}
