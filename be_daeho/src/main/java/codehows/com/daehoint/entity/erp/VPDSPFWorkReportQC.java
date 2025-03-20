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
@Table(name = "vpdspf_work_report_qc")
public class VPDSPFWorkReportQC extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VPDSPFWorkReportQC_id")
	private Long id;
	//생산사업장
	private String productionPlaceOfBusiness;
	// 작업일
	private String workingDay;
	// 생산부서
	private String productionDepartment;
	//생산공정
	private String productionProcess;
	//작업지시번호
	private String operationInstructionNumber;
	//완성품명
	private String finishedProductName;
	//완성품번
	private String finishedProductNumber;
	//완성품규격
	private String finishedProductSpecification;
	//BOM차수
	private String bomAberration;
	//생산수량
	private Double productionQuantity;
	//양품수량
	private Double goodQuantity;
	//불량수량
	private Double defectiveQuantity;
	//검사번호
	private String inspectionNumber;
	//검사수량
	private Double inspectionQuantity;
	//합격수량
	private Double passQuantityInspection;
	//불량수량
	private Double defectiveQuantityInspection;
	//검사비고
	private String inspectionExpense;
	//생산실적여부
	private String ProductionPerformanceStatus;
	//모델명
	private String modelName;
	//고객사코드
	private String customCode;
	//00시 마무리 데이터
	private boolean snapShot;
}
