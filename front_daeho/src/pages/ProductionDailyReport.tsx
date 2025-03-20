import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-quartz.css";
import {
  attendanceStatus,
  costAnalyze,
  manPowerInputManage,
  manufacturingCostAnalysis,
} from "../components/tools/tableDef";
import { DateField } from "../components/tools/Search";
import { useState, useRef, useEffect, useMemo, useCallback } from "react";
import { Button, Col, Row } from "react-bootstrap";
import searchIcon from "../asset/search.svg"; // SVG 파일 경로
import PrintPage from "../components/tools/PrintPage";
import styled from "styled-components";
import { getData, getDateTime } from "../components/tools/utils";
import apiClient from "../apiClient";
import { DOWNLOAD_DAILY, PROCESS_MAIN_API } from "../constants/API";
import TableWrapper from "../components/tools/TableWrapper";
import { useNameContext } from "../context/nameProvider";
import { HideColumns } from "../components/tools/HideColmuns";
import { useLoginContext } from "../context/LoginProvider";
import { useLocation, useNavigate } from "react-router-dom";

const StyledComponent = styled.div`
  .print-info {
    display: none;
  }

  @media print {
    max-width: 500mm; /* 가로 A4 용지 크기에서 마진을 뺀 너비 */
    font-size: 10px;
    @page {
      size: A4 landscape;
    }

    /* 제목 크기 및 마진 조정 */
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

function ProductionDailyReport() {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const { userName } = useNameContext();
  const [mainData, setMainData] = useState({
    attendanceStatus: [],
    costAnalyze: [],
    manPowerInputManage: [],
    manufacturingCostAnalysis: [],
  });
  const location = useLocation();
  const { details } = location.state || {};
  const [attendanceGridParams, setAttendanceGridParams] = useState<any>(null);
  const [manPowerGridParams, setManPowerGridParams] = useState<any>(null);
  const [costAnalyzeGridParams, setCostAnalyzeGridParams] = useState<any>(null);
  const [manufacturingCostGridParams, setManufacturingCostGridParams] =
    useState<any>(null);
  const navigate = useNavigate();
  const { auth } = useLoginContext();
  const currentDate = getDateTime();
  const componentRef = useRef<any>();
  const date = getData();
  const attendanceStatusDef = useMemo(() => attendanceStatus, []);
  const manPowerInputManageDef = useMemo(() => manPowerInputManage, []);
  const costAnalyzeDef = useMemo(() => costAnalyze, []);
  const manufacturingCostAnalysisDef = useMemo(
    () => manufacturingCostAnalysis,
    []
  );

  // 데이터 로딩 함수
  const handleMainData = useCallback(async () => {
    try {
      const res = await apiClient.get(PROCESS_MAIN_API);
      setMainData(res.data);
    } catch (error) {
      console.error("Error occurred while fetching main process data:", error);
    }
  }, []);

  // 검색 기능
  const handleSearch = useCallback(async () => {
    try {
      const response = await apiClient.get(
        `${PROCESS_MAIN_API}/${startDate}/${endDate}`
      );
      navigate("/search/main", {
        state: {
          searchResult: response.data,
          url: PROCESS_MAIN_API,
          redirect: "/productionDailyReport",
        },
      });
    } catch (error) {
      console.error("Error occurred while fetching search data:", error);
    }
  }, [startDate, endDate, navigate]);

  // 엑셀 다운로드 기능
  const handleExcelDownload = useCallback(async () => {
    try {
      const response = await apiClient.get(DOWNLOAD_DAILY, {
        responseType: "blob",
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "메인생산일보.xlsx");
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.log("다운로드 에러", error);
    }
  }, []);

  useEffect(() => {
    if (!details) {
      handleMainData();
    } else {
      setMainData(details);
    }
  }, [details, handleMainData]);

  return (
    <>
      <StyledComponent ref={componentRef}>
        <div className="print-info">
          <p>출력: {userName}</p>
          <p>출력시간: {currentDate}</p>
        </div>
        {details ? (
          <h1>{details.createDate} 메인생산일보</h1>
        ) : (
          <h1 className="mb-4 mt-3">{date}메인생산일보</h1>
        )}

        <Row className="justify-content-end align-items-center mb-4">
          <Col>
            <h3>1. 근태현황</h3>
          </Col>
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
        <div className="d-flex justify-content-end mb-2 no-print">
          {attendanceGridParams && (
            <HideColumns
              params={attendanceGridParams}
              tableDef={attendanceStatusDef}
            />
          )}
        </div>
        <TableWrapper className="mb-5 table-wrapper" height="172px">
          <AgGridReact
            rowData={mainData.attendanceStatus}
            columnDefs={attendanceStatusDef}
            defaultColDef={{
              sortable: false,
              resizable: true,
              flex: 1,
            }}
            headerHeight={30}
            rowHeight={50}
            onGridReady={(params) => {
              params.api.sizeColumnsToFit();
              setAttendanceGridParams(params);
            }}
          />
        </TableWrapper>

        {/* 공수투입 관리 테이블 */}
        <h3 className="mb-4">2. 공수투입관리</h3>
        <div className="d-flex justify-content-end mb-2 no-print">
          {manPowerGridParams && (
            <HideColumns
              params={manPowerGridParams}
              tableDef={manPowerInputManageDef}
            />
          )}
        </div>
        <TableWrapper className="mb-5 table-wrapper" height="157px">
          <AgGridReact
            rowData={mainData.manPowerInputManage}
            columnDefs={manPowerInputManageDef}
            defaultColDef={{
              sortable: false,
              resizable: true,
              flex: 1.5,
            }}
            headerHeight={35}
            rowHeight={50}
            onGridReady={(params) => {
              params.api.sizeColumnsToFit();
              setManPowerGridParams(params);
            }}
          />
        </TableWrapper>

        {/* 생산 비용 분석 테이블 */}
        {auth !== "B" && auth !== "C" && (
          <>
            <h3 className="mb-4">3. 생산비용분석</h3>
            <div className="d-flex justify-content-end mb-2 no-print">
              {costAnalyzeGridParams && (
                <HideColumns
                  params={costAnalyzeGridParams}
                  tableDef={costAnalyzeDef}
                />
              )}
            </div>
            <TableWrapper className="mb-5 table-wrapper" height="157px">
              <AgGridReact
                rowData={mainData.costAnalyze}
                columnDefs={costAnalyzeDef}
                defaultColDef={{
                  sortable: false,
                  resizable: true,
                  flex: 1.5,
                }}
                headerHeight={35}
                rowHeight={50}
                onGridReady={(params) => {
                  params.api.sizeColumnsToFit();
                  setCostAnalyzeGridParams(params);
                }}
              />
            </TableWrapper>

            <h3 className="mb-4">4. 제조원가분석</h3>
            <div className="d-flex justify-content-end mb-2 no-print">
              {manufacturingCostGridParams && (
                <HideColumns
                  params={manufacturingCostGridParams}
                  tableDef={manufacturingCostAnalysisDef}
                />
              )}
            </div>
            <TableWrapper className="mb-5 table-wrapper" height="122px">
              <AgGridReact
                rowData={mainData.manufacturingCostAnalysis}
                columnDefs={manufacturingCostAnalysisDef}
                defaultColDef={{
                  sortable: false,
                  resizable: true,
                  flex: 1.5,
                }}
                headerHeight={35}
                rowHeight={50}
                onGridReady={(params) => {
                  params.api.sizeColumnsToFit();
                  setManufacturingCostGridParams(params);
                }}
              />
            </TableWrapper>
          </>
        )}
      </StyledComponent>

      {/* PrintPage 컴포넌트에 handlePrintRef를 전달 */}
      <PrintPage
        handleExcelDownload={handleExcelDownload}
        handlePrintRef={componentRef}
      />
    </>
  );
}

export default ProductionDailyReport;
