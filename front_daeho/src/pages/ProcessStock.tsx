import { AgGridReact } from "ag-grid-react";
import TableWrapper from "../components/tools/TableWrapper";
import { processStock } from "../components/tools/tableDef";
import { getData } from "../components/tools/utils";
import { Button, Col, Row } from "react-bootstrap";
import { DateField } from "../components/tools/Search";
import { useEffect, useState, useMemo } from "react";
import searchIcon from "../asset/search.svg";
import apiClient from "../apiClient";
import { PROCESS_STOCK_API } from "../constants/API";
import { useLocation, useNavigate } from "react-router-dom";

interface ProcessStockType {
  category: string;
  productName: string;
  modelNo: string;
  specification: string;
  materialCost: number;
  processingCost: number;
  totalCost: number;
  wipQuantity: number;
  wipCost: number;
  qcPendingQuantity: number;
  qcPendingCost: number;
  qcPassedQuantity: number;
  qcPassedCost: number;
  defectiveQuantity: number;
  defectiveCost: number;
  totalQuantity: number;
  totalCostSummary: number;
  remarks: string;
}

interface GroupedData {
  category: string;
  items: ProcessStockType[];
}

// 데이터를 category별로 그룹화하는 함수
const groupByCategory = (data: ProcessStockType[]): GroupedData[] => {
  return data.reduce((acc: GroupedData[], item) => {
    const existingGroup = acc.find((group) => group.category === item.category);
    if (existingGroup) {
      existingGroup.items.push(item);
    } else {
      acc.push({ category: item.category, items: [item] });
    }
    return acc;
  }, []);
};

function ProcessStock() {
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [processData, setProcessData] = useState<ProcessStockType[]>([]); // 빈 배열로 초기화
  const navigate = useNavigate();
  const date = getData();
  const location = useLocation();
  const { details } = location.state || {};

  // API에서 데이터를 불러오는 함수
  const handleProcessStock = async () => {
    try {
      const response = await apiClient.get(PROCESS_STOCK_API);
      setProcessData(response.data); // 데이터를 상태로 설정
    } catch (error) {
      console.log("에러 발생: ", error);
    }
  };

  useEffect(() => {
    if (!details) {
      handleProcessStock();
    } else {
      setProcessData(details);
    }
  }, [details]);

  // 데이터가 변경될 때만 groupByCategory 함수 실행 (메모이제이션)
  const groupedData = useMemo(
    () => groupByCategory(processData),
    [processData]
  );

  // 검색 기능을 위한 함수
  const handleSearch = async () => {
    try {
      const response = await apiClient.get(
        `${PROCESS_STOCK_API}/${startDate}/${endDate}`
      );
      navigate("/search/main", {
        state: {
          searchResult: response.data,
          url: PROCESS_STOCK_API,
          redirect: "/processStock",
        },
      });
    } catch (error: any) {
      if (error.response?.status === 500) {
        alert("검색조건을 확인해주세요");
      } else {
        console.error("검색 중 에러 발생: ", error);
      }
    }
  };

  return (
    <>
      <Row className="justify-content-end align-items-center mb-4 mt-3">
        <h1>{details ? "검색결과" : `${date} 공정별 재고`}</h1>
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
      {groupedData.map((group, index) => (
        <div className="mt-5 mb-5" key={index}>
          <h4>{group.category} 공정</h4>
          <TableWrapper className="mt-5 table-wrapper" height="300px">
            <AgGridReact
              rowData={group.items} // 그룹화된 데이터를 전달
              columnDefs={processStock}
              defaultColDef={{
                sortable: false,
                resizable: true,
                flex: 1,
              }}
              headerHeight={60}
              rowHeight={70}
            />
          </TableWrapper>
        </div>
      ))}
    </>
  );
}

export default ProcessStock;
