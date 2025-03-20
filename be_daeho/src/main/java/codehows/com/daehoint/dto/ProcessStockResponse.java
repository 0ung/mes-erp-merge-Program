package codehows.com.daehoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProcessStockResponse {
	private String category;
	private String productName;
	private String modelNo;
	private String specification;
	private Double materialCost;
	private Double processingCost;
	private Double totalCost;
	private Double wipQuantity;
	private Double wipCost;
	private Double qcPendingQuantity;
	private Double qcPendingCost;
	private Double qcPassedQuantity;
	private Double qcPassedCost;
	private Double defectiveQuantity;
	private Double defectiveCost;
	private Double totalQuantity;
	private Double totalCostSummary;
	private String remarks;
}
