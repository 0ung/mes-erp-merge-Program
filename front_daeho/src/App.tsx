import { Outlet, Route, Routes } from "react-router-dom";
import MainPage from "./pages/MainPage";
import ProductionDailyReport from "./pages/ProductionDailyReport";
import SMAssy from "./components/ProcessReport/SMAssy";
import Header from "./components/Header";
import "bootstrap/dist/css/bootstrap.min.css";
import Login from "./pages/Login";
import AppSidebar from "./components/SiderBar";
import styled from "styled-components";
import IMAssy from "./components/ProcessReport/IMAssy";
import CASEAssy from "./components/ProcessReport/CASEAssy";
import ACCY from "./components/ProcessReport/ACCY";
import PackingAssy from "./components/ProcessReport/PackingAssy";
import DIPAssy from "./components/ProcessReport/DIPAssy";
import PCBAssy from "./components/ProcessReport/PCBAssy";
import ManualAssy from "./components/ProcessReport/ManualAssy";
import MasterData from "./components/Manage/MasterData";
import LossOccurrence from "./pages/LossOccurrence";
import User from "./components/Manage/User";
import History from "./components/Manage/LoginHistory";
import PerformanceAndProductionFlow from "./pages/PerformanceAndProductionFlow";
import PerformanceByProcess from "./pages/PerformanceByProcess";
import HoliDay from "./components/Manage/HoliDay";
import ProcessStock from "./pages/ProcessStock";
import LTUnusedMaterials from "./pages/LTUnusedMaterials";
import PurchaseAndReceipt from "./pages/PurchaseAndReceipt";
import DailySearch from "./pages/DailySearch";

// 그리드 레이아웃 설정
const LayoutWrapper = styled.div`
  display: grid;
  grid-template-columns: 10% 90%;
  height: calc(100vh - 60px); /* 헤더 높이만큼 빼기 */
`;

// 메인 콘텐츠 스타일 정의
const MainContentWrapper = styled.div`
  padding: 20px;
  hegith: 100%;
`;

const Layout = () => {
  return (
    <>
      <Header />
      <LayoutWrapper>
        <AppSidebar />
        <MainContentWrapper>
          <Outlet />
        </MainContentWrapper>
      </LayoutWrapper>
    </>
  );
};

function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route path="/search/main" element={<DailySearch />}></Route>
        <Route index element={<MainPage />}></Route>
        <Route
          path="productionDailyReport"
          element={<ProductionDailyReport />}
        />
        <Route path="ProcessProductionDailyReport">
          <Route path="sm" element={<SMAssy />} />
          <Route path="im" element={<IMAssy />} />
          <Route path="case" element={<CASEAssy />} />
          <Route path="accy" element={<ACCY />} />
          <Route path="packing" element={<PackingAssy />} />
          <Route path="dip" element={<DIPAssy />} />
          <Route path="pcb" element={<PCBAssy />} />
          <Route path="manual" element={<ManualAssy />} />
        </Route>
        <Route path="loss" element={<LossOccurrence />} />
        <Route path="performance" element={<PerformanceByProcess />} />
        <Route
          path="productionFlow"
          element={<PerformanceAndProductionFlow />}
        />
        <Route path="ltIssue" element={<LTUnusedMaterials />} />
        <Route path="purchase" element={<PurchaseAndReceipt />} />
        <Route path="processStock" element={<ProcessStock />} />
        <Route path="manage">
          <Route path="user" element={<User />} />
          <Route path="history" element={<History />} />
          <Route path="masterData" element={<MasterData />} />
          <Route path="holiday" element={<HoliDay />} />
        </Route>
      </Route>
      <Route path="/login" element={<Login />}></Route>
    </Routes>
  );
}

export default App;
