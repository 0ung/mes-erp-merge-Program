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
@Table(name = "vpd_parts_list")
public class VPDPartsList extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VPDPartsList_id")
	private Long id;

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

	private boolean recent;


	public void updateRecent(boolean recent){
		this.recent = recent;
	}
}
