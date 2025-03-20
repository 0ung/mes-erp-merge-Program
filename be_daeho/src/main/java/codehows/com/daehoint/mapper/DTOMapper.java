package codehows.com.daehoint.mapper;

import codehows.com.daehoint.dto.DailyProcessReportResponse;
import codehows.com.daehoint.dto.MainProductionReportResponse;
import codehows.com.daehoint.dto.ProcessStockResponse;
import codehows.com.daehoint.dto.PurchaseAndReceiptResponse;
import codehows.com.daehoint.dto.sync.DailyWorkLossResponse;
import codehows.com.daehoint.dto.sync.MaterialIssueListDTO;
import codehows.com.daehoint.entity.MainProductionReport;
import codehows.com.daehoint.entity.ProcessProductionReport;
import codehows.com.daehoint.entity.ProcessStock;
import codehows.com.daehoint.entity.ProductionPerformanceStatus;
import codehows.com.daehoint.entity.erp.VPUORDAmtList;
import codehows.com.daehoint.entity.mes.DailyWorkLoss;
import codehows.com.daehoint.entity.mes.MaterialIssueList;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOMapper {
	DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

	//
	DailyProcessReportResponse.ManInputManageDataDTO toManDTO(ProcessProductionReport report);

	DailyProcessReportResponse.ProductionDataDTO toDTO(ProductionPerformanceStatus status);

	DailyProcessReportResponse.ProductionCostDataDTO toCostDTO(ProcessProductionReport report);

	DailyProcessReportResponse.TechProblem toTechProblem(DailyWorkLoss dailyWorkLoss);

	DailyProcessReportResponse.StopRisks toStopRisks(DailyWorkLoss dailyWorkLoss);

	MainProductionReportResponse.AttendanceStatusDataDTO toAttendanceStatusDataDto(MainProductionReport mainProductionReport);

	MainProductionReportResponse.ManPowerInputManageDataDTO toManPowerInputManageDataDto(
		MainProductionReport mainProductionReport);

	MainProductionReportResponse.CostAnalyzeDataDTO toCostAnalyzeDataDto(MainProductionReport mainProductionReport);

	MainProductionReportResponse.ManufacturingCostAnalysisDataDTO toManufacturingCostAnalysisDataDto(
		MainProductionReport mainProductionReport);

	DailyWorkLossResponse toDailyWorkLossDto(DailyWorkLoss dailyWorkLoss);

	ProcessStockResponse toProcessStockResponse(ProcessStock processStock);

	MaterialIssueListDTO toMaterialIssueListDto(MaterialIssueList issueList);

	PurchaseAndReceiptResponse.DailyMaterialCostDTO toDailyMaterialCostDto(VPUORDAmtList vpuordAmtList);
}
