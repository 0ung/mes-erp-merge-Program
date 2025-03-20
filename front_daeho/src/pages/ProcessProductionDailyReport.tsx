import { Button, Col, FormControl, FormLabel, Row } from "react-bootstrap";
import PrintPage from "../components/tools/PrintPage";
import {
  manInputManage,
  productionCostAnalzye as productionCostAnalyze,
  productionPerformanceStatus,
  stopRisk,
  techProblem,
} from "../components/tools/tableDef";
import TableWrapper from "../components/tools/TableWrapper";
import { AgGridReact } from "ag-grid-react";
import { DateField } from "../components/tools/Search";
import { useState, useRef, useCallback, useMemo } from "react";
import searchIcon from "../asset/search.svg";
import { getData, getDateTime } from "../components/tools/utils";
import styled from "styled-components";
import { ColDef, ColGroupDef } from "ag-grid-community";
import apiClient from "../apiClient";
import {
  DOWNLOAD_PROCESS,
  PROCESS_API,
  PROCESS_UPDATE_API,
  UPDATE_PARTLIST_API,
} from "../constants/API";
import CustomModal from "../components/CustomModal";
import { useNameContext } from "../context/nameProvider";
import { useLoginContext } from "../context/LoginProvider";
import { useNavigate } from "react-router-dom";
import CustomButton from "../components/tools/CustomButton";
import { HideColumns } from "../components/tools/HideColmuns";
// 1. ProductionData 인터페이스 수정
export interface ProductionData {
  no: number; // AG-Grid에서 No 값 사용
  lotNo: string;
  productName: string;
  modelNo: string;
  specification: string;
  unit: string;
  plannedQuantity: number; // 계획수량
  inputQuantity: number; // 투입수량
  defectiveQuantity: number; // 불량수량
  defectRate: number; // 불량율
  completedQuantity: number; // 생산완료수량
  achievementRate: number; // 달성율
  workInProgressQuantity: number; // 재공수량
  materialCost: number; // 재료비
  manHours: number; // 공수(초)
  processingCost: number; // 가공비
  subtotal: number; // 소계
  pricePerSet: number; // /1set 가격
  totalProduction: number; // 총생산
  performanceMaterialCost: number; // 실적 재료비
  performanceProcessingCost: number; // 실적 가공비
  totalPerformanceAmount: number; // 총 실적 금액
  monthlyCumulativeProduction: number; // 월간 누적 생산량
}

// 2. ManInputManageData 인터페이스 수정
export interface ManInputManageData {
  availablePersonnel: number; // 가용인원
  availableManHours: number; // 가용공수
  standardManHours: number; // 표준공수
  nonProductiveManHours: number; // 비생산공수
  workloadManHours: number; // 부하공수
  stopManHours: number; // 정지공수
  reworkManHours: number; // 재작업공수
  actualManHours: number; // 실동공수
  workingManHours: number; // 작업공수
  workEfficiency: number; // 작업 능률
  actualEfficiency: number; // 실동 효율
  lossRate: number; // Loss율
  manHourInputRate: number; // 공수 투입율
  manHourOperationRate: number; // 공수 가동율
  overallManHourEfficiency: number; // 공수 종합 효율
  overtimePersonnel: number; // 잔업 인원
  overtimeManHours: number; // 잔업 공수
  additionalInputRate: number; // 추가 투입율
  fluxOnTime: number; // Flux 설비 전원 ON 시간
  fluxOperatingTime: number; // Flux 실가동시간
  fluxOperatingRate: number; // Flux 실가동율
  solderingOnTime: number; // Soldering 설비 전원 ON 시간
  solderingOperatingTime: number; // Soldering 실가동시간
  solderingOperatingRate: number; // Soldering 실가동율
  remarks: string; // 비고
}

