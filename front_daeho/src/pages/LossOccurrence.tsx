import { ColDef, ColGroupDef } from "ag-grid-community";
import TableWrapper from "../components/tools/TableWrapper";
import { AgGridReact } from "ag-grid-react";
import { getData, getDateTime } from "../components/tools/utils";
import PrintPage from "../components/tools/PrintPage";
import { Button, Col, Row } from "react-bootstrap";
import { DateField, TextField } from "../components/tools/Search";
import { useEffect, useRef, useState } from "react";
import searchIcon from "../asset/search.svg";
import styled from "styled-components";
import apiClient from "../apiClient";
import { DOWNLOAD_LOSS, LOSS_API } from "../constants/API";
import { useNameContext } from "../context/nameProvider";
import { useNavigate } from "react-router-dom";

interface LossOccurrenceStatusType {
  lossEffectDate: string; // 발생일자
  lotNo: string; // 작업지시번호
  lossEffectDept: string; // 발생부서
  lossTime: number; // 유실시간(분)
  lossWorker: number; // 투입인원
  lossTimeTotal: number; // 총합계시간(분)
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
  stateProgressing: string; // 진행상태
  remark: string; // 비고
}

const lossOccurrenceStatus: (ColDef | ColGroupDef)[] = [
  { headerName: "발생일자", field: "lossEffectDate" }, // 발생일자
  { headerName: "작업지시번호", field: "lotNo" }, // 작업지시번호
  { headerName: "발생부서", field: "lossEffectDept" }, // 발생부서
  { headerName: "유실시간(분)", field: "lossTime" }, // 유실시간
  { headerName: "투입인원", field: "lossWorker" }, // 투입인원
  { headerName: "총합계시간(분)", field: "lossTimeTotal" }, // 총합계시간
  { headerName: "사유구분", field: "lossReason" }, // 사유구분
  { headerName: "세부내용", field: "lossContents" }, // 세부내용
  { headerName: "조치내용", field: "lossMeasure" }, // 조치내용
  { headerName: "발생금액", field: "lossAmount" }, // 발생금액
  { headerName: "책임부서 1", field: "lossBlameDept01" }, // 책임부서 1
  { headerName: "책임비율 1", field: "lossRate01" }, // 책임비율 1
  { headerName: "책임부서 2", field: "lossBlameDept02" }, // 책임부서 2
  { headerName: "책임비율 2", field: "lossRate02" }, // 책임비율 2
  { headerName: "책임부서 3", field: "lossBlameDept03" }, // 책임부서 3
  { headerName: "책임비율 3", field: "lossRate03" }, // 책임비율 3
  { headerName: "진행상태", field: "stateProgressing" }, // 진행상태
  { headerName: "비고", field: "remark" }, // 비고
];

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

function LossOccurrence() {
  const data = getData();
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [standard, setStandard] = useState<string>("");
  const currentDate = getDateTime();
  const [pageData, setPageData] = useState<LossOccurrenceStatusType[]>([]);
  const navigate = useNavigate();
  const { userName } = useNameContext();

  const handlePageData = async () => {
    try {
      const response = await apiClient.get(LOSS_API);
      setPageData(response.data);
      console.log(response.data);
    } catch (error) {
      console.error("Failed to fetch page data:", error);
    }
  };

  const componentRef = useRef<any>();
  const handleExcelDownload = async () => {
    try {
      const response = await apiClient.get(DOWNLOAD_LOSS, {
        responseType: "blob",
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "loss.xlsx");
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.log("다운로드 에러", error);
    }
  };

  useEffect(() => {
    handlePageData();
  }, []);

  const handleSearch = () => {
    apiClient
      .get(`${LOSS_API}/${startDate}/${endDate}?lossReason=${standard}`)
      .then((response) => {
        console.log(response.data);
        navigate("/search/main", {
          state: {
            searchResult: response.data,
            url: LOSS_API,
            redirect: "/loss",
          },
        });
      })
      .catch((error) => {
        if (error.response?.status === 500) {
          alert("검색조건을 확인해주세요");
        }
      });
  };

  return (
    <>
      <StyledComponent ref={componentRef}>
        <div className="print-info">
          <p>출력: {userName}</p>
          <p>출력시간: {currentDate}</p>
        </div>

        <h1 className="mb-4 mt-3">{data} LOSS 발생현황</h1>
        <Row className="justify-content-end align-items-center ">
          <Col xs="auto" className="no-print">
            <TextField label="구분" value={standard} onChange={setStandard} />
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
              <img src={searchIcon} alt="Search" width="20" height="20" />{" "}
            </Button>
          </Col>
        </Row>
        <TableWrapper>
          <div className="ag-theme-quartz mt-3">
            <AgGridReact
              rowData={pageData}
              columnDefs={lossOccurrenceStatus}
              defaultColDef={{
                sortable: true,
                resizable: true,
                flex: 1,
              }}
              domLayout="autoHeight"
            ></AgGridReact>
          </div>
        </TableWrapper>
        <PrintPage
          handleExcelDownload={handleExcelDownload}
          handlePrintRef={componentRef}
        />
      </StyledComponent>
    </>
  );
}

export default LossOccurrence;
