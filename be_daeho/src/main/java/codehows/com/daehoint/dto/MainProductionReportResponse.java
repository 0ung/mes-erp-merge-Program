package codehows.com.daehoint.dto;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.entity.BaseEntity;
import codehows.com.daehoint.entity.MainProductionReport;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainProductionReportResponse extends BaseEntity {

    @JsonProperty("attendanceStatus")
    private List<AttendanceStatusDataDTO> attendanceStatusDataDTO;
    @JsonProperty("manPowerInputManage")
    private List<ManPowerInputManageDataDTO> manPowerInputManageDataDTO;
    @JsonProperty("costAnalyze")
    private List<CostAnalyzeDataDTO> costAnalyzeDataDTO;
    @JsonProperty("manufacturingCostAnalysis")
    private List<ManufacturingCostAnalysisDataDTO> manufacturingCostAnalysisDataDTO;

    private String createDate;
    private Long id;

    @Data
    @Builder
    public static class AttendanceStatusDataDTO {
        private Double productionPersonnel;
        private Double directPersonnel;
        private Double supportPersonnel;
        private Double etcPersonnel;
        private Double totalPersonnel;
        private Double indirectManHours;
        private Double directManHours;
        private Double totalManHours;

        private Double directYearlyLeavePersonnel;
        private Double directYearlyLeaveHours;
        private Double directPartTimePersonnel;
        private Double directPartTimeHours;
        private Double directEtcPersonnel;
        private Double directEtcPersonnelTime;
        private Double directTotalPersonnel;
        private Double directTotalPersonnelTime;

        private Double subYearlyLeavePersonnel;
        private Double subYearlyLeaveHours;
        private Double subPartTimePersonnel;
        private Double subPartTimeHours;
        private Double subTotalPersonnel;
        private Double subTotalHours;
        private Double directMan;
        private Double directTime;
        private Double subMan;
        private Double subTime;
        private Double etcMan;
        private Double etcTime;
        private Double totalMan;
        private Double totalTime;
        private String attendanceRemark;
    }

    @Data
    @Builder
    public static class ManPowerInputManageDataDTO {
        private Double availablePersonnel;
        private Double availableManHours;
        private Double standardManHours;
        private Double nonProductiveManHours;
        private Double loadManHours;
        private Double stoppedManHours;
        private Double reworkManHours;
        private Double actualManHours;
        private Double workingManHours;

        private Double workEfficiency;
        private Double actualEfficiency;
        private Double lossRate;
        private Double manHourInputRate;
        private Double manHourOperationRate;

        private Double totalEfficiency;
        private Double specialSupportPersonnel;
        private Double specialSupportManHours;
        private Double additionalInputRate;
        private Double fluxEquipmentRunningTime;
        private Double fluxEquipmentRunningRate;
        private Double solderingEquipmentRunningTime;
        private Double solderingEquipmentRunningRate;
        private String manPowerRemark;
    }

    @Data
    @Builder
    public static class CostAnalyzeDataDTO {
        private Double rawMaterialCost;
        private Double subsidiaryMaterialCost;
        private Double totalMaterialCost;
        private Double productionCost;
        private Double smImCost;

        private Double externalProcessingCost;
        private Double totalProductionCost;
        private Double totalProductionAmount;
        private Double lossHandlingCnt;
        private Double lossHandlingCost;
        private Double nonProductiveTimeHour;
        private Double nonProductiveTimeCost;
        private Double reworkTimeCnt;
        private Double reworkTimeCost;
        private Double totalLossCost;
        private Double totalProductionDirectInputCost;
    }

    @Data
    @Builder
    public static class ManufacturingCostAnalysisDataDTO {
        private Double directPersonnelCost;
        private Double indirectPersonnelCost;
        private Double generalManagementCost;
        private Double salesCost;
        private Double equipmentDepreciationCost;
        private Double otherCost;
        private Double totalManufacturingCost;
        private Double totalProductCost;
        private Double totalEstimateCost;
        private Double totalProfit;
        private Double netProfit;
        private Double investCost;
        private Double totalExpenditure;
        private String costRemark;
    }

    public static MainProductionReportResponse convertToDTO(MainProductionReport mainProductionReport) {
        List<MainProductionReportResponse.AttendanceStatusDataDTO> statusDataDTOS = new ArrayList<>();
        List<MainProductionReportResponse.ManPowerInputManageDataDTO> manPowerInputManageDataDTOS = new ArrayList<>();
        List<MainProductionReportResponse.CostAnalyzeDataDTO> costAnalyzeDataDTOS = new ArrayList<>();
        List<MainProductionReportResponse.ManufacturingCostAnalysisDataDTO> manufacturingCostAnalysisDataDTOS = new ArrayList<>();

        // Mapper를 통해 데이터를 변환하고 리스트에 추가
        statusDataDTOS.add(Util.mapper.toAttendanceStatusDataDto(mainProductionReport));
        manPowerInputManageDataDTOS.add(Util.mapper.toManPowerInputManageDataDto(mainProductionReport));
        costAnalyzeDataDTOS.add(Util.mapper.toCostAnalyzeDataDto(mainProductionReport));
        manufacturingCostAnalysisDataDTOS.add(Util.mapper.toManufacturingCostAnalysisDataDto(mainProductionReport));

        // ProcessDailyReport 빌더 패턴으로 객체 생성 후 반환
        return MainProductionReportResponse.builder()
                .attendanceStatusDataDTO(statusDataDTOS)
                .manPowerInputManageDataDTO(manPowerInputManageDataDTOS)
                .costAnalyzeDataDTO(costAnalyzeDataDTOS)
                .manufacturingCostAnalysisDataDTO(manufacturingCostAnalysisDataDTOS)
                .createDate(mainProductionReport.getCreateDateTime().format(Util.formatter))
                .id(mainProductionReport.getId())
                .build();
    }
}
