package codehows.com.daehoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class FlowDataResponse {
	// FlowDataDTO의 필드
	public String lotProgress;
	public String modelName;
	public String specification;
	public String productionRequestNo;
	public String partNumber;
	public Map<String, ProcessDataDTO> processData; // 각 공정별 데이터
	public ProgressTypeDTO progressTypeDTO; // 공정 진행률 데이터

	@Getter
	@Builder
	public static class ProgressTypeDTO {
		public List<Double> processProgress;
	}

	// 각 어셈블리 별 데이터를 관리하는 ProcessDataDTO 클래스
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ProcessDataDTO {
		public double planQty;
		public double inputQty;
		public double completedQty;
		public double inputCost;
		public double completedRate;
	}

}
