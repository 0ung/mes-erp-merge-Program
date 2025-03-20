import { useEffect, useState } from "react";
import { Container, FormControl, FormLabel, FormSelect } from "react-bootstrap";
import CustomButton from "../tools/CustomButton";
import CustomModal from "../CustomModal";
import apiClient from "../../apiClient";
import {
  MANAGE_API,
  MANAGE_MEMBER_API,
  MANAGE_UPDATE_PASSWORD_API,
} from "../../constants/API";
import TableWrapper from "../tools/TableWrapper";
import { AgGridReact } from "ag-grid-react";
import { userColumns } from "../tools/tableDef";

// 데이터 타입 정의
interface userDataType {
  name: string;
  id: string;
  rank: string;
  auth: string;
}

interface userCreateType {
  name: string;
  id: string;
  password?: string;
  rank: string;
  auth: string;
}

function MasterData() {
  const [masterDatas, setMasterDatas] = useState<userDataType[]>([]);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [userData, setUserData] = useState<userCreateType>({
    name: "",
    id: "",
    password: "",
    rank: "",
    auth: "",
  });
  const [selectedUser, setSelectedUser] = useState<userDataType | null>(null); // 선택된 사용자 정보

  // 회원 목록 가져오기
  const handlePage = async () => {
    try {
      const response = await apiClient.get(MANAGE_MEMBER_API);
      setMasterDatas(response.data);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    handlePage();
  }, []);

  // 수정 모달 열기
  const openEditModal = (user: userDataType) => {
    setSelectedUser(user); // 선택된 유저 설정
    setUserData({
      name: user.name,
      id: user.id,
      password: "", // 비밀번호는 수정하지 않도록 초기화
      rank: user.rank,
      auth: user.auth,
    });
    setShowModal(true);
  };

  // 모달 닫기
  const closeModal = () => {
    setShowModal(false);
    setUserData({
      name: "",
      id: "",
      password: "",
      rank: "",
      auth: "",
    });
    setSelectedUser(null);
  };

  // 유저 등록
  const handleCreateUser = async () => {
    try {
      const response = await apiClient.post(MANAGE_API, userData);
      if (response.status === 201) {
        alert("생성이 완료되었습니다.");
        handlePage(); // 업데이트된 데이터 불러오기
        closeModal();
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 유저 수정
  const handleUpdateUser = async () => {
    try {
      const response = await apiClient.put(`${MANAGE_MEMBER_API}`, userData);
      if (response.status === 200) {
        alert("수정이 완료되었습니다.");
        handlePage(); // 업데이트된 데이터 불러오기
        closeModal();
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 비밀번호 초기화
  const handlePassword = async (user: userDataType) => {
    try {
      const response = await apiClient.put(
        `${MANAGE_UPDATE_PASSWORD_API}/${user.id}`
      );

      if (response.status === 200) {
        alert(`${user.id}의 비밀번호가 초기화 되었습니다.`);
      }
    } catch (error) {
      alert(`${user.id}의 비밀번호 초기화에 실패했습니다.`);
    }
  };

  return (
    <Container>
      <h3 className="mt-4">회원 관리</h3>

      <div className="d-flex justify-content-end mt-3">
        <CustomButton
          onClick={() => {
            setUserData({
              name: "",
              id: "",
              password: "",
              rank: "",
              auth: "",
            }); // 유저 등록 시 상태 초기화
            setSelectedUser(null);
            setShowModal(true);
          }}
        >
          회원 등록
        </CustomButton>
      </div>

      <TableWrapper>
        <div className="ag-theme-quartz mt-3" style={{ height: "400px" }}>
          <AgGridReact
            rowData={masterDatas} // 사용자 데이터
            columnDefs={userColumns} // 컬럼 정의
            defaultColDef={{
              sortable: true,
              resizable: true,
              editable: false,
              flex: 1,
            }}
            context={{ openEditModal, handlePassword }} // 컨텍스트에 함수 전달
            domLayout="autoHeight"
            pagination={true}
            paginationPageSize={100} // 기본 페이지 크기 설정
          />
        </div>
      </TableWrapper>

      {/* 수정/등록 모달 */}
      {showModal && (
        <CustomModal
          show={showModal}
          handleClose={closeModal}
          handleSave={selectedUser ? handleUpdateUser : handleCreateUser}
          title={selectedUser ? "회원 수정" : "신규 회원 등록"}
          activeModal={true}
        >
          <FormLabel className="fw-bold">아이디</FormLabel>
          <FormControl
            type="text"
            placeholder="아이디를 입력하세요"
            className="p-2"
            value={userData.id}
            readOnly={!!selectedUser} // 수정 시 아이디는 읽기 전용
            onChange={(event) =>
              setUserData({ ...userData, id: event.target.value })
            }
          />

          <FormLabel className="fw-bold mt-3">이름</FormLabel>
          <FormControl
            type="text"
            placeholder="이름을 입력하세요"
            className="p-2"
            value={userData.name}
            onChange={(event) =>
              setUserData({ ...userData, name: event.target.value })
            }
          />

          {!selectedUser && (
            <>
              <FormLabel className="fw-bold mt-3">비밀번호</FormLabel>
              <FormControl
                type="password"
                placeholder="비밀번호를 입력하세요"
                className="p-2"
                value={userData.password}
                onChange={(event) =>
                  setUserData({ ...userData, password: event.target.value })
                }
              />
            </>
          )}

          <FormLabel className="fw-bold mt-3">직급</FormLabel>
          <FormSelect
            className="p-2"
            value={userData.rank}
            onChange={(event) =>
              setUserData({ ...userData, rank: event.target.value })
            }
          >
            <option value="">직급 선택</option>
            <option value="대표이사">대표이사</option>
            <option value="전무">전무</option>
            <option value="이사">이사</option>
            <option value="실장">실장</option>
            <option value="팀장">팀장</option>
            <option value="프로">프로</option>
            <option value="프로(PL)">프로(PL)</option>
            <option value="사원">사원</option>
            <option value="수석연구원">수석연구원</option>
            <option value="책임연구원">책임연구원</option>
            <option value="선임연구원">선임연구원</option>
            <option value="연구원">연구원</option>
          </FormSelect>

          <FormLabel className="fw-bold mt-3">권한</FormLabel>
          <FormSelect
            className="p-2"
            value={userData.auth}
            onChange={(event) =>
              setUserData({ ...userData, auth: event.target.value })
            }
          >
            <option value="">권한 선택</option>
            <option value="A">A 권한</option>
            <option value="B">B 권한</option>
            <option value="C">C 권한</option>
            <option value="관리자">관리자</option>
          </FormSelect>
        </CustomModal>
      )}
    </Container>
  );
}

export default MasterData;
