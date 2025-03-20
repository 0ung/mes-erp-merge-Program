import { AgGridReact } from "ag-grid-react";
import { ColDef, ColGroupDef } from "ag-grid-community";
import { Container } from "react-bootstrap";
import TableWrapper from "../tools/TableWrapper";
import { useState, useEffect } from "react";
import apiClient from "../../apiClient";
import {
  LOG_ACCESS_DOWNLOAD_API,
  LOG_API,
  LOG_DOWNLOAD_DOWNLOAD_API,
  LOG_LOGIN_DOWNLOAD_API,
  LOG_PRINT_DOWNLOAD_API,
} from "../../constants/API";
import excelIamge from "../../asset/excel.png";

// 각 로그 데이터에 대한 타입 정의
interface LoginLog {
  time: string;
  accessID: string;
  accessIP: string;
}

interface PageAccessLog {
  time: string;
  accessID: string;
  accessPage: string;
  accessIP: string;
}

interface PrintLog {
  time: string;
  accessID: string;
  printPage: string;
  accessIP: string;
}

interface DownloadLog {
  time: string;
  accessID: string;
  fileName: string;
  accessIP: string;
}

// Column 정의
const loginHistory: (ColDef | ColGroupDef)[] = [
  {
    headerName: "일시",
    field: "time",
  },
  {
    headerName: "접근 ID",
    field: "accessID",
  },
  {
    headerName: "접근 IP",
    field: "accessIP",
  },
];

const pageHistory: (ColDef | ColGroupDef)[] = [
  {
    headerName: "일시",
    field: "time",
  },
  {
    headerName: "접근 ID",
    field: "accessID",
  },
  {
    headerName: "접근 페이지",
    field: "accessPage",
  },
  {
    headerName: "접근 IP",
    field: "accessIP",
  },
];

const printHistory: (ColDef | ColGroupDef)[] = [
  {
    headerName: "일시",
    field: "time",
  },
  {
    headerName: "접근 ID",
    field: "accessID",
  },
  {
    headerName: "출력 페이지",
    field: "printPage",
  },
  {
    headerName: "접근 IP",
    field: "accessIP",
  },
];

const downloadHistory: (ColDef | ColGroupDef)[] = [
  {
    headerName: "일시",
    field: "time",
  },
  {
    headerName: "접근 ID",
    field: "accessID",
  },
  {
    headerName: "다운로드 파일",
    field: "fileName",
  },
  {
    headerName: "접근 IP",
    field: "accessIP",
  },
];

