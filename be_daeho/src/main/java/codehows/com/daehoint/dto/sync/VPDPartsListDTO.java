package codehows.com.daehoint.dto.sync;

import lombok.Data;

@Data
public class VPDPartsListDTO {
	//대호코드
	private String daehoCode;
	//품명
	private String productName;
	//원자재비
	private Double costRawMaterials;
	//부자재비
	private Double subExpenses;
	//자재비합계
	private Double totalMaterialRatio;
	// 가공비
	private Double processingCost;
	//견적단가
	private Double estimatedUnitPrice;

}
