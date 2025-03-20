package codehows.com.daehoint.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "main_production_report")
public class MainProductionReport extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 1. 근태현황
	private int productionPersonnel;           // 생산 인원 (간접 인원)
	private int directPersonnel;               // 직접 인원
	private int supportPersonnel;              // 지원 인원
	private int etcPersonnel;                  // 기타 인원
	private int totalPersonnel;                // 합계 인원
	private Double indirectManHours;           // 간접 공수
	private Double directManHours;             // 직접 공수
	private Double totalManHours;              // 합계 공수

	private Double directYearlyLeavePersonnel;       // 연차 인원
	private Double directYearlyLeaveHours;           // 연차 시간
	private Double directPartTimePersonnel;          // 시간차 인원
	private Double directPartTimeHours;              // 시간차 시간
	private Double directEtcPersonnel;          // 기타 인원
	private Double directEtcPersonnelTime;              // 기타 시간
	private Double directTotalPersonnel;          // 합계 인원
	private Double directTotalPersonnelTime;
	// 합계 시간
	private Double subYearlyLeavePersonnel;       // 연차 인원
	private Double subYearlyLeaveHours;           // 연차 시간
	private Double subPartTimePersonnel;          // 시간차 인원
	private Double subPartTimeHours;				// 시간차 시간
	private Double subTotalPersonnel;
	private Double subTotalHours;
	private Double directMan;       // 연차 인원
	private Double directTime;           // 연차 시간
	private Double subMan;          // 시간차 인원
	private Double subTime;              // 시간차 시간
	private Double etcMan;          // 기타 인원
	private Double etcTime;              // 기타 시간
	private Double totalMan;          // 합계 인원
	private Double totalTime;              // 합계 시간
	private String attendanceRemark;

	// 2. 공수 투입 관리 필드
	private Double availablePersonnel;         // 가용 인원
	private Double availableManHours;          // 가용 공수
	private Double standardManHours;           // 표준 공수
	private Double nonProductiveManHours;      // 비생산 공수
	private Double loadManHours;               // 부하 공수
	private Double stoppedManHours;            // 정지 공수
	private Double reworkManHours;             // 재작업 공수
	private Double actualManHours;             // 실동 공수
	private Double workingManHours;            // 작업 공수

	private Double workEfficiency;             // 작업 능률
	private Double actualEfficiency;           // 실동 효율
	private Double lossRate;                   // Loss율
	private Double manHourInputRate;           // 공수 투입율
	private Double manHourOperationRate;       // 공수 가동율

	private Double totalEfficiency;            // 공수 종합 효율
	private int specialSupportPersonnel;       // 잔업(특근) 지원 인원
	private Double specialSupportManHours;     // 잔업(특근) 지원 공수
	private Double additionalInputRate;        // 추가 투입율
	private Double fluxEquipmentRunningTime;   // Flux 설비 실가동 시간
	private Double fluxEquipmentRunningRate;   // Flux 설비 실가동율
	private Double solderingEquipmentRunningTime; // Soldering 설비 실가동 시간
	private Double solderingEquipmentRunningRate; // Soldering 설비 실가동율
	private String manPowerRemark;

	// 3. 생산비용분석
	private Double rawMaterialCost;            // 원재료비
	private Double subsidiaryMaterialCost;     // 부자재
	private Double totalMaterialCost;          // 합계 재료비
	private Double productionCost;             // 생산비용
	private Double smImCost;                   // SM / IM 비용

	private Double externalProcessingCost;     // 공정내 외주 비용
	private Double totalProductionCost;			// 총 생산실적금액
	private Double totalProductionAmount;        // 총 생산실적금액
	private Double lossHandlingCnt;           // 불량 수량
	private Double lossHandlingCost;           // 불량 비용
	private Double nonProductiveTimeHour;      // 정지 & 비생산 시간
	private Double nonProductiveTimeCost;      // 정지 & 비생산 비용
	private Double reworkTimeCnt;             // 재작업 수량
	private Double reworkTimeCost;             // 재작업 비용
	private Double totalLossCost;              // 손실 비용 합계
	private Double totalProductionDirectInputCost; // 총 생산 직접투입 비용

	//제조원가분석
	private Double directPersonnelCost;        // 직접비
	private Double indirectPersonnelCost;      // 간접비
	private Double generalManagementCost;      // 일반 관리비
	private Double salesCost;                  // 판매 관리비
	private Double equipmentDepreciationCost;  // 설비 감가
	private Double otherCost;                  // 기타
	private Double totalManufacturingCost;     // 합계
	private Double totalProductCost;           // 총생산 비용
	private Double totalEstimateCost;           // 총생산 견적비용
	private Double totalProfit;                // 총생산 이익
	private Double netProfit;                  // 총생산 이익률
	private Double investCost;            //투자비용
	private Double totalExpenditure;           // 총 지출 비용
	private String costRemark;

	@Setter
	//00시 마무리 데이터
	private boolean snapShot;
}
