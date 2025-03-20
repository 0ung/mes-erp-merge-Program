import { useCallback, useEffect, useRef, useState } from "react";
import apiClient from "../apiClient";
import { Button, Col, Row } from "react-bootstrap";
import searchIcon from "../asset/search.svg";
import TableWrapper from "../components/tools/TableWrapper";
import { AgGridReact } from "ag-grid-react";
import {
  DailyMaterialCost,
  ModelPurchasePlan,
  ModelReceiptStatus,
  StockStatus,
  WarehouseMaterialStatus,
} from "../components/tools/tableDef";
import { DOWNLOAD_PURCHASE, PURCHASE_API } from "../constants/API";
import { DateField } from "../components/tools/Search";
import { useLocation, useNavigate } from "react-router-dom";
import styled from "styled-components";
import PrintPage from "../components/tools/PrintPage";
import { caluMaterialCostData, caluModelPurchasePlanData, caluModelReceiptStatusData, caluStockStatusData, caluWarehouseMaterialStatusData, getDateTime } from "../components/tools/utils";
import { useNameContext } from "../context/nameProvider";
import { HideColumns } from "../components/tools/HideColmuns";

export interface MaterialCost {
  monthlySalesPlan: number;
  monthlyPurchasePlan: number;
  dailyDirectTransactionAmount: number;
  weeklyDirectTransactionAmount: number;
  monthlyDirectTransactionAmount: number;
  dailySubcontractAmount: number;
  weeklySubcontractAmount: number;
  monthlySubcontractAmount: number;
  dailyTotalAmount: number;
  weeklyTotalAmount: number;
  monthlyTotalAmount: number;
  dailyDirectReceiptAmount: number;
  weeklyDirectReceiptAmount: number;
  monthlyDirectReceiptAmount: number;
  dailySubcontractReceiptAmount: number;
  weeklySubcontractReceiptAmount: number;
  monthlySubcontractReceiptAmount: number;
  dailyTotalReceiptAmount: number;
  weeklyTotalReceiptAmount: number;
  monthlyTotalReceiptAmount: number;
  dailyPendingDirectAmount: number;
  weeklyPendingDirectAmount: number;
  monthlyPendingDirectAmount: number;
  dailyPendingSubcontractAmount: number;
  weeklyPendingSubcontractAmount: number;
  monthlyPendingSubcontractAmount: number;
  dailyPendingTotalAmount: number;
  weeklyPendingTotalAmount: number;
  monthlyPendingTotalAmount: number;
}

export interface StockStatus {
  directPurchaseMaterial: number;
  subcontractMaterial: number;
  totalMaterial: number;
}

export interface ModelPurchasePlan {
  salesPlanMonthly: number;
  materialCostRatio: number;
  purchasePlanMonthly: number;
  orderAmountMonthly: number;
}

export interface ModelReceiptStatus {
  directPurchaseReceipt: number;
  subcontractReceipt: number;
  totalMaterialReceipt: number;
  receiptRatioMonthly: number;
}

export interface WarehouseMaterialStatus {
  wiringWaiting: number;
  wiringInProcess: number;
  wiringTotal: number;
  mechanismWaiting: number;
  mechanismInProcess: number;
  mechanismTotal: number;
  packingWaiting: number;
  packingInProcess: number;
  packingTotal: number;
  subMaterialsWaiting: number;
  subMaterialsInProcess: number;
  subMaterialsTotal: number;
  otherWaiting: number;
  otherInProcess: number;
  otherTotal: number;
}





const StyledComponent = styled.div`
  .print-info {
    display: none;
  }

  @media print {
    max-width: 500mm;
    font-size: 10px;
    @page {
      size: A4 landscape;
    }
    h1 {
      font-size: 16px;
      text-align: center;
    }
    h3 {
      font-size: 14px;
    }
    .no-print {
      display: none;
    }
    .print-info {
      display: block;
    }
  }
`;

