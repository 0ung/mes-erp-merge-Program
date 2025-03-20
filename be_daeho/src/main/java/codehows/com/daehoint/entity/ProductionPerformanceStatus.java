package codehows.com.daehoint.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "production_performance_status")
public class ProductionPerformanceStatus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long id;

    @Setter
    private String category;
    private String lotNo;
    private String productName;
    private String modelNo;
    private String specification;
    //아이템 이름
    private String depart;
    private String itemCd;
    private String itemName;
    private String unit;
    private Double plannedQuantity;
    private Double inputQuantity;
    private Double defectiveQuantity;
    private Double defectRate;
    private Double completedQuantity;
    private Double achievementRate;
    private Double workInProgressQuantity;
    private Double materialCost;
    private Double manHours;
    private Double processingCost;
    private Double subtotal;
    private Double pricePerSet;
    private Double totalProduction;
    private Double performanceMaterialCost;
    private Double performanceProcessingCost;
    private Double totalPerformanceAmount;
    private Double monthlyCumulativeProduction;

    @Setter
    private Boolean snapShot;

}
