package codehows.com.daehoint.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStock extends BaseEntity {

	@Id
	@Column(name = "stock_process_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String category;
	@Setter
	private boolean snapShot;
	private String productName;
	private String modelNo;
	private String specification;
	private Double materialCost;
	private Double processingCost;
	private Double totalCost;
	private Double wipQuantity;
	private Double wipCost;
	private Double qcPendingQuantity;
	private Double qcPendingCost;
	private Double qcPassedQuantity;
	private Double qcPassedCost;
	private Double defectiveQuantity;
	private Double defectiveCost;
	private Double totalQuantity;
	private Double totalCostSummary;
	private String remarks;
}
