import { useEffect, useState } from "react";
import ProcessProductionDailyReport, {
  ProcessReportProps,
} from "../../pages/ProcessProductionDailyReport";
import apiClient from "../../apiClient";
import { PROCESS_API } from "../../constants/API";
import { calculateProductionDataTotals } from "../tools/utils";
import { useLocation } from "react-router-dom";

function PCBAssy() {
  const [processData, setProcessData] = useState<ProcessReportProps>({
    processName: "PCB ASSY",
    manInputManageData: [],
    productionCostData: [],
    productionData: [],
    stopRisks: [],
    techProblems: [],
  });
  const location = useLocation();
  const { details } = location.state || {};

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
  }, []);

  const handleProcessData = async () => {
    try {
      const response = await apiClient.get(`${PROCESS_API}/PCB ASSY`);
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

  return (
    <ProcessProductionDailyReport
      processName={"PCB ASSY"}
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

export default PCBAssy;
