package codehows.com.daehoint.entity.erp;

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
@Table(name = "vlgwh_stock_whamt_list")
public class VLGWHStockWHAmtList extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VLGWHStockWHAmtList_id")
	private Long id;

	//구분
	private String sortation;
	//전장_대기
	private Double fullLengthStandby;
	//전장_공정진행
	private Double fullLengthProcessInProgress;
	//전장_합계
	private Double fullLengthTotal;
	//기구_대기
	private Double instrumentStandby;
	//기구_공정진행
	private Double mechanismProcessInProgress;
	//기구_합계
	private Double mechanismTotal;
	//포장_대기
	private Double packagingStandby;
	//포장_공정진행
	private Double packagingProcessInProgress;
	//포장_합계
	private Double packagingTotal;
	//부자재_대기
	private Double subsidiaryMaterialStandby;
	//부자재_공정진행
	private Double subMaterialsProcessing;
	//부자재_합계
	private Double subMaterialsTotal;
	//기타_대기
	private Double otherStandby;
	//기타_공정진행
	private Double otherProcessInProgress;
	//기타_합계
	private Double otherTotal;

	//00시 마무리 데이터
	private boolean snapShot;

}
