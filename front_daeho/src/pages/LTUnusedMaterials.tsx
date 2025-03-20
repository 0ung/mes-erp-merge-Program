import { useEffect, useState, useMemo, useRef, useCallback } from "react";
import { AgGridReact } from "ag-grid-react";
import TableWrapper from "../components/tools/TableWrapper";
import { Button, Col, Row } from "react-bootstrap";
import { DateField } from "../components/tools/Search";
import searchIcon from "../asset/search.svg";
import apiClient from "../apiClient";
import { LT_UNUSED_MATERIAL_API } from "../constants/API"; // 가정된 API 경로
import { LTUnusedMaterial } from "../components/tools/tableDef";
import { getData, getDateTime } from "../components/tools/utils";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import PrintPage from "../components/tools/PrintPage";
import { useNameContext } from "../context/nameProvider";
import { HideColumns } from "../components/tools/HideColmuns";

interface LTUnusedMaterialType {
  ItemCd: string;
  ItemName: string;
  DateOfOccurrence: string;
  Reason: string;
  ApplyContents: string;
  ResponsibilityClassification: string;
  StockQty: number;
  BeforeOccurrenceUsingQty: number;
  AfterOccurrenceUsingQty: number;
  LongTermInventory: number;
  InsolvencyStock: number;
  Resale: number;
  Disuse: number;
}

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
function LTUnusedMaterials() {
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const { userName } = useNameContext();
  const currentDate = getDateTime();
  const [unusedMaterialsData, setUnusedMaterialsData] = useState<
    LTUnusedMaterialType[] | undefined
  >();
  const [gridParams, setGridParams] = useState<any>(null); // GridParams 상태 추가
  const date = getData();
  const navigate = useNavigate();
  const componentRef = useRef<any>();

  // 데이터를 가져오는 함수
  const fetchUnusedMaterials = async () => {
    try {
      const response = await apiClient.get(LT_UNUSED_MATERIAL_API);
      setUnusedMaterialsData(response.data);
    } catch (error) {
      console.error("에러 발생: ", error);
    }
  };

  // 컴포넌트가 마운트될 때 데이터 불러오기
  useEffect(() => {
    fetchUnusedMaterials();
  }, []);

  const handleSearch = async () => {
    try {
      const response = await apiClient.get(
        `${LT_UNUSED_MATERIAL_API}/${startDate}/${endDate}`
      );
      navigate("/search/main", {
        state: {
          searchResult: response.data,
          url: LT_UNUSED_MATERIAL_API,
          redirect: "/lt",
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

  const handleGridReady = useCallback((params: any) => {
    setGridParams(params); // GridParams 상태 설정
    params.api.sizeColumnsToFit(); // 그리드의 컬럼 크기를 맞춤
  }, []);

  // 데이터 및 컬럼 정의 최적화
  const memoizedUnusedMaterialsData = useMemo(
    () => unusedMaterialsData || [],
    [unusedMaterialsData]
  );

  const memoizedColumnDefs = useMemo(() => LTUnusedMaterial, []);

  return (
    <StyledComponent ref={componentRef}>
      <div className="print-info">
        <p>출력: {userName}</p>
        <p>출력시간: {currentDate}</p>
      </div>
      <Row className="justify-content-end align-items-center mb-4 mt-3">
        <h1>{date} 장기불용 자재 </h1>
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
        {gridParams && (
          <HideColumns
            params={gridParams} // GridParams 전달
            tableDef={memoizedColumnDefs}
          />
        )}
      </div>

      <TableWrapper className="mt-5 table-wrapper" height="300px">
        <AgGridReact
          rowData={memoizedUnusedMaterialsData}
          columnDefs={memoizedColumnDefs}
          defaultColDef={{
            sortable: true,
            resizable: true,
            flex: 1,
          }}
          headerHeight={60}
          rowHeight={50}
          onGridReady={handleGridReady} // GridParams 설정
        />
      </TableWrapper>
      <PrintPage handlePrintRef={componentRef} />
    </StyledComponent>
  );
}

export default LTUnusedMaterials;
