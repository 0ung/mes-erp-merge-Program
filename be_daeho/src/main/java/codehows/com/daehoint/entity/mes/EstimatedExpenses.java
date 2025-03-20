package codehows.com.daehoint.entity.mes;

import codehows.com.daehoint.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class EstimatedExpenses extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estimated_id")
    private Long id;

    @Setter
    private boolean snapshot;
    private String expectDate;  // 예상일자
    private Double overheadCost;  // 간접비용
    private Double generalManagementExpense;  // 일반관리비
    private Double salesManagementExpense;  // 판매관리비
    private Double depreciationCost;  // 감가상각비
    private Double etc;  // 기타
    private String operationType;  // 운영 유형
    private Boolean isValid;  // 유효 여부
}