function History() {
  // 상태 관리 (각 로그 데이터를 관리)
  const [loginData, setLoginData] = useState<LoginLog[]>([]);
  const [pageData, setPageData] = useState<PageAccessLog[]>([]);
  const [printData, setPrintData] = useState<PrintLog[]>([]);
  const [downloadData, setDownloadData] = useState<DownloadLog[]>([]);

  const handlePageLoad = async () => {
    try {
      const response = await apiClient.get(LOG_API);
      setLoginData(response.data.loginLogs);
      setDownloadData(response.data.downloadLogs);
      setPageData(response.data.pageAccessLogs);
      setPrintData(response.data.printLogs);
    } catch (error) {
      console.log(error);
    }
  };

  const handleDownloadExcel = async (location: string) => {
    try {
      const response = await apiClient.get(location, {
        responseType: "blob", // 파일을 Blob 형태로 받아옴
      });

      // 헤더에서 파일 이름을 추출 (소문자로 변경)
      const contentDisposition = response.headers["content-disposition"];
      console.log(response.headers);
      const fileName = contentDisposition
        ? contentDisposition.split("filename=")[1].replace(/['"]/g, "")
        : "default.xlsx";

      // 파일을 Blob URL로 변환
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;

      // 백엔드에서 전달된 파일 이름 사용
      link.setAttribute("download", fileName);

      // 링크를 클릭하여 다운로드 시작
      document.body.appendChild(link);
      link.click();

      // 메모리 누수 방지
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("파일 다운로드 중 에러 발생:", error);
    }
  };

  // 데이터 fetching (추가적으로 실제 API 경로를 설정해야 함)
  useEffect(() => {
    handlePageLoad();
  }, []);

  return (
    <Container>
      {/* 로그인 기록 */}
      <div className="d-flex justify-content-between align-items-center">
        <h3 className="mt-4">로그인 기록</h3>
        <img
          src={excelIamge}
          alt="엑셀 다운로드"
          style={{ cursor: "pointer", width: "32px", height: "32px" }}
          onClick={() => handleDownloadExcel(LOG_LOGIN_DOWNLOAD_API)}
        />
      </div>
      <TableWrapper>
        <div className="ag-theme-quartz mt-3">
          <AgGridReact
            rowData={loginData}
            columnDefs={loginHistory}
            defaultColDef={{
              sortable: true,
              resizable: false,
              editable: false,
              flex: 1,
            }}
            domLayout="autoHeight"
            pagination={true}
            paginationPageSize={5}
            paginationPageSizeSelector={[5, 10, 20, 50, 100]}
          />
        </div>
      </TableWrapper>

      {/* 페이지 접근 기록 */}
      <div className="d-flex justify-content-between align-items-center mt-5">
        <h3>페이지 접근 기록</h3>
        <img
          src={excelIamge}
          alt="엑셀 다운로드"
          style={{ cursor: "pointer", width: "32px", height: "32px" }}
          onClick={() => handleDownloadExcel(LOG_ACCESS_DOWNLOAD_API)}
        />
      </div>
      <TableWrapper>
        <div className="ag-theme-quartz mt-3">
          <AgGridReact
            rowData={pageData}
            columnDefs={pageHistory}
            defaultColDef={{
              sortable: true,
              resizable: false,
              editable: false,
              flex: 1,
            }}
            domLayout="autoHeight"
            pagination={true}
            paginationPageSize={5}
            paginationPageSizeSelector={[5, 10, 20, 50, 100]}
          />
        </div>
      </TableWrapper>

      {/* 출력 기록 */}
      <div className="d-flex justify-content-between align-items-center mt-5">
        <h3>출력 기록</h3>
        <img
          src={excelIamge}
          alt="엑셀 다운로드"
          style={{ cursor: "pointer", width: "32px", height: "32px" }}
          onClick={() => handleDownloadExcel(LOG_PRINT_DOWNLOAD_API)}
        />
      </div>
      <TableWrapper>
        <div className="ag-theme-quartz mt-3">
          <AgGridReact
            rowData={printData}
            columnDefs={printHistory}
            defaultColDef={{
              sortable: true,
              resizable: false,
              editable: false,
              flex: 1,
            }}
            domLayout="autoHeight"
            pagination={true}
            paginationPageSize={5}
            paginationPageSizeSelector={[5, 10, 20, 50, 100]}
          />
        </div>
      </TableWrapper>

      {/* 다운로드 기록 */}
      <div className="d-flex justify-content-between align-items-center mt-5">
        <h3>다운로드 기록</h3>
        <img
          src={excelIamge}
          alt="엑셀 다운로드"
          style={{ cursor: "pointer", width: "32px", height: "32px" }}
          onClick={() => handleDownloadExcel(LOG_DOWNLOAD_DOWNLOAD_API)}
        />
      </div>
      <TableWrapper>
        <div className="ag-theme-quartz mt-3">
          <AgGridReact
            rowData={downloadData}
            columnDefs={downloadHistory}
            defaultColDef={{
              sortable: true,
              resizable: false,
              editable: false,
              flex: 1,
            }}
            domLayout="autoHeight"
            pagination={true}
            paginationPageSize={5}
            paginationPageSizeSelector={[5, 10, 20, 50, 100]}
          />
        </div>
      </TableWrapper>
    </Container>
  );
}

export default History;
