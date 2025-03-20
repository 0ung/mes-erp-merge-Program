import { useState } from "react";
import { Container, Form, Alert } from "react-bootstrap";
import CustomButton from "../components/tools/CustomButton"; // 커스텀 버튼 컴포넌트 가져오기
import apiClient from "../apiClient";
import { LOGIN_API } from "../constants/API";
import { useNavigate } from "react-router-dom";
import { useLoginContext } from "../context/LoginProvider";
import loginBackground from "../asset/loginBackground.png";

function Login() {
  const navigate = useNavigate();

  const [userId, setUserId] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { setLoginState } = useLoginContext(); // LoginContext의 상태 업데이트 함수 가져오기

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault(); // 기본 폼 제출 동작 방지

    if (userId === "" || password === "") {
      setError("아이디 및 비밀번호를 확인해주세요");
      return;
    }

    const data = {
      id: userId,
      password: password,
    };

    try {
      const response = await apiClient.post(LOGIN_API, data);

      if (response.status === 400) {
        setError("아이디 및 비밀번호를 확인해주세요");
      } else if (response.status === 200) {
        const accessToken = response.data.accessToken;
        const auth = response.data.authority;

        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("auth", auth);

        if (setLoginState) {
          setLoginState({
            auth: auth,
          });
        } else {
          console.error(
            "JWT 페이로드를 파싱할 수 없거나 상태를 업데이트할 수 없습니다."
          );
        }

        navigate("/"); // 데이터를 다 처리한 후에 navigate 호출
      }
    } catch (error) {
      console.log(error);
      setError("아이디 및 비밀번호를 확인해주세요.");
    }
  };

  return (
    <div
      style={{
        backgroundImage: `url(${loginBackground})`,
        backgroundSize: "cover", // 이미지가 전체 화면에 맞게 채워짐
        backgroundPosition: "center", // 이미지가 중앙에 위치하도록 설정
        backgroundRepeat: "no-repeat", // 이미지 반복을 막음
      }}
    >
      <Container className="d-flex justify-content-center align-items-center vh-100">
        <div
          className="p-4 rounded shadow"
          style={{
            maxWidth: "400px",
            width: "100%",
            background: "rgba(255, 255, 255, 1.0)", // 백그라운드 반투명 설정
          }}
        >
          <h1 className="mb-4 text-center">대호아이앤티</h1>
          {error && <Alert variant="danger">{error}</Alert>}
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>ID 입력</Form.Label>
              <Form.Control
                type="email"
                placeholder="ID"
                value={userId}
                onChange={(e) => setUserId(e.target.value)}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>비밀번호</Form.Label>
              <Form.Control
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </Form.Group>
            <CustomButton classNames="w-100" onClick={handleSubmit}>
              로그인
            </CustomButton>
          </Form>
        </div>
      </Container>
    </div>
  );
}

export default Login;