const PurchaseAndReceipt = () => {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [dailyMaterialCostData, setDailyMaterialCostData] = useState<MaterialCost[]>([]);
  const [stockStatusData, setStockStatusData] = useState<StockStatus[]>([]);
  const [modelPurchasePlanData, setModelPurchasePlanData] = useState<ModelPurchasePlan[]>([]);
  const [modelReceiptStatusData, setModelReceiptStatusData] = useState<ModelReceiptStatus[]>([]);
  const [warehouseMaterialStatusData, setWarehouseMaterialStatusData] = useState<WarehouseMaterialStatus[]>([]);
  const componentRef = useRef<any>();
  const [currentMonthAndWeek, setCurrentMonthAndWeek] = useState({ month: 0, week: 0 });
  const location = useLocation();
  const navigate = useNavigate();
  const currentDate = getDateTime();
  const { userName } = useNameContext();

  const [dailyMaterialGridParams, setDailyMaterialGridParams] = useState<any>(null);
  const [stockStatusGridParams, setStockStatusGridParams] = useState<any>(null);
  const [modelPurchasePlanGridParams, setModelPurchasePlanGridParams] = useState<any>(null);
  const [modelReceiptStatusGridParams, setModelReceiptStatusGridParams] = useState<any>(null);
  const [warehouseMaterialGridParams, setWarehouseMaterialGridParams] = useState<any>(null);

  const { details } = location.state || {};

  const getCurrentMonthAndWeek = () => {
    const currentDate = new Date();
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    const firstDayOfMonth = new Date(year, month, 1);
    const firstSunday = new Date(firstDayOfMonth.setDate(firstDayOfMonth.getDate() - firstDayOfMonth.getDay()));

    const weekNumber = Math.ceil(((currentDate.getTime() - firstSunday.getTime()) / (1000 * 60 * 60 * 24) + 1) / 7);

    return { month: month + 1, week: weekNumber };
  };

  const fetchData = async () => {
    try {
      const response = await apiClient.get(PURCHASE_API);

      const dailyMaterialCost = response.data.dailyMaterialCost || [];
      const stockStatus = response.data.stockStatus || [];
      const modelPurchasePlan = response.data.modelPurchasePlan || [];
      const modelReceiptStatus = response.data.modelReceiptStatus || [];
      const warehouseMaterialStatus = response.data.warehouseMaterialStatus || [];

      // 합계 계산
      const sumDailyMeterial = caluMaterialCostData(dailyMaterialCost);
      const sumStockStatus = caluStockStatusData(stockStatus);
      const sumModelPurchasePlan = caluModelPurchasePlanData(modelPurchasePlan);
      const sumReceiptStatus = caluModelReceiptStatusData(modelReceiptStatus);
      const sumWarehouseMaterial = caluWarehouseMaterialStatusData(warehouseMaterialStatus);
      console.log(sumDailyMeterial);
      // 상태 업데이트
      setDailyMaterialCostData([...dailyMaterialCost, sumDailyMeterial]);
      setStockStatusData([...stockStatus, sumStockStatus]);
      setModelPurchasePlanData([...modelPurchasePlan, sumModelPurchasePlan]);
      setModelReceiptStatusData([...modelReceiptStatus, sumReceiptStatus]);
      setWarehouseMaterialStatusData([...warehouseMaterialStatus, sumWarehouseMaterial]);
    } catch (error) {
      console.error("데이터 가져오기 실패:", error);
      alert("데이터를 불러오는 데 실패했습니다. 나중에 다시 시도해주세요.");
    }
  };

  // 그리드가 준비되면 호출되는 함수
  const handleGridReady = useCallback((params: any, setParams: any) => {
    setParams(params);
    params.api.sizeColumnsToFit();
  }, []);

  useEffect(() => {
    const monthAndWeek = getCurrentMonthAndWeek();
    setCurrentMonthAndWeek(monthAndWeek);

    // 검색 결과가 없으면 데이터를 가져옴
    if (!details) {
      fetchData();
    } else {
      // 검색 결과가 있을 때 검색 결과 데이터를 상태에 설정
      setDailyMaterialCostData(details.dailyMaterialCost || []);
      setStockStatusData(details.stockStatus || []);
      setModelPurchasePlanData(details.modelPurchasePlan || []);
      setModelReceiptStatusData(details.modelReceiptStatus || []);
      setWarehouseMaterialStatusData(details.warehouseMaterialStatus || []);
    }
  }, [details]);

  // 검색 처리 함수
  const handleSearch = () => {
    apiClient
      .get(`${PURCHASE_API}/${startDate}/${endDate}`)
      .then((response) => {
        navigate("/search/main", {
          state: {
            searchResult: response.data,
            url: PURCHASE_API,
            redirect: "/purchase",
          },
        });
      })
      .catch((error) => {
        if (error.response.status === 500) {
          alert("검색 조건을 확인해주세요.");
        }
      });
  };

  const handleExcel = async () => {
    try {
      const response = await apiClient.get(DOWNLOAD_PURCHASE, { responseType: "blob" });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `구매자재현황.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("엑셀 다운로드 실패:", error);
      alert("엑셀 다운로드에 실패했습니다. 나중에 다시 시도해주세요.");
    }
  };

  return (
    <>
      <StyledComponent ref={componentRef}>
        <div className="print-info">
          <p>출력: {userName}</p>
          <p>출력시간: {currentDate}</p>
        </div>
        <Row className="justify-content-end align-items-center mb-4 mt-3">
          <h1>
            {details
              ? "검색결과"
              : `${currentMonthAndWeek.month}월 ${currentMonthAndWeek.week}주차 구매 및 입고 현황`}
          </h1>
          <Col xs="auto" className="no-print">
            <DateField
              label="조회기간"
              value={startDate}
              onChange={setStartDate}
            />
          </Col>
          <Col xs="auto" className="no-print">
            <span className="ms-2">~</span>
          </Col>
          <Col xs="auto" className="no-print">
            <DateField
              label=""
              value={endDate}
              onChange={setEndDate}
              minDate={startDate}
            />
          </Col>
          <Col xs="auto" className="no-print">
            <Button variant="light" onClick={handleSearch}>
              <img src={searchIcon} alt="Search" width="20" height="20" />
            </Button>
          </Col>
        </Row>

        <div className="mb-5">
          <h4>일일 자재 비용 현황</h4>
          <div className="d-flex justify-content-end mb-2 no-print">
            {dailyMaterialGridParams && (
              <HideColumns
                params={dailyMaterialGridParams}
                tableDef={DailyMaterialCost}
              />
            )}
          </div>
          <TableWrapper className="mt-5 table-wrapper" height="600px">
            <AgGridReact
              rowData={dailyMaterialCostData}
              columnDefs={DailyMaterialCost}
              defaultColDef={{ sortable: false, resizable: true, flex: 1 }}
              headerHeight={60}
              rowHeight={80}
              onGridReady={(params) =>
                handleGridReady(params, setDailyMaterialGridParams)
              }
            />
          </TableWrapper>
        </div>

        <div className="mb-5">
          <h4>자재 재고 현황</h4>
          <div className="d-flex justify-content-end mb-2 no-print">
            {stockStatusGridParams && (
              <HideColumns
                params={stockStatusGridParams}
                tableDef={StockStatus}
              />
            )}
          </div>
          <TableWrapper className="mt-5 table-wrapper" height="400px">
            <AgGridReact
              rowData={stockStatusData}
              columnDefs={StockStatus}
              defaultColDef={{ sortable: false, resizable: true, flex: 1 }}
              headerHeight={60}
              rowHeight={50}
              onGridReady={(params) =>
                handleGridReady(params, setStockStatusGridParams)
              }
            />
          </TableWrapper>
        </div>

        <div className="mb-5">
          <h4>모델별 구매 계획</h4>
          <div className="d-flex justify-content-end mb-2 no-print">
            {modelPurchasePlanGridParams && (
              <HideColumns
                params={modelPurchasePlanGridParams}
                tableDef={ModelPurchasePlan}
              />
            )}
          </div>
          <TableWrapper className="mt-5 table-wrapper" height="300px">
            <AgGridReact
              rowData={modelPurchasePlanData}
              columnDefs={ModelPurchasePlan}
              defaultColDef={{ sortable: false, resizable: true, flex: 1 }}
              headerHeight={60}
              rowHeight={50}
              onGridReady={(params) =>
                handleGridReady(params, setModelPurchasePlanGridParams)
              }
            />
          </TableWrapper>
        </div>

        <div className="mb-5">
          <h4>모델별 입고 현황</h4>
          <div className="d-flex justify-content-end mb-2 no-print">
            {modelReceiptStatusGridParams && (
              <HideColumns
                params={modelReceiptStatusGridParams}
                tableDef={ModelReceiptStatus}
              />
            )}
          </div>
          <TableWrapper className="mt-5 table-wrapper" height="300px">
            <AgGridReact
              rowData={modelReceiptStatusData}
              columnDefs={ModelReceiptStatus}
              defaultColDef={{ sortable: false, resizable: true, flex: 1 }}
              headerHeight={60}
              rowHeight={50}
              onGridReady={(params) =>
                handleGridReady(params, setModelReceiptStatusGridParams)
              }
            />
          </TableWrapper>
        </div>

        <div className="mb-5">
          <h4>창고별 자재 현황</h4>
          <div className="d-flex justify-content-end mb-2 no-print">
            {warehouseMaterialGridParams && (
              <HideColumns
                params={warehouseMaterialGridParams}
                tableDef={WarehouseMaterialStatus}
              />
            )}
          </div>
          <TableWrapper className="mt-5 table-wrapper" height="600px">
            <AgGridReact
              rowData={warehouseMaterialStatusData}
              columnDefs={WarehouseMaterialStatus}
              defaultColDef={{ sortable: false, resizable: true, flex: 1 }}
              headerHeight={60}
              rowHeight={60}
              onGridReady={(params) =>
                handleGridReady(params, setWarehouseMaterialGridParams)
              }
            />
          </TableWrapper>
        </div>

        <PrintPage
          handlePrintRef={componentRef}
          handleExcelDownload={handleExcel}
        />
      </StyledComponent>
    </>
  );
};

export default PurchaseAndReceipt;
