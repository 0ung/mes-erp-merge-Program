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
@Table(name = "vlgwh_stock_amt_list")
public class VLGWHStockAmtList extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VLGWHStockAmtList_id")
	private Long id;

	//구분
	private String sortation;
	//직구매자재
	private Double directPurchasingMaterials;
	//사급자재
	private Double privateSupplyMaterial;
	//합계
	private Double total;

	//00시 마무리 데이터
	private boolean snapShot;
}
