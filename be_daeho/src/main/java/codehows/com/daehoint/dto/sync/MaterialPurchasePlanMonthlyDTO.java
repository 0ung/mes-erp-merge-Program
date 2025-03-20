package codehows.com.daehoint.dto.sync;

import lombok.Data;

@Data
public class MaterialPurchasePlanMonthlyDTO {
	// 계획년월
	private String PlanMonth;

	// 거래처
	private String CustomerName;

	// 계획금액
	private String SalesPlanAmt;

	// 자재구매비율
	private Double MaterialPurchasingPlanRate;

	// 자재구매계획금액
	private Double MaterialPurchasingPlanAmt;

	// -
	private String Remark;

	// -
	private Integer CompanyId;

	// -
	private Integer DivisionId;

	// -
	private Boolean IsValid;

	// -
	private String CreateUserId;

	// -
	private String  CreateTime;

	// -
	private String ModifyUserId;

	// -
	private String ModifyTime;

}
