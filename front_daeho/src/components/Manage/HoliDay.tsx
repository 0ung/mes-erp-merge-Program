import { useState, useEffect } from "react";
import { Container, FormControl, FormLabel } from "react-bootstrap";
import { AgGridReact } from "ag-grid-react";
import { ColDef, ColGroupDef } from "ag-grid-community";
import apiClient from "../../apiClient";
import TableWrapper from "../tools/TableWrapper";
import { HOLIDAY_API } from "../../constants/API";
import CustomModal from "../CustomModal";
import CustomButton from "../tools/CustomButton";

// Holiday 데이터 타입 정의
interface Holiday {
  id?: number;
  holidayDate: string;
  holidayName: string;
}

// Column 정의
const holidayColumns: (ColDef | ColGroupDef)[] = [
  { headerName: "날짜", field: "holidayDate" },
  { headerName: "설명", field: "holidayName" },
  {
    headerName: "수정",
    cellRenderer: (params: any) => (
      <CustomButton onClick={() => params.context.openEditModal(params.data)}>
        수정
      </CustomButton>
    ),
    width: 100,
  },
  {
    headerName: "삭제",
    cellRenderer: (params: any) => (
      <CustomButton
        onClick={() => params.context.handleDeleteHoliday(params.data.id)}
      >
        삭제
      </CustomButton>
    ),
    width: 100,
  },
];

function HoliDay() {
  const [holidayData, setHolidayData] = useState<Holiday[]>([]); // Holiday 데이터 상태
  const [showModal, setShowModal] = useState<boolean>(false); // 모달 상태
  const [currentHoliday, setCurrentHoliday] = useState<Holiday>({
    id: 0,
    holidayDate: "",
    holidayName: "",
  }); // 현재 선택된 데이터
  const [isEdit, setIsEdit] = useState<boolean>(false); // 수정 상태 관리

  // 페이지가 로드될 때 데이터 fetching
  useEffect(() => {
    handlePageLoad();
  }, []);

  // API 호출 및 데이터 세팅
  const handlePageLoad = async () => {
    try {
      const response = await apiClient.get(HOLIDAY_API);
      setHolidayData(response.data);
    } catch (error) {
      console.log("데이터 로딩 중 에러 발생:", error);
    }
  };

  // 휴일 데이터 추가 및 수정
  const handleCreateOrUpdateHoliday = async () => {
    try {
      let response;
      if (isEdit) {
        // 수정일 경우
        response = await apiClient.put(HOLIDAY_API, currentHoliday);
      } else {
        // 새 데이터 등록
        console.log(currentHoliday);
        response = await apiClient.post(HOLIDAY_API, currentHoliday);
      }

      if (response.status === 200 || response.status === 201) {
        alert("작업이 완료되었습니다.");
        handlePageLoad(); // 새 데이터 로드
        setShowModal(false); // 모달 닫기
      }
    } catch (error) {
      console.log("휴일 저장 중 에러 발생:", error);
    }
  };

  // 휴일 데이터 삭제
  const handleDeleteHoliday = async (id: number) => {
    if (window.confirm("해당 휴일을 삭제하시겠습니까?")) {
      try {
        await apiClient.delete(`${HOLIDAY_API}/${id}`);
        alert("휴일이 삭제되었습니다.");
        handlePageLoad();
      } catch (error) {
        console.log("휴일 삭제 중 에러 발생:", error);
      }
    }
  };

  const validationHoliday = () => {
    if (
      currentHoliday.holidayDate === "" ||
      currentHoliday.holidayName === ""
    ) {
      return false;
    }
    return true;
  };

  // 수정 모달 열기
  const openEditModal = (holiday: Holiday) => {
    setCurrentHoliday(holiday);
    setIsEdit(true); // 수정 상태로 전환
    setShowModal(true);
  };

  // 새 휴일 등록 모달 열기
  const openCreateModal = () => {
    setCurrentHoliday({ holidayDate: "", holidayName: "" });
    setIsEdit(false); // 신규 등록 상태로 전환
    setShowModal(true);
  };

  return (
    <Container>
      {/* 휴일 데이터 표시 */}
      <div className="d-flex justify-content-between align-items-center">
        <h3 className="mt-4">휴일 데이터</h3>
        <CustomButton onClick={openCreateModal}>휴일 등록</CustomButton>
      </div>

      <TableWrapper>
        <div className="ag-theme-quartz mt-3" style={{ height: "400px" }}>
          <AgGridReact
            rowData={holidayData} // 현재 페이지 데이터만 로드
            columnDefs={holidayColumns} // 컬럼 정의
            defaultColDef={{
              sortable: true,
              resizable: false,
              editable: false,
              flex: 1,
            }}
            context={{ openEditModal, handleDeleteHoliday }} // 컨텍스트에 함수 전달
            domLayout="autoHeight"
            pagination={true}
            paginationPageSize={5} // 기본 페이지 크기 설정
            paginationPageSizeSelector={[5, 10, 20, 50, 100]} // 페이지 크기 선택
          />
        </div>
      </TableWrapper>
      {/* 등록/수정 모달 */}
      {showModal && (
        <CustomModal
          show={showModal}
          handleClose={() => setShowModal(false)}
          handleSave={handleCreateOrUpdateHoliday}
          title={isEdit ? "휴일 수정" : "휴일 등록"}
          activeModal={validationHoliday}
        >
          {/* 수정 모드일 때만 ID 표시 */}
          {isEdit && (
            <>
              <FormLabel className="fw-bold">ID</FormLabel>
              <FormControl
                type="text"
                className="p-2"
                value={currentHoliday.id?.toString() || ""} // id가 undefined일 수 있으므로 안전하게 처리
                readOnly // 수정 모드에서는 ID를 변경하지 못하도록 설정
              />
            </>
          )}

          <FormLabel className="fw-bold mt-3">날짜</FormLabel>
          <FormControl
            type="date"
            className="p-2"
            value={currentHoliday.holidayDate}
            onChange={(event) =>
              setCurrentHoliday({
                ...currentHoliday,
                holidayDate: event.target.value,
              })
            }
          />

          <FormLabel className="fw-bold mt-3">휴일 설명</FormLabel>
          <FormControl
            type="text"
            placeholder="설명을 입력하세요"
            className="p-2"
            value={currentHoliday.holidayName}
            onChange={(event) =>
              setCurrentHoliday({
                ...currentHoliday,
                holidayName: event.target.value,
              })
            }
          />
        </CustomModal>
      )}
    </Container>
  );
}

export default HoliDay;
