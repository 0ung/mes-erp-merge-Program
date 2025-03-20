package codehows.com.daehoint.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PerformanceByProcessResponse {
	private String depart;
	private String process;

	private String modelName;
	private String processStatus;
	private QuantityDataDTO quantityData;
	private List<String> planData;
	private List<String> inputData;
	private List<String> completedData;

	@Getter
	@Builder
	public static class QuantityDataDTO {
		private int planQuantity;        // 계획 수량
		private int inputQuantity;       // 투입 수량
		private int completedQuantity;   // 완료 수량
		private int defectQuantity;      // 불량 수량
	}
}