// 3. ProductionCostData 인터페이스 수정
export interface ProductionCostData {
  totalProductionMaterialCostSum: number; // 총 생산 재료비 합계
  processUsageSubMaterialSum: number; // 공정 사용 부자재 합계
  materialTotalSum: number; // 재료비 총합계
  totalProductionProcessingCostSum: number; // 총 생산 가공비 합계
  processInOutsourcingWorkSum: number; // 공정 내 외주작업 합계
  processTotalSum: number; // 총합계
  totalProductionActualSum: number; // 총 생산 실적 합계
  defectiveQuantity: number; // 불량 수량
  defectiveCost: number; // 불량 비용
  stopAndNonproductiveHours: number; // 정지 & 비생산 시간
  stopAndNonproductiveCost: number; // 정지 & 비생산 비용
  reworkHours: number; // 재작업 시간
  reworkCost: number; // 재작업 비용
  totalCost: number; // 비용 합계
  manufacturingExpenseIndirect: number; // 제조 간접비
  manufacturingExpenseGeneralAdmin: number; // 일반 관리비
  manufacturingExpenseSellingAndAdmin: number; // 판매 관리비
  manufacturingExpenseDepreciationEtc: number; // 설비감가 및 기타 비용
  manufacturingExpenseTotal: number; // 제조 경비 합계
  estimateCostTotal: number; // 총 견적 비용
  processTotalProductionInputAmount: number; // 투입 금액
  processTotalProductionActualProfit: number; // 실적 이익
  processTotalProductionProfitRate: number; // 이익율
  processTotalProductionLossRate: number; // 손실율
  processTotalProductionMaterialRate: number; // 재료비율
  processTotalProductionProcessingRate: number; // 가공비율
}

export interface techProblemProps {
  id: number; // DailyWorkLoss ID
  category: string; // 카테고리
  lossEffectDate: string; // 발생일자
  lotNo: string; // 작업지시번호
  lossEffectDept: string; // 발생부서
  lossTime: number; // 유실시간 (분)
  lossWorker: number; // 투입인원
  lossTimeTotal: number; // 총합계시간 (분)
  lossReason: string; // 사유구분
  lossContents: string; // 세부내용
  lossMeasure: string; // 조치내용
  lossAmount: number; // 발생금액
  lossBlameDept01: string; // 책임부서 1
  lossRate01: number; // 책임비율 1
  lossBlameDept02: string; // 책임부서 2
  lossRate02: number; // 책임비율 2
  lossBlameDept03: string; // 책임부서 3
  lossRate03: number; // 책임비율 3
  progressState: string; // 진행상태
  remark: string; // 비고
  snapShot: boolean; // 00시 스냅샷 데이터 여부
}

export interface stopRisksProps {
  id: number; // DailyWorkLoss ID
  category: string; // 카테고리
  lossEffectDate: string; // 발생일자
  lotNo: string; // 작업지시번호
  lossEffectDept: string; // 발생부서
  lossTime: number; // 유실시간 (분)
  lossWorker: number; // 투입인원
  lossTimeTotal: number; // 총합계시간 (분)
  lossReason: string; // 사유구분
  lossContents: string; // 세부내용
  lossMeasure: string; // 조치내용
  lossAmount: number; // 발생금액
  lossBlameDept01: string; // 책임부서 1
  lossRate01: number; // 책임비율 1
  lossBlameDept02: string; // 책임부서 2
  lossRate02: number; // 책임비율 2
  lossBlameDept03: string; // 책임부서 3
  lossRate03: number; // 책임비율 3
  progressState: string; // 진행상태
  remark: string; // 비고
  snapShot: boolean; // 00시 스냅샷 데이터 여부
}

export interface additionalDataType {
  category: string;
  availableManHours: number;
  availablePersonnel: number;
  outSouringCost: number;
}

export interface ProcessReportProps {
  processName: string;
  productionData: ProductionData[];
  manInputManageData: ManInputManageData[];
  productionCostData: ProductionCostData[];
  techProblems: techProblemProps[];
  stopRisks: stopRisksProps[];
  search?: boolean;
  searchData?: string;
}

