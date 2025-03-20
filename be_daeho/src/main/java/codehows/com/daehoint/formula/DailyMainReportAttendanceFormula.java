package codehows.com.daehoint.formula;

import codehows.com.daehoint.entity.mes.WorkerRetention;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


/**
 * <b>DailyMainReportAttendanceFormula</b><br />
 * <p></p>
 * 메인 생산일보의 근태 현황 데이터를 기반으로 다양한 계산을 수행하는 유틸리티 클래스입니다.<br />
 * 인원 및 공수 관련 데이터를 처리하여, 생산, 직접, 간접, 지원 등 다양한 항목에 대한 계산 결과를 제공합니다.<br />
 *
 * <p><b>주요 기능:</b></p><br />
 * - `WorkerRetention` 데이터를 기반으로 각종 인원 및 공수 항목 계산.<br />
 * - 간접, 직접, 지원, 기타 항목에 대한 세부 항목별 데이터 제공.<br />
 * - 합계 계산 및 세부 데이터의 통합 처리.<br />
 */
@Component
public class DailyMainReportAttendanceFormula {

    //인원 -> 생산
    public int productionPersonnel(List<WorkerRetention> list) {
        return list.stream()
                .map(work -> Optional.ofNullable(work.getManagePerson()).orElse(0.0))
                .reduce(0.0, Double::sum).intValue();
    }

    //인원 -> 직접
    public int directPersonnel(List<WorkerRetention> list) {
        return list.stream()
                .map(work -> Optional.ofNullable(work.getProdPerson()).orElse(0.0))
                .reduce(0.0, Double::sum).intValue();
    }

    //인원 -> 지원
    public int supportPersonnel(List<WorkerRetention> list) {
        return list.stream()
                .map(work->Optional.ofNullable(work.getAddWorkPerson()).orElse(0.0))
                .reduce(0.0, Double::sum).intValue();
    }

    //인원 -> 기타
    public int etcPersonnel(List<WorkerRetention> list) {
        return list.stream()
                .map(work-> Optional.ofNullable(work.getEtcPerson()).orElse(0.0))
                .reduce(0.0, Double::sum).intValue();
    }

    //인원 -> 합계
    public int totalPersonnel(int productionPersonnel, int directPersonnel, int supportPersonnel, int etcPersonnel) {
        return productionPersonnel + directPersonnel + supportPersonnel + etcPersonnel;
    }

    //공수 -> 간접
    public double indirectManHours(List<WorkerRetention> list) {
        return list.stream().mapToDouble(workerRetention ->
                Optional.ofNullable(workerRetention.getManagePerson()).orElse(0.0)
                        * Optional.ofNullable(workerRetention.getWorkTime()).orElse(0.0)).sum();
    }

    //공수 ->직접
    public double directManHours(List<WorkerRetention> list) {
        return list.stream().mapToDouble(value ->
                Optional.ofNullable(value.getRateTime()).orElse(0.0)).sum();
    }

    //공수 ->합계
    public double totalManHours(double indirectPersonnel, double directPersonnel) {
        return indirectPersonnel + directPersonnel;
    }

    //현황 -> 직접 -> 년차 -> 인원
    public double directYearlyLeavePersonnel(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional
                        .ofNullable(work.getWkPercntHoli()).orElse(0.0)).sum();
    }

    //현황 -> 직접 -> 년차 -> 시간
    public double directYearlyLeaveHours(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional.ofNullable(work.getWkPercntHoliTime())
                        .orElse(0.0)).sum();
    }

    //현황 -> 직접 -> 시간차 -> 인원
    public double directPartTimePersonnel(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional.ofNullable(work.getProdPersonPartTime()).orElse(0.0)).sum();
    }

    //현황 -> 직접 -> 시간차 -> 시간
    public double directPartTimeHours(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional.ofNullable(work.getManagePersonPartTimeHour()).orElse(0.0)).sum();
    }

    //현황 -> 직접 -> 기타 -> 인원
    public double directEtcPersonnel(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional.ofNullable(work.getWkPercntEarly()).orElse(0.0)).sum();
    }

    //현황 -> 직접 -> 기타 -> 시간
    public double directEtcPersonnelTime(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional.ofNullable(work.getWkPercntEarlyTime()).orElse(0.0)).sum();
    }

    //현황 -> 직접 -> 합계 -> 인원
    public double directTotalPersonnel(double directYearlyLeavePersonnel
            , double directPartTimePersonnel, double directEtcPersonnel) {
        return directYearlyLeavePersonnel + directPartTimePersonnel + directEtcPersonnel;
    }

    //현황 -> 직접 -> 합계 -> 시간
    public double directTotalPersonnelTime(double directYearlyLeaveHours,
                                           double directPartTimeHours, double directEtcPersonnelTime) {
        return directYearlyLeaveHours + directPartTimeHours + directEtcPersonnelTime;
    }

    //현황 -> 간접 -> 년차 -> 인원
    public double subYearlyLeavePersonnel(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional.ofNullable(work.getManagePersonYearly()).orElse(0.0)).sum();
    }

    //현황 -> 간접 -> 년차 -> 시간
    public double subYearlyLeaveHours(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional.ofNullable(work.getManagePersonYearlyHour()).orElse(0.0)).sum();
    }

    //현황 -> 간접 -> 시간차 -> 인원
    public double subPartTimePersonnel(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional.ofNullable(work.getProdPersonPartTime()).orElse(0.0)).sum();
    }

    //현황 -> 간접 -> 시간차 -> 시간
    public double subPartTimeHours(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional.ofNullable(work.getManagePersonPartTimeHour()).orElse(0.0)).sum();
    }

    //현황 -> 간접 -> 합계 -> 인원
    public double subTotalPersonnel(double subYearlyLeavePersonnel, double subPartTimePersonnel) {
        return subYearlyLeavePersonnel + subPartTimePersonnel;
    }

    //현황 -> 간접 -> 합계 -> 시간
    public double subTotalHours(double subYearlyLeaveHours, double subPartTimeHours) {
        return subYearlyLeaveHours + subPartTimeHours;
    }


    //직접인원 -> 인원
    public double directMan(double directPersonnel, double directTotalPersonnel) {
        return directPersonnel - directTotalPersonnel;
    }

    //직접인원 -> 시간
    public double directTime(double directManHours, double directTotalPersonnelTime) {
        return directManHours - directTotalPersonnelTime;
    }

    //간접인원 -> 인원
    public double subMan(double productionPersonnel, double subTotalPersonnel) {
        return productionPersonnel - subTotalPersonnel;
    }

    //간접인원 -> 시간
    public double subTime(double indirectManHours, double subTotalHours) {
        return indirectManHours - subTotalHours;
    }

    //지원 -> 인원
    public double etcMan(double supportPersonnel, double etcPersonnel) {
        return supportPersonnel + etcPersonnel;
    }

    //지원 -> 시간
    public double etcTime(List<WorkerRetention> list) {
        return list.stream()
                .mapToDouble(work->Optional.ofNullable(work.getAddWorkTime()).orElse(0.0)).sum();
    }

    //합계 -> 인원
    public double totalMan(double directMan, double subMan, double etcMan) {
        return directMan + subMan + etcMan;
    }

    //합계 -> 시간
    public double totalTime(double directTime, double subTime, double etcManTime) {
        return directTime + subTime + etcManTime;
    }
}
