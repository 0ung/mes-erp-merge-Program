package codehows.com.daehoint.dto.sync;

import lombok.Data;

@Data
public class VLGWHStockAmtListDTO {

	//구분
	private String sortation;
	//직구매자재
	private Double directPurchasingMaterials;
	//사급자재
	private Double privateSupplyMaterial;
	//합계
	private Double total;
}
