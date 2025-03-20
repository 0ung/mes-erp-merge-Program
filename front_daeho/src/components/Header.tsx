import { FormControl, FormLabel, Nav, Navbar } from "react-bootstrap";
import { useLoginContext } from "../context/LoginProvider";
import { useEffect, useState } from "react";
import CustomModal from "./CustomModal";
import apiClient from "../apiClient";
import { LOGOUT_API, UPDATE_PASSWORD_API } from "../constants/API";
import { useNavigate } from "react-router-dom";
import { useNameContext } from "../context/nameProvider";
import daehoCI from "../asset/daehoCI.png";
const Header = () => {
  const { auth } = useLoginContext();
  const { userName, rank } = useNameContext();
  const [isLogin, setIsLogin] = useState<boolean>(false);
  const [showModal, setShowModal] = useState<boolean>(false); // 모달 열림/닫힘 상태
  const [password, setPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");
  const [isUpdate, setIsUpdate] = useState<boolean>(false);
  const navigator = useNavigate();

  useEffect(() => {
    if (auth) {
      setIsLogin(true);
    } else {
      setIsLogin(false);
      alert("로그인되지 않은 유저입니다. \n로그인 부탁드립니다.");
      navigator("/login");
    }
  }, [auth, userName, navigator]);

  const handleLogout = async () => {
    try {
      const response = await apiClient.get(LOGOUT_API);
      console.log(response.status);
      if (response.status === 200) {
        localStorage.clear();
        navigator("/login");
      } else {
        alert("로그아웃 실패");
      }
    } catch (error) {}
  };

  const openModal = () => {
    setShowModal(true); // 모달 열기
  };
  const handlePassword = async () => {
    const data = {
      passwd: password,
    };
    try {
      const response = await apiClient.patch(
        UPDATE_PASSWORD_API,
        JSON.stringify(data),
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      alert("비밀번호 수정 완료");
      setShowModal(false);
      console.log(response);
    } catch (error) {
      console.error("Error updating password:", error);
    }
  };

  const handleCloseModal = () => {
    setShowModal(false); // 모달 닫기
  };

  useEffect(() => {
    if (password === "" || confirmPassword == "") {
      setIsUpdate(false);
      return;
    }
    setIsUpdate(confirmPassword === password);
  }, [confirmPassword, password]);

  return (
    <Navbar
      expand="lg"
      className="shadow-sm w-100"
      style={{ backgroundColor: "#d3d3d3" }}
    >
      <Navbar.Brand
        href="/"
        className="fw-bold text-muted ms-5"
        style={{ padding: 0 }}
      >
        <img
          src={daehoCI}
          alt="대호아이앤티"
          style={{ height: "40px", width: "auto" }} // 원하는 크기 설정
        />
      </Navbar.Brand>

      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav className="ms-auto align-items-center">
          {isLogin ? (
            <>
              <span className="text-muted me-3">{`${rank} ${userName}님 반갑습니다.`}</span>
              <Nav.Link onClick={handleLogout} className="text-muted me-3">
                로그아웃
              </Nav.Link>
              <Nav.Link onClick={openModal} className="text-muted me-5">
                개인정보 수정
              </Nav.Link>
            </>
          ) : (
            <>
              <Nav.Link href="/login" className="text-muted">
                Login
              </Nav.Link>
            </>
          )}
        </Nav>
      </Navbar.Collapse>
      {showModal && (
        <CustomModal
          show={showModal}
          handleClose={handleCloseModal}
          handleSave={handlePassword}
          title="개인 정보수정"
          activeModal={isUpdate}
        >
          <FormLabel className="fw-bold">비밀번호</FormLabel>
          <FormControl
            type="password"
            placeholder="변경할 비밀번호 입력"
            className="p-2"
            onChange={(e) => {
              setPassword(e.target.value);
            }}
          />
          <FormLabel className="fw-bold mt-3">비밀번호 재입력</FormLabel>
          <FormControl
            type="password"
            placeholder="비밀번호 확인"
            className="p-2"
            onChange={(e) => {
              setConfirmPassword(e.target.value);
            }}
          />
        </CustomModal>
      )}
    </Navbar>
  );
};

export default Header;
