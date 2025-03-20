// NameProvider.tsx
import React, {
  createContext,
  FC,
  ReactNode,
  useContext,
  useState,
  useEffect,
} from "react";
import { useLoginContext } from "./LoginProvider"; // LoginContext와 연동
import { parseJWT } from "../components/tools/utils";

interface nameContextType {
  userName: string | null;
  rank: string | null;
  setLoginState?: React.Dispatch<React.SetStateAction<string | null>>;
  setRankState?: React.Dispatch<React.SetStateAction<string | null>>;
}

const defaultContext: nameContextType = {
  userName: null,
  rank: null,
  setLoginState: () => {},
};

const NameContextState = createContext<nameContextType>(defaultContext);

const NameProvider: FC<{ children: ReactNode }> = ({ children }) => {
  const { auth } = useLoginContext(); // LoginContext에서 auth 가져옴
  const [userName, setUserName] = useState<string | null>(null);
  const [rank, setRank] = useState<string | null>(null);

  const handleAccessToken = () => {
    if (auth) {
      const jwtPayload = parseJWT(localStorage.getItem("accessToken"));
      if (jwtPayload && jwtPayload.sub) {
        setUserName(jwtPayload.sub); // JWT에서 사용자 이름 추출
        setRank(jwtPayload.rank); // JWT에서 직급 추출
      } else {
        setUserName(null);
        setRank(null);
      }
    }
  };

  useEffect(() => {
    handleAccessToken(); // auth가 변경될 때마다 실행
  }, [auth]);

  return (
    <NameContextState.Provider
      value={{
        userName,
        rank,
        setLoginState: setUserName,
        setRankState: setRank,
      }}
    >
      {children}
    </NameContextState.Provider>
  );
};

export const useNameContext = () => {
  const context = useContext(NameContextState);
  if (context === undefined) {
    throw new Error("useNameContext must be used within a NameProvider");
  }
  return context;
};

export default NameProvider;
