package codehows.com.daehoint.mapper;

import codehows.com.daehoint.dto.sync.*;
import codehows.com.daehoint.entity.erp.*;
import codehows.com.daehoint.entity.mes.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DataSyncMapper {
	DataSyncMapper INSTANCE = Mappers.getMapper(DataSyncMapper.class);

	//MSSQL -> MYSQL 매핑
	DailyWorkLoss toEntity(DailyWorkLossResponse dailyWorkLossResponse);

	LotResultList toEntity(LotResultListDTO lotResultListDTO);

	MaterialPurchasePlanMonthly toEntity(MaterialPurchasePlanMonthlyDTO materialPurchasePlanMonthlyDTO);

	ProductionDailyList toEntity(ProductionDailyListDTO productionDailyListDTO);

	VLGWHStockAmtList toEntity(VLGWHStockAmtListDTO vlgwhStockAmtListDTO);

	VLGWHStockWHAmtList toEntity(VLGWHStockWHAmtListDTO vlgwhStockWHAmtListDTO);

	VPDPartsList toEntity(VPDPartsListDTO vpdPartsListDTO);

	VPDSPFWorkReportQC toEntity(VPDSPFWorkReportQCDTO vpdspfWorkReportQCDTO);

	VPUORDAmtList toEntity(VPUORDAmtListDayDTO vpuordAmtListDayDTO);

	WorkerRetention toEntity(WorkerRetentionDTO workerRetentionDTO);

	MaterialIssueList toEntity(MaterialIssueListDTO materialIssueListDTO);

	EstimatedExpenses toEntity(EstimatedExpensesDTO estimatedExpensesDTO);
}
