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
@Table(name = "material_purchase_plan_monthly")
public class MaterialPurchasePlanMonthly extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MaterialPurchasePlanMonthly_id")
	private Long id;
	// 계획년월
	private String planMonth;

	// 거래처
	private String customerName;

	// 계획금액
	private String salesPlanAmt;

	// 자재구매비율
	private Double materialPurchasingPlanRate;

	// 자재구매계획금액
	private Double materialPurchasingPlanAmt;

	// -
	private String remark;

	// -
	private Integer companyId;

	// -
	private Integer divisionId;

	// -
	private Boolean isValid;

	// -
	private String createUserId;

	// -
	private String createTime;

	// -
	private String modifyUserId;

	// -
	private String modifyTime;

	//00시 마무리 데이터
	private boolean snapShot;

}
