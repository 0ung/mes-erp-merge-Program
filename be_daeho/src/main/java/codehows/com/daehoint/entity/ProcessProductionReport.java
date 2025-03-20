package codehows.com.daehoint.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "process_production_report")
public class ProcessProductionReport extends BaseEntity {

    public ProcessProductionReport(String category) {
        this.category = category;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "process_report_id")
    private Long id;

    @Setter
    private String category;

    // 공수 관리 필드
    private Double availablePersonnel;          // 가용인원
    private Double availableManHours;        // 가용공수
    private Double standardManHours;         // 표준공수
    private Double nonProductiveManHours;    // 비생산공수
    private Double workloadManHours;        // 부하공수
    private Double stopManHours;        // 정지공수
    private Double reworkManHours;           // 재작업공수
    private Double actualManHours;    // 실동공수
    private Double workingManHours;          // 작업공수

    // 생산 효율 관리 필드
    private Double workEfficiency;           // 작업 능률
    private Double actualEfficiency;         // 실동 효율
    private Double lossRate;                 // Loss율
    private Double manHourInputRate;         // 공수 투입율
    private Double manHourOperationRate;     // 공수 가동율
    private Double overallManHourEfficiency;          // 공수 종합 효율

    private Double overtimePersonnel;         // 지원 인원
    private Double overtimeManHours;       // 지원 공수
    private Double additionalInputRate;      // 추가 투입율

    // 설비 효율 관리 (Flux 설비 관련 필드)
    private Double fluxOnTime;          // Flux 설비 전원ON 시간
    private Double fluxOperatingTime;    // Flux 설비 실가동 시간
    private Double fluxOperatingRate;    // Flux 설비 실가동율

    // 설비 효율 관리 (Soldering 설비 관련 필드)
    private Double solderingOnTime;     // Soldering 설비 전원ON 시간
    private Double solderingOperatingTime; // Soldering 설비 실가동 시간
    private Double solderingOperatingRate; // Soldering 설비 실가동율

    // 기타 비고 항목
    private String remarks;                  // 비고

    private Double totalProductionMaterialCostSum;  // 총생산재료비합계
    private Double processUsageSubMaterialSum;      // 공정사용부자재합계
    private Double materialTotalSum;                        // 총합계

    private Double totalProductionProcessingCostSum; // 총생산가공비합계
    private Double processInOutsourcingWorkSum;      // 공정내외주작업합계
    private Double processTotalSum;                        // 총합계

    private Double totalProductionActualSum; // 총생산 실적 합계
    private Double defectiveQuantity;        // 불량 수량
    private Double defectiveCost;                         // 불량 비용
    private Double stopAndNonproductiveHours;         // 정지 & 비생산 시간
    private Double stopAndNonproductiveCost;          // 정지 & 비생산 비용
    private Double reworkHours;                           // 재작업 시간
    private Double reworkCost;                            // 재작업 비용
    private Double totalCost;                             // 비용합계
    private Double manufacturingExpenseIndirect;          // 제조경비 - 제조간접비
    private Double manufacturingExpenseGeneralAdmin;      // 제조경비 - 일반관리비
    private Double manufacturingExpenseSellingAndAdmin;   // 제조경비 - 판관비
    private Double manufacturingExpenseDepreciationEtc;   // 제조경비 - 설비감가 및 기타
    private Double manufacturingExpenseTotal;             // 제조경비 - 합계

    private Double estimateCostTotal;     // 공정총생산 - 투입금액
    private Double processTotalProductionInputAmount;     // 공정총생산 - 투입금액
    private Double processTotalProductionActualProfit;    // 공정총생산 - 실적이익
    private Double processTotalProductionProfitRate;      // 공정총생산 - 이익율
    private Double processTotalProductionLossRate;        // 공정총생산 - 손실율
    private Double processTotalProductionMaterialRate;    // 공정총생산 - 재료비율
    private Double processTotalProductionProcessingRate;  // 공정총생산 - 가공비율

    //00시 마무리 데이터
    @Setter
    private boolean snapShot;

    public void updateAvailableManHour(Double availableManHours) {
        this.availableManHours = availableManHours;
    }

    public void updateAvailablePersonnel(Double availablePersonnel) {
        this.availablePersonnel = availablePersonnel;
    }

    public void updateProcessInOutsourcingWorkSum(Double processInOutsourcingWorkSum) {
        this.processInOutsourcingWorkSum = processInOutsourcingWorkSum;
    }

    public void updateOvertimePersonnel(Double overtimePersonnel) {
        this.overtimePersonnel = overtimePersonnel;
    }

    public void updateOvertimeManHours(Double overtimeManHours) {
        this.overtimeManHours = overtimeManHours;
    }

}
