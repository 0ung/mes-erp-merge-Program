import { useEffect, useState } from "react";
import ProcessProductionDailyReport, {
  ProcessReportProps,
} from "../../pages/ProcessProductionDailyReport";
import apiClient from "../../apiClient";
import { PROCESS_API } from "../../constants/API";
import { calculateProductionDataTotals } from "../tools/utils";
import { useLocation } from "react-router-dom";
function ACCY() {
  const [processData, setProcessData] = useState<ProcessReportProps>({
    processName: "ACCY",
    manInputManageData: [],
    productionCostData: [],
    productionData: [],
    stopRisks: [],
    techProblems: [],
  });
  const location = useLocation(); // useLocation으로 state 받기
  const { details } = location.state || {}; // state에서 details 추출

  const handleProcessData = async () => {
    try {
      const response = await apiClient.get(`${PROCESS_API}/ACCY`);
      const data = response.data;
      const newData = calculateProductionDataTotals(data.productionData);
      setProcessData({
        ...data,
        productionData: [...data.productionData, newData],
      });
    } catch (error) {
      console.error(
        "Error occurred while fetching SM ASSY process data:",
        error
      );
    }
  };
  useEffect(() => {
    if (!details) {
      handleProcessData();
    } else {
      console.log(details);
      const newData = calculateProductionDataTotals(details.productionData);
      setProcessData({
        ...details,
        productionData: [...details.productionData, newData],
      });
    }
  }, []); // details에 의존성을 설정

  return (
    <ProcessProductionDailyReport
      processName={"ACCY"}
      manInputManageData={processData.manInputManageData}
      productionCostData={processData.productionCostData}
      productionData={processData.productionData}
      stopRisks={processData.stopRisks}
      techProblems={processData.techProblems}
      search={details ? true : false}
      searchData={details ? details.createDate : ""}
    ></ProcessProductionDailyReport>
  );
}

export default ACCY;
