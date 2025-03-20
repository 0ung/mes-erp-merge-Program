import { Sidebar, Menu, MenuItem, SubMenu } from "react-pro-sidebar";
import styled from "styled-components";
import { useLoginContext } from "../context/LoginProvider";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const StyledSidebar = styled(Sidebar)`
  transition-duration: 300ms; /* 애니메이션 유지 */
  box-shadow: 2px 0px 5px rgba(0, 0, 0, 0.1); /* 약간의 그림자 추가 */
`;

const StyledMenuItem = styled(MenuItem)`
  font-size: 15px;
  padding: 2px 2px;
  margin: 0px 0px;
`;

const StyledSubMenu = styled(SubMenu)`
  font-size: 15px;
  padding: 4px 4px;

  & > .pro-inner-list-item {
    padding: 2px 2px;
  }
`;

const AppSidebar = () => {
  const { auth } = useLoginContext();
  const [isAdmin, setIsAdmin] = useState<boolean>(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (auth === "관리자") {
      setIsAdmin(true);
    }
  }, [auth]);

  return (
    <StyledSidebar transitionDuration={300} width="400">
      <Menu>
        <StyledMenuItem onClick={() => navigate("/")}>Home</StyledMenuItem>
        {auth === "A" || auth === "B" || auth === "관리자" ? (
          <>
            <StyledMenuItem onClick={() => navigate("productionDailyReport")}>
              일일생산일보
            </StyledMenuItem>
            <StyledSubMenu label="공정생산일보">
              <StyledMenuItem
                onClick={() => navigate("/ProcessProductionDailyReport/sm")}
              >
                SM ASSY
              </StyledMenuItem>
              <StyledMenuItem
                onClick={() => navigate("/ProcessProductionDailyReport/im")}
              >
                IM ASSY
              </StyledMenuItem>
              <StyledMenuItem
                onClick={() => navigate("/ProcessProductionDailyReport/dip")}
              >
                DIP ASSY
              </StyledMenuItem>
              <StyledMenuItem
                onClick={() => navigate("/ProcessProductionDailyReport/manual")}
              >
                MANUAL ASSY
              </StyledMenuItem>
              <StyledMenuItem
                onClick={() => navigate("/ProcessProductionDailyReport/pcb")}
              >
                PCB ASSY
              </StyledMenuItem>
              <StyledMenuItem
                onClick={() => navigate("/ProcessProductionDailyReport/case")}
              >
                CASE ASSY
              </StyledMenuItem>
              <StyledMenuItem
                onClick={() =>
                  navigate("/ProcessProductionDailyReport/packing")
                }
              >
                PACKING ASSY
              </StyledMenuItem>
              <StyledMenuItem
                onClick={() => navigate("/ProcessProductionDailyReport/accy")}
              >
                ACCY
              </StyledMenuItem>
            </StyledSubMenu>
            <StyledSubMenu label="현황">
              <StyledMenuItem onClick={() => navigate("/loss")}>
                LOSS 발생
              </StyledMenuItem>
              <StyledMenuItem onClick={() => navigate("/performance")}>
                공정별 실적
              </StyledMenuItem>
              <StyledMenuItem onClick={() => navigate("/productionFlow")}>
                실적 및 생산 흐름
              </StyledMenuItem>
              <StyledMenuItem onClick={() => navigate("/processStock")}>
                공정별 재고
              </StyledMenuItem>
              <StyledMenuItem onClick={() => navigate("/purchase")}>
                구매자재 발주 및 입고
              </StyledMenuItem>
              <StyledMenuItem onClick={() => navigate("/ltIssue")}>
                장기불용 자재 ISSUE
              </StyledMenuItem>
            </StyledSubMenu>
          </>
        ) : (
          <>
            <StyledMenuItem onClick={() => navigate("/loss")}>
              LOSS 발생
            </StyledMenuItem>
            <StyledMenuItem onClick={() => navigate("/performance")}>
              공정별 실적
            </StyledMenuItem>
          </>
        )}

        {isAdmin && (
          <StyledSubMenu label="관리자">
            <StyledMenuItem onClick={() => navigate("/manage/user")}>
              회원관리
            </StyledMenuItem>
            <StyledMenuItem onClick={() => navigate("/manage/history")}>
              기록관리
            </StyledMenuItem>
            <StyledMenuItem onClick={() => navigate("/manage/masterData")}>
              기준정보관리
            </StyledMenuItem>
            <StyledMenuItem onClick={() => navigate("/manage/holiday")}>
              휴일관리
            </StyledMenuItem>
          </StyledSubMenu>
        )}
      </Menu>
    </StyledSidebar>
  );
};

export default AppSidebar;
