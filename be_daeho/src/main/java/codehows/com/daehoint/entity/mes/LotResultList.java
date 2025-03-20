package codehows.com.daehoint.entity.mes;

import codehows.com.daehoint.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "lot_result_list")
public class LotResultList extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "LotResultList_id")
	private Long id;
	// 생산시작일
	private String resultDate;

	// 작업계획일
	private String productionReqEndDate;

	// 대호코드
	private String itemCd;

	// 작업지시번호
	private String lotId;

	// 생산모듈
	private String workshopName;

	// 생산부서
	private String department;

	// 품명(공정명)
	private String itemName;

	// 품목 대분류
	private String categoryItemValue01;

	// 품목 중분류
	private String categoryItemValue02;

	// 품목 소분류
	private String categoryItemValue03;

	// 규격
	private String itemSpec;

	// 작업지시 수량
	private Double lotQty;

	// 투입수량
	private Double inQty;

	// 생산완료수량
	private Double outQty;

	// 불량수량
	private Double defectQty;

	// 불량수리완료
	private Double repairQty;

	// 실적완료수량
	private Double qty;

	// 표준공수
	private Double standardTime;

	// 진행상태
	private String lotState;

	// 보류유무
	private Boolean isHold;

	// 생산소요시간
	private Double operationTime;

	// 작업자
	private String workerList;

	// 표준인원
	private Double averageWorker;

	// 투입인원
	private Double workInCnt;

	// 투입시간
	private Double inputTime;

	// 설비사용시간
	private String equipmentUseTime;

	//납품번호
	private String productionRequestNo;

	//00시 마무리 데이터
	private boolean snapShot;

	public void updateLotResultList(String lotState) {
		this.lotState = lotState;
	}
}
