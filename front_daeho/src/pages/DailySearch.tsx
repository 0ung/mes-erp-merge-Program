import { useLocation, useNavigate } from "react-router-dom";
import { AgGridReact } from "ag-grid-react";
import TableWrapper from "../components/tools/TableWrapper";
import { Button, Spinner } from "react-bootstrap";
import { useState, useEffect } from "react";
import apiClient from "../apiClient"; // 서버 요청을 위한 클라이언트
import { CellClickedEvent } from "ag-grid-community";

function DailySearch() {
  const location = useLocation();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true); // 로딩 상태 추가
  const searchResult = location.state?.searchResult || []; // 전달된 검색 결과
  const url = location.state?.url;
  const redirectUrl = location.state?.redirect;

  // 데이터를 로드할 때 로딩 상태 업데이트
  useEffect(() => {
    setLoading(true); // 로딩 상태를 true로 설정

    const timer = setTimeout(() => {
      if (searchResult.length === 0) {
        setLoading(false); // 5초 후에도 데이터가 없으면 로딩 상태 종료
      }
    }, 5000); // 5초 대기

    if (searchResult.length > 0) {
      setLoading(false); // 데이터 로드 완료 시 로딩 상태 false
      clearTimeout(timer); // 타이머 제거
    }

    return () => clearTimeout(timer); // 컴포넌트 언마운트 시 타이머 제거
  }, [searchResult]);

  const handleCellClick = async (event: CellClickedEvent) => {
    const clickedId = event.data.id; // 클릭된 셀의 ID 가져오기

    try {
      const response = await apiClient.get(`${url}/${clickedId}`);
      console.log(response.data, "검색결과");
      navigate(`${redirectUrl}`, { state: { details: response.data } });
    } catch (error) {
      console.error("Error occurred while fetching details:", error);
    }
  };

  return (
    <>
      <h1 className="mb-4">검색 결과</h1>
      <div className="d-flex justify-content-end align-items-start mb-4">
        <Button onClick={() => navigate(-1)} className="mt-4">
          뒤로 가기
        </Button>
      </div>

      {loading ? (
        // 로딩 중일 때 스피너 또는 로딩 메시지 표시
        <div className="d-flex justify-content-center">
          <Spinner animation="border" variant="primary" />
        </div>
      ) : (
        <>
          <TableWrapper height="600px">
            <AgGridReact
              rowData={searchResult}
              columnDefs={[
                { headerName: "ID", field: "id", sortable: true, width: 100 },
                {
                  headerName: "이름",
                  field: "name",
                  sortable: true,
                  width: 200,
                },
                {
                  headerName: "생성일",
                  field: "createDate",
                  sortable: true,
                  width: 200,
                },
              ]}
              defaultColDef={{
                sortable: true,
                flex: 1,
                resizable: true,
                minWidth: 100,
              }}
              pagination={true}
              paginationPageSize={10}
              domLayout="autoHeight"
              onCellClicked={handleCellClick} // 셀 클릭 이벤트 핸들러
            />
          </TableWrapper>
        </>
      )}
    </>
  );
}

export default DailySearch;
