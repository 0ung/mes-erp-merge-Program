package codehows.com.daehoint.dto.sync;

import lombok.Data;

@Data
public class LotResultListDTO {
	// 생산시작일
	private String resultDate;

	// 작업계획일
	private String ProductionReqEndDate;

	// 대호코드
	private String ItemCd;

	// 작업지시번호
	private String LotId;

	// 생산모듈
	private String WorkshopName;

	// 생산부서
	private String Department;

	// 품명(공정명)
	private String ItemName;

	// 품목 대분류
	private String CategoryItemValue01;

	// 품목 중분류
	private String CategoryItemValue02;

	// 품목 소분류
	private String CategoryItemValue03;

	// 규격
	private String ItemSpec;

	// 작업지시 수량
	private Double LotQty;

	// 투입수량
	private Double InQty;

	// 생산완료수량
	private Double OutQty;

	// 불량수량
	private Double DefectQty;

	// 불량수리완료
	private Double RepairQty;

	// 실적완료수량
	private Double Qty;

	// 표준공수
	private Double StandardTime;

	// 진행상태
	private String LotState;

	// 보류유무
	private Boolean IsHold;

	// 생산소요시간
	private Double OperationTime;

	// 작업자
	private String WorkerList;

	// 표준인원
	private Double AverageWorker;

	// 투입인원
	private Double WorkInCnt;

	// 투입시간
	private Double InputTime;

	// 설비사용시간
	private String EquipmentUseTime;
	//납품번호
	private String productionRequestNo;
}
