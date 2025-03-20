package codehows.com.daehoint.mapper.mes;

import codehows.com.daehoint.dto.sync.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

import java.sql.Date;
import java.util.List;

@Mapper
public interface MESMapper {

	@Select("{CALL dbo.usp_DailyWorkLoss(#{startDate, mode=IN} , #{endDate, mode=IN})}")
	@Options(statementType = StatementType.CALLABLE)
	List<DailyWorkLossResponse> getDailyWorkLosses(@Param("startDate") String startDate, @Param("endDate") String endDate);

	@Select("{CALL dbo.usp_LotResultList(#{startDate, mode=IN} , #{endDate, mode=IN})}")
	@Options(statementType = StatementType.CALLABLE)
	List<LotResultListDTO> getLotResultLists(@Param("startDate") String startDate, @Param("endDate") String endDate);

	@Select("{CALL dbo.usp_MaterialPurchasePlanMonthly}")
	@Options(statementType = StatementType.CALLABLE)
	List<MaterialPurchasePlanMonthlyDTO> getMaterialPurchasePlanMonthlies();

	@Select("{CALL dbo.usp_ProductionDailyList}")
	@Options(statementType = StatementType.CALLABLE)
	List<ProductionDailyListDTO> getProductionDailyLists();

	@Select("{CALL dbo.usp_WorkerRetention}")
	@Options(statementType = StatementType.CALLABLE)
	List<WorkerRetentionDTO> getWorkerRetentions();

	@Select("select * from WM_MATERIALISSUELIST")
	List<MaterialIssueListDTO> getMaterialIssueList();

	@Select("{CALL dbo.usp_EstimatedExpenses}")
	@Options(statementType = StatementType.CALLABLE)
	List<EstimatedExpensesDTO> getEstimatedExpensesDto();

	@Select("select EQUIPMENTUSETIME from DHI_MES.dbo.WM_PRODUCTIONDAILY where LOTID = #{lotId} and PLANDATE = #{date}")
	EquipmentUseTimeDTO getEquipmentUseTimeDto(String lotId, Date date);
}
