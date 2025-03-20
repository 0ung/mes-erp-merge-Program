import axios from "axios";
import { REFRESH_API } from "./constants/API";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  withCredentials: true,
});

// 요청 인터셉터 설정
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken"); // 예시로 로컬 스토리지에서 토큰 가져오기
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`; // Authorization 헤더에 토큰 추가
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 설정
apiClient.interceptors.response.use(
  (response) => {
    return response; // 응답 성공 처리
  },
  async (error) => {
    const originalRequest = error.config;

    // 401 에러가 발생하고, 토큰 재시도가 아직 없는 경우에만
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // 무한 루프 방지
      try {
        // 토큰 갱신 요청
        const response = await tokenRefresh();
        // 새로운 토큰이 설정된 후 원래 요청을 다시 시도
        if (response && response.data.accessToken) {
          localStorage.setItem("accessToken", response.data.accessToken);
          return apiClient(originalRequest); // 갱신된 토큰으로 원래 요청 재시도
        }
      } catch (tokenRefreshError) {
        console.error("토큰 갱신 실패:", tokenRefreshError);
        window.location.href = "/login"; // 실패하면 로그인 페이지로 이동
      }
    } else if (error.response.status === 403) {
      window.location.href = "/login"; // 403 에러는 로그인 페이지로 리다이렉트
    } else {
      return Promise.reject(error);
    }
  }
);

// 토큰 갱신 함수
const tokenRefresh = async () => {
  try {
    const response = await apiClient.get(API_BASE_URL + REFRESH_API);
    return response;
  } catch (error) {
    console.error("토큰 갱신 오류:", error);
    throw error; // 오류가 발생하면 catch 블록에서 처리
  }
};

export default apiClient;
