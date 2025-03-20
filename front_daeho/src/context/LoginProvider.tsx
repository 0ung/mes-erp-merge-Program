// LoginProvider.tsx
import {
  createContext,
  FC,
  ReactNode,
  useContext,
  useState,
  useEffect,
} from "react";

interface LoginContextType {
  auth: string | null;
  setLoginState?: React.Dispatch<React.SetStateAction<LoginContextType>>; // 상태 업데이트 함수
}

const defaultContext: LoginContextType = {
  auth: localStorage.getItem("auth"), // 로컬 스토리지에서 가져온 정보로 초기화
  setLoginState: () => {},
};

const LoginContextState = createContext<LoginContextType>(defaultContext);

const LoginProvider: FC<{ children: ReactNode }> = ({ children }) => {
  const [auth, setAuth] = useState<string | null>(localStorage.getItem("auth"));
  const [loginState, setLoginState] = useState<LoginContextType>({ auth });

  useEffect(() => {
    const handleStorageChange = () => {
      const authValue = localStorage.getItem("auth");
      setAuth(authValue);
      setLoginState({ auth: authValue });
    };

    // 스토리지 변경 이벤트 구독
    window.addEventListener("storage", handleStorageChange);

    // 클린업 함수
    return () => {
      window.removeEventListener("storage", handleStorageChange);
    };
  }, []);

  return (
    <LoginContextState.Provider value={{ ...loginState, setLoginState }}>
      {children}
    </LoginContextState.Provider>
  );
};

export const useLoginContext = () => useContext(LoginContextState); // LoginContext 사용을 위한 커스텀 훅

export default LoginProvider;