const StyledComponent = styled.div`
  .print-info {
    display: none;
  }

  @media print {
    .ag-theme-quartz {
      max-width: 100%; /* A4 용지 기준 너비가 맞지 않을 수 있음 */
    }
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

    /* 정지 RISK는 다음 페이지에서 시작 */
    .page-break {
      page-break-before: always;
    }

    /* 셀과 헤더의 너비를 비율에 따라 조정 */
    .ag-cell,
    .ag-header-cell {
      font-size: 11px;
    }
  }
`;

interface AdditionalDataType {
  category: string;
  availableManHours: number;
  availablePersonnel: number;
  outSouringCost: number;
}

const ProcessProductionDailyReport: React.FC<ProcessReportProps> = ({
  processName,
  productionData,
  manInputManageData,
  productionCostData,
  techProblems,
  stopRisks,
  search,
  searchData,
}) => {
  const { userName } = useNameContext();
  const { auth } = useLoginContext();
  const navigate = useNavigate();
  const currentDate = getDateTime();
  const printRef = useRef<HTMLDivElement>(null);

  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [showModal, setShowModal] = useState(false);
  const [data, setAdditionalData] = useState<AdditionalDataType>({
    category: processName,
    availableManHours: 0,
    availablePersonnel: 0,
    outSouringCost: 0,
  });

  // 각각의 테이블에 대한 GridParams 상태 관리
  const [productionGridParams, setProductionGridParams] = useState<any>(null);
  const [manInputGridParams, setManInputGridParams] = useState<any>(null);
  const [productionCostGridParams, setProductionCostGridParams] =
    useState<any>(null);
  const [techProblemGridParams, setTechProblemGridParams] = useState<any>(null);
  const [stopRiskGridParams, setStopRiskGridParams] = useState<any>(null);

  // 권한에 따른 컬럼 필터링
  const hiddenFieldsByProduction: Record<"C" | "B" | "A" | "관리자", string[]> =
    {
      C: [
        "materialCost",
        "manHours",
        "processingCost",
        "subtotal",
        "pricePerSet",
        "totalProduction",
        "performanceMaterialCost",
        "performanceProcessingCost",
        "totalPerformanceAmount",
      ],
      B: [
        "materialCost",
        "manHours",
        "processingCost",
        "subtotal",
        "pricePerSet",
        "totalProduction",
        "performanceMaterialCost",
        "performanceProcessingCost",
        "totalPerformanceAmount",
      ],
      A: [], // A 권한에서 숨길 필드가 없음
      관리자: [], // 관리자 권한에서 숨길 필드가 없음
    };

  const getFilteredColumnDefs = useCallback(
    (
      columns: (ColDef | ColGroupDef)[],
      hiddenFields: string[]
    ): (ColDef | ColGroupDef)[] => {
      return columns
        .map((col) => {
          if ("children" in col && Array.isArray(col.children)) {
            return {
              ...col,
              children: getFilteredColumnDefs(col.children, hiddenFields),
            };
          }
          if ("field" in col) {
            return hiddenFields.includes(col.field!) ? null : col;
          }
          return col;
        })
        .filter((col): col is ColDef | ColGroupDef => col !== null);
    },
    []
  );
  const date = getData();
  const productionPerformanceStatusDef = useMemo(() => {
    if (!auth || !(auth in hiddenFieldsByProduction))
      return productionPerformanceStatus;
    return getFilteredColumnDefs(
      productionPerformanceStatus,
      hiddenFieldsByProduction[auth as "C" | "B" | "A" | "관리자"] // 타입 단언 사용
    );
  }, [auth, productionPerformanceStatus, getFilteredColumnDefs]);

  // 그리드 리사이즈 처리
  const handleGridReady = useCallback((params: any) => {
    params.api.sizeColumnsToFit();
  }, []);

  const handleSearch = useCallback(async () => {
    try {
      const response = await apiClient.get(
        `${PROCESS_API}/${processName}/${startDate}/${endDate}`
      );
      navigate("/search/main", {
        state: {
          searchResult: response.data,
          url: `${PROCESS_API}/detail`,
          redirect: `/ProcessProductionDailyReport/${processName
            .toLowerCase()
            .replace("assy", "")}`,
        },
      });
    } catch (error) {
      console.error("Error occurred while fetching search data:", error);
    }
  }, [startDate, endDate, processName, navigate]);

  const handleExcelDownload = useCallback(async () => {
    try {
      const response = await apiClient.get(
        `${DOWNLOAD_PROCESS}/${processName}`,
        { responseType: "blob" }
      );
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `${processName}공정생산일보.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("다운로드 에러", error);
    }
  }, [processName]);

  const handleUpdateAdditionalData = useCallback(async () => {
    try {
      const response = await apiClient.put(PROCESS_UPDATE_API, data);
      if (response.status === 200) {
        console.log(
          `Data for category ${data.category} was successfully updated.`
        );
      } else {
        console.warn(
          `Unexpected response status ${response.status}: ${response.statusText}`
        );
      }
    } catch (error) {
      console.error(
        `Failed to update data for category ${data.category}:`,
        error
      );
    }
  }, [data]);

  const validateData = useCallback(() => {
    return (
      data.availableManHours !== null &&
      data.availablePersonnel !== null &&
      data.outSouringCost !== null
    );
  }, [data]);

  const updatePartList = async () => {
    apiClient
      .get(UPDATE_PARTLIST_API, { timeout: 60000 }) // 60000ms = 1분
      .then((resp) => {
        console.log(resp);
        alert("가격 정보가 업데이트 되었습니다.");
      })
      .catch((error) => {
        if (error.code === "ECONNABORTED") {
          console.log("요청이 시간 초과되었습니다.");
          alert("요청이 시간 초과되었습니다. 다시 시도해주세요.");
        } else {
          console.log(error);
        }
      });
  };

  return (
    <>
      <StyledComponent ref={printRef}>
        <div className="print-info">
          <p>출력: {userName}</p>
          <p>출력시간: {currentDate}</p>
        </div>
        <Row className="justify-content-end align-items-center mb-4 mt-3">
          <h1>
            {search
              ? `${searchData} ${processName} 공정 생산일보`
              : `${date} ${processName} 공정 생산일보`}
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
          <Row className="justify-content-end align-items-center mb-4 mt-3">
            <CustomButton
              style={{ width: "200px", height: "40px" }} // 버튼의 가로, 세로 크기 조정
              onClick={() => {
                updatePartList();
              }}
              className="no-print me-3"
            >
              가격 정보업데이트
            </CustomButton>

            <CustomButton
              style={{ width: "100px", height: "40px" }} // 버튼의 가로, 세로 크기 조정
              onClick={() => {
                setShowModal(true);
              }}
              className="no-print"
            >
              업데이트
            </CustomButton>
          </Row>
        </Row>

        <div className="d-flex justify-content-end mb-2 no-print">
          {productionGridParams && (
            <HideColumns
              params={productionGridParams}
              tableDef={productionPerformanceStatusDef}
            />
          )}
        </div>
        <TableWrapper className="mt-4" height="500px">
          <AgGridReact
            rowData={productionData}
            columnDefs={productionPerformanceStatusDef}
            defaultColDef={{ sortable: false, resizable: true, flex: 1 }}
            headerHeight={40}
            rowHeight={50}
            onGridReady={(params) => {
              handleGridReady(params);
              setProductionGridParams(params);
            }}
          />
        </TableWrapper>

        <h3 className="mt-5">공수투입관리</h3>
        <div className="d-flex justify-content-end mb-2 no-print">
          {manInputGridParams && (
            <HideColumns
              params={productionGridParams}
              tableDef={manInputManage}
            />
          )}
        </div>
        <TableWrapper className="mt-4" height="232px">
          <AgGridReact
            rowData={manInputManageData}
            columnDefs={manInputManage}
            defaultColDef={{ sortable: false, resizable: true, flex: 1 }}
            headerHeight={60}
            rowHeight={50}
            onGridReady={(params) => {
              handleGridReady(params);
              setManInputGridParams(params);
            }}
          />
        </TableWrapper>

        {auth !== "C" && auth !== "B" && (
          <>
            <h3 className="mt-5">생산비용분석</h3>
            <div className="d-flex justify-content-end mb-2 no-print">
              {productionCostGridParams && (
                <HideColumns
                  params={productionCostGridParams}
                  tableDef={productionCostAnalyze}
                />
              )}
            </div>
            <TableWrapper className="mt-4" height="202px">
              <AgGridReact
                rowData={productionCostData}
                columnDefs={productionCostAnalyze}
                defaultColDef={{ sortable: false, resizable: true, flex: 1 }}
                headerHeight={50}
                rowHeight={50}
                onGridReady={(params) => {
                  handleGridReady(params);
                  setProductionCostGridParams(params); // productionCostGridParams 설정
                }}
              />
            </TableWrapper>

            <div className="page-break">
              <h3 className="mt-5">생산문제점 분석 및 개선조치 내용</h3>
              <h5 className="mt-5">기술적 문제점</h5>
              <div className="d-flex justify-content-end mb-2 no-print">
                {techProblemGridParams && (
                  <HideColumns
                    params={techProblemGridParams}
                    tableDef={techProblem}
                  />
                )}
              </div>
              <TableWrapper className="mt-4" height="200px">
                <AgGridReact
                  rowData={techProblems}
                  columnDefs={techProblem}
                  defaultColDef={{ sortable: false, resizable: true, flex: 1 }}
                  onGridReady={(params) => {
                    handleGridReady(params);
                    setTechProblemGridParams(params); // techProblemGridParams 설정
                  }}
                />
              </TableWrapper>

              <h5 className="mt-5">정지 RISK</h5>
              <div className="d-flex justify-content-end mb-2 no-print">
                {stopRiskGridParams && (
                  <HideColumns
                    params={stopRiskGridParams}
                    tableDef={stopRisk}
                  />
                )}
              </div>
              <TableWrapper height="200px">
                <AgGridReact
                  rowData={stopRisks}
                  columnDefs={stopRisk}
                  defaultColDef={{ sortable: false, resizable: true, flex: 1 }}
                  onGridReady={(params) => {
                    handleGridReady(params);
                    setStopRiskGridParams(params); // stopRiskGridParams 설정
                  }}
                />
              </TableWrapper>
            </div>
          </>
        )}
      </StyledComponent>

      {showModal && (
        <CustomModal
          show={showModal}
          handleClose={() => setShowModal(false)}
          handleSave={handleUpdateAdditionalData}
          title="추가 데이터 수정/등록"
          activeModal={validateData}
        >
          <FormLabel className="fw-bold mt-3">가용인원</FormLabel>
          <FormControl
            type="number"
            placeholder="가용인원"
            className="p-2"
            onChange={(event) =>
              setAdditionalData({
                ...data,
                availablePersonnel: Number(event.target.value),
              })
            }
          />
          <FormLabel className="fw-bold mt-3">가용공수</FormLabel>
          <FormControl
            type="number"
            placeholder="가용공수"
            className="p-2"
            onChange={(event) =>
              setAdditionalData({
                ...data,
                availableManHours: Number(event.target.value),
              })
            }
          />
          <FormLabel className="fw-bold mt-3">공정 내 외주작업 합계</FormLabel>
          <FormControl
            type="number"
            placeholder="공정 내 외주작업 합계"
            className="p-2"
            onChange={(event) =>
              setAdditionalData({
                ...data,
                outSouringCost: Number(event.target.value),
              })
            }
          />
        </CustomModal>
      )}

      <PrintPage
        handleExcelDownload={handleExcelDownload}
        handlePrintRef={printRef}
      />
    </>
  );
};

export default ProcessProductionDailyReport;
