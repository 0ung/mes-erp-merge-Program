package codehows.com.daehoint.mapper.erp;

import codehows.com.daehoint.dto.sync.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ERPMapper {
	@Select("SELECT * FROM dbo.dhi_VLGWHStockAmtList")
	@Results({
		@Result(property = "sortation", column = "구분"),
		@Result(property = "directPurchasingMaterials", column = "직구매자재"),
		@Result(property = "privateSupplyMaterial", column = "사급자재"),
		@Result(property = "total", column = "합계"),
	})
	List<VLGWHStockAmtListDTO> getVlgwhStockAmtList();

	@Select("select * from dbo.dhi_VLGWHStockWHAmtList;")
	@Results({
		@Result(property = "Sortation", column = "구분"), // 구분
		@Result(property = "FullLengthStandby", column = "전장_대기"), // 전장_대기
		@Result(property = "FullLengthProcessInProgress", column = "전장_공정진행"), // 전장_공정진행
		@Result(property = "FullLengthTotal", column = "전장_합계"), // 전장_합계
		@Result(property = "InstrumentStandby", column = "기구_대기"), // 기구_대기
		@Result(property = "MechanismProcessInProgress", column = "기구_공정진행"), // 기구_공정진행
		@Result(property = "MechanismTotal", column = "기구_합계"), // 기구_합계
		@Result(property = "PackagingStandby", column = "포장_대기"), // 포장_대기
		@Result(property = "PackagingProcessInProgress", column = "포장_공정진행"), // 포장_공정진행
		@Result(property = "PackagingTotal", column = "포장_합계"), // 포장_합계
		@Result(property = "SubsidiaryMaterialStandby", column = "부자재_대기"), // 부자재_대기
		@Result(property = "SubMaterialsProcessing", column = "부자재_공정진행"), // 부자재_공정진행
		@Result(property = "SubMaterialsTotal", column = "부자재_합계"), // 부자재_합계
		@Result(property = "OtherStandby", column = "기타_대기"), // 기타_대기
		@Result(property = "OtherProcessInProgress", column = "기타_공정진행"), // 기타_공정진행
		@Result(property = "OtherTotal", column = "기타_합계") // 기타_합계
	})
	List<VLGWHStockWHAmtListDTO> getVlgwhStockWhAmtList();

	@Select("select * from dbo.dhi_VPDPartsList")
	@Results({
		@Result(property = "daehoCode", column = "대호코드"), // 대호코드
		@Result(property = "productName", column = "품명(공정명)"), // 품명
		@Result(property = "costRawMaterials", column = "원자재비"), // 원자재비
		@Result(property = "subExpenses", column = "부자재비"), // 부자재비
		@Result(property = "totalMaterialRatio", column = "자재비합계"), // 자재비합계
		@Result(property = "processingCost", column = "가공비"), // 가공비
		@Result(property = "estimatedUnitPrice", column = "견적 단가(판매단가)") // 견적단가
	})
	List<VPDPartsListDTO> getVpdPartsLists();

	@Select("select * from dbo.dhi_VPDSPFWorkReportQC where 작업일 = #{workingDay}")
	@Results({
		@Result(property = "productionPlaceOfBusiness", column = "생산사업장"), // 생산사업장
		@Result(property = "workingDay", column = "작업일"), // 작업일
		@Result(property = "productionDepartment", column = "생산부서"), // 생산부서
		@Result(property = "productionProcess", column = "생산공정"), // 생산공정
		@Result(property = "operationInstructionNumber", column = "작업지시번호"), // 작업지시번호
		@Result(property = "finishedProductName", column = "완성품명"), // 완성품명
		@Result(property = "finishedProductNumber", column = "완성품번"), // 완성품번
		@Result(property = "finishedProductSpecification", column = "완성품규격"), // 완성품규격
		@Result(property = "bomAberration", column = "BOM차수"), // BOM차수
		@Result(property = "productionQuantity", column = "생산수량"), // 생산수량
		@Result(property = "goodQuantity", column = "양품수량"), // 양품수량
		@Result(property = "defectiveQuantity", column = "불량수량"), // 불량수량
		@Result(property = "inspectionNumber", column = "검사번호"), // 검사번호
		@Result(property = "inspectionQuantity", column = "검사수량"), // 검사수량
		@Result(property = "passQuantityInspection", column = "(검사)합격수량"), // 합격수량
		@Result(property = "defectiveQuantityInspection", column = "(검사)불량수량"), // 불량수량
		@Result(property = "inspectionExpense", column = "검사비고"), // 검사비고
		@Result(property = "productionPerformanceStatus", column = "생산실적여부"), // 생산실적여부
		@Result(property = "modelName", column = "모델명"), // 생산실적여부
		@Result(property = "customCode", column = "고객사코드") // 생산실적여부
	})
	List<VPDSPFWorkReportQCDTO> getVpdspfWorkReportQcs(@Param("workingDay") String workingDay);
	@Select("select * from dbo.dhi_VPUORDAmtList")
	@Results({
		@Result(property = "sortation", column = "구분"), // 구분
		@Result(property = "orderDirectTransactionDailyAmount", column = "발주-직거래-일간금액"), // 발주-직거래-일간금액
		@Result(property = "orderDirectTransactionMonthlyAmount", column = "발주-직거래-월간금액"), // 발주-직거래-월간금액
		@Result(property = "orderSaidDailyAmount", column = "발주-사급-일간금액"), // 발주-사급-일간금액
		@Result(property = "orderSaidMonthlyAmount", column = "발주-사급-월간금액"), // 발주-사급-월간금액
		@Result(property = "receivingDirectTransactionDailyAmount", column = "입고-직거래-일간금액"), // 입고-직거래-일간금액
		@Result(property = "receivingDirectTransactionMonthlyAmount", column = "입고-직거래-월간금액"), // 입고-직거래-월간금액
		@Result(property = "receiptSaidDailyAmount", column = "입고-사급-일간금액"), // 입고-사급-일간금액
		@Result(property = "receivingPaymentMonthlyAmount", column = "입고-직거래-월간금액"), // 입고-직거래-월간금액
		@Result(property = "deliveryDirectTransactionDailyAmount", column = "납품-직거래-일간금액"), // 납품-직거래-일간금액
		@Result(property = "deliveryDirectTransactionMonthlyAmount", column = "납품-직거래-월간금액"), // 납품-직거래-월간금액
		@Result(property = "deliverySaidDailyAmount", column = "납품-사급-일간금액"), // 납품-사급-일간금액
		@Result(property = "deliverySalaryMonthlyAmount", column = "납품-사급-월간금액") // 납품-사급-월간금액
	})
	List<VPUORDAmtListDayDTO> getVpuordAmtListDays();
}
