package codehows.com.daehoint.dto.sync;

import lombok.Data;

@Data
public class VLGWHStockWHAmtListDTO {
	//구분
	private String Sortation;
	//전장_대기
	private Double FullLengthStandby;
	//전장_공정진행
	private Double FullLengthProcessInProgress;
	//전장_합계
	private Double FullLengthTotal;
	//기구_대기
	private Double InstrumentStandby;
	//기구_공정진행
	private Double MechanismProcessInProgress;
	//기구_합계
	private Double MechanismTotal;
	//포장_대기
	private Double PackagingStandby;
	//포장_공정진행
	private Double PackagingProcessInProgress;
	//포장_합계
	private Double PackagingTotal;
	//부자재_대기
	private Double SubsidiaryMaterialStandby;
	//부자재_공정진행
	private Double SubMaterialsProcessing;
	//부자재_합계
	private Double SubMaterialsTotal;
	//기타_대기
	private Double OtherStandby;
	//기타_공정진행
	private Double OtherProcessInProgress;
	//기타_합계
	private Double OtherTotal;

}
