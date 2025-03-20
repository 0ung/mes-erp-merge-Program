package codehows.com.daehoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class DailyProcessReportResponse {

	private Long id;

	@JsonProperty("productionData")
	private List<ProductionDataDTO> productionDataDTO;

	@JsonProperty("manInputManageData")
	private List<ManInputManageDataDTO> manInputManageDataDTO;

	@JsonProperty("productionCostData")
	private List<ProductionCostDataDTO> productionCostDataDTO;

	@JsonProperty("techProblems")
	private List<TechProblem> techProblem;

	@JsonProperty("stopRisks")
	private List<StopRisks> stopRisks;

	private String createDate;

	@Data
	@Builder
	public static class ProductionDataDTO {
		private String lotNo;
		private String productName;
		private String modelNo;
		private String specification;
		private String unit;
		private String depart;
		private String itemName;
		private String itemCd;
		private Double plannedQuantity;
		private Double inputQuantity;
		private Double defectiveQuantity;
		private Double defectRate;
		private Double completedQuantity;
		private Double achievementRate;
		private Double workInProgressQuantity;
		private Double materialCost;
		private Double manHours;
		private Double processingCost;
		private Double subtotal;
		private Double pricePerSet;
		private Double totalProduction;
		private Double performanceMaterialCost;
		private Double performanceProcessingCost;
		private Double totalPerformanceAmount;
		private Double monthlyCumulativeProduction;

	}

	@Data
	@Builder
	public static class ManInputManageDataDTO {
		private Double availablePersonnel;
		private Double availableManHours;
		private Double standardManHours;
		private Double nonProductiveManHours;
		private Double workloadManHours;
		private Double stopManHours;
		private Double reworkManHours;
		private Double actualManHours;
		private Double workingManHours;

		private Double workEfficiency;
		private Double actualEfficiency;
		private Double lossRate;
		private Double manHourInputRate;
		private Double manHourOperationRate;
		private Double overallManHourEfficiency;
		private Double overtimePersonnel;
		private Double overtimeManHours;
		private Double additionalInputRate;
		private Double fluxOnTime;
		private Double fluxOperatingTime;
		private Double fluxOperatingRate;
		private Double solderingOnTime;
		private Double solderingOperatingTime;
		private Double solderingOperatingRate;
		private String remarks;

	}

	@Data
	@Builder
	public static class ProductionCostDataDTO {
		private Double totalProductionMaterialCostSum;
		private Double processUsageSubMaterialSum;
		private Double materialTotalSum;
		private Double totalProductionProcessingCostSum;
		private Double processInOutsourcingWorkSum;
		private Double processTotalSum;
		private Double totalProductionActualSum;
		private Double defectiveQuantity;
		private Double defectiveCost;
		private Double stopAndNonproductiveHours;
		private Double stopAndNonproductiveCost;
		private Double reworkHours;
		private Double reworkCost;
		private Double totalCost;
		private Double manufacturingExpenseIndirect;
		private Double manufacturingExpenseGeneralAdmin;
		private Double manufacturingExpenseSellingAndAdmin;
		private Double manufacturingExpenseDepreciationEtc;
		private Double manufacturingExpenseTotal;
		private Double estimateCostTotal;
		private Double processTotalProductionInputAmount;
		private Double processTotalProductionActualProfit;
		private Double processTotalProductionProfitRate;
		private Double processTotalProductionLossRate;
		private Double processTotalProductionMaterialRate;
		private Double processTotalProductionProcessingRate;
	}

	@Data
	@Builder
	public static class TechProblem {
		private String category;          // 구분
		private String description;       // 내용
		private Integer personnel;        // 인원
		private Double manHours;          // 공수
		private Double cost;              // 비용
		private String progressResult;    // 진행결과
		private String processResult;     // 처리결과
		private String responsibleDept1;  // 책임부서1
		private String responsibleDept2;  // 책임부서2
		private String remarks;           // 비고
	}

	@Data
	@Builder
	public static class StopRisks {
		private String category;          // 구분
		private String description;       // 내용
		private Integer personnel;        // 인원
		private Double manHours;          // 공수
		private Double cost;              // 비용
		private String progressResult;    // 진행결과
		private String processResult;     // 처리결과
		private String responsibleDept1;  // 책임부서1
		private String responsibleDept2;  // 책임부서2
		private String remarks;           // 비고
	}
}
