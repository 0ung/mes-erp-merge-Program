import { Button, Card, Col, Row, Spinner, Table } from "react-bootstrap";
import ProgressDoughnutChart from "../components/tools/ProgressdoughnutChart";
import MultiBarChart from "../components/tools/MultiBarChart";
import React, { useEffect, useRef, useState, useMemo, Suspense } from "react";
import { getData, getDateTime } from "../components/tools/utils";
import PrintPage from "../components/tools/PrintPage";
import { DateField, TextField } from "../components/tools/Search";
import searchIcon from "../asset/search.svg";
import styled from "styled-components";
import apiClient from "../apiClient";
import { FLOWDATA_API } from "../constants/API";
import { Chart } from "react-chartjs-2";
import { useNameContext } from "../context/nameProvider";

interface flowData {
  lotProgress: string;
  modelName: string;
  specification: string;
  productionRequestNo: string;
  partNumber: string;
  totalProgress: number;
  progressTypeDTO: progressTypeDTO;
  processData: {
    [key: string]: {
      planQty: number;
      inputQty: number;
      completedQty: number;
      inputCost: number;
      completedRate: number;
    };
  };
}

interface progressTypeDTO {
  processProgress: number[];
}

const StyledComponent = styled.div`
  .print-info {
    display: none;
  }

  @media print {
    max-width: 500mm; /* 가로 A4 용지 크기에서 마진을 뺀 너비 */
    font-size: 10px;
    @page {
      size: B4;
    }

    /* 제목 크기 및 마진 조정 */
    h1 {
      font-size: 16px;
      text-align: center;
    }

    h3 {
      font-size: 14px;
    }

    .no-print {
      display: none;
    }

    .print-info {
      display: block;
    }
  }
`;

const StyledTable = styled(Table)`
  font-size: 14px; /* 글자 크기 설정 */
  th,
  td {
    text-align: center; /* 중앙 정렬 */
    vertical-align: middle; /* 수직 중앙 정렬 */
    padding: 10px; /* 패딩 조정 */
  }
`;

// CustomCard를 React.memo로 최적화
const CustomCard = React.memo(({ data }: { data: flowData }) => {
  console.log(data);
  const lotProgress = parseInt(data.lotProgress);
  const assyNames = useMemo(
    () => Object.keys(data.processData),
    [data.processData]
  );

  return (
    <Card className="p-3 mb-4 mt-3 shadow">
      <Row>
        <Col md={4} className="border-right">
          <div style={{ padding: "10px" }}>
            <h5 className="fs-4">
              <b>LOT 진행률:</b>
              {Math.floor(parseFloat(data.lotProgress)).toFixed(0)}%
            </h5>
            <p className="fw-bold text-primary" style={{ fontSize: "18px" }}>
              프로젝트 정보
            </p>
            <p>
              <b>모델명:</b> {data.modelName || "N/A"}
            </p>
            <p>
              <b>규격:</b> {data.specification || "N/A"}
            </p>
            <p>
              <b>생산의뢰번호:</b> {data.productionRequestNo || "N/A"}
            </p>
            <p>
              <b>품번:</b> {data.partNumber || "N/A"}
            </p>
            <div className="d-flex justify-content-center my-3">
              <ProgressDoughnutChart
                width="100px"
                progress={parseInt(data.lotProgress)}
              />
            </div>
            <p className="text-start mt-2">
              <span className="text-primary fw-bold">진행률:</span>
              {lotProgress}%
            </p>
            <p className="text-start">
              <span className="text-secondary fw-bold">남은 진행률:</span>
              {lotProgress > 100 ? 0 : 100 - lotProgress}%
            </p>
            <hr />
            <h5 style={{ textAlign: "center" }}>투입대비 진행률</h5>
            <Row className="justify-content-center mt-3">
              {assyNames.map((assy, idx) => (
                <Col key={idx} className="text-center mb-4">
                  <p>{assy}</p>
                  <div style={{ margin: "auto" }}>
                    <ProgressDoughnutChart
                      width="80px"
                      progress={data.progressTypeDTO.processProgress[idx]}
                    />
                  </div>
                </Col>
              ))}
            </Row>
          </div>
        </Col>
        {/* 우측: 차트 영역 */}
        <Col md={8}>
          <Row className="mb-3">
            {/* 공정 수량 차트 */}
            <Col md={8}>
              <h5 className="text-center">공정 수량</h5>
              <MultiBarChart
                chartData={data.processData}
                width="300px"
                height="200px"
              />
            </Col>

            {/* 금액 현황 차트 */}
            <Col md={4}>
              <h5 className="text-center">금액 현황</h5>
              <Chart
                type="bar"
                data={useMemo(
                  () => ({
                    labels: assyNames,
                    datasets: [
                      {
                        label: "투입금액",
                        data: assyNames.map(
                          (assy) => data.processData[assy]?.inputCost || 0
                        ),
                        backgroundColor: "rgba(54, 162, 235, 0.6)",
                      },
                    ],
                  }),
                  [assyNames, data.processData]
                )}
                options={{
                  responsive: true,
                  plugins: {
                    legend: { position: "top" },
                    title: { display: true, text: "금액 현황 차트" },
                  },
                }}
                width="300px"
                height="450px"
              />
            </Col>
          </Row>
        </Col>
      </Row>

      <hr />

      {/* 테이블 영역 */}
      <Row>
        <StyledTable striped bordered>
          <thead>
            <tr>
              <th></th>
              {assyNames.map((assy, idx) => (
                <th key={idx}>{assy}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className="fw-bold">계획수량</td>
              {assyNames.map((assy, idx) => (
                <td key={idx}>
                  {data.processData[assy]?.planQty.toFixed(0) || 0}
                </td>
              ))}
            </tr>
            <tr>
              <td className="fw-bold">투입수량</td>
              {assyNames.map((assy, idx) => (
                <td key={idx}>
                  {data.processData[assy]?.inputQty.toFixed(0) || 0}
                </td>
              ))}
            </tr>
            <tr>
              <td className="fw-bold">완료수량</td>
              {assyNames.map((assy, idx) => (
                <td key={idx}>
                  {data.processData[assy]?.completedQty.toFixed(0) || 0}
                </td>
              ))}
            </tr>
            <tr>
              <td className="fw-bold">투입금액</td>
              {assyNames.map((assy, idx) => (
                <td key={idx}>
                  {Number(
                    data.processData[assy]?.inputCost.toFixed(0)
                  ).toLocaleString() || 0}
                </td>
              ))}
            </tr>
            <tr>
              <td className="fw-bold">완료율</td>
              {assyNames.map((assy, idx) => (
                <td key={idx}>
                  {data.processData[assy]?.completedRate.toFixed(0) + `%` ||
                    "0%"}
                </td>
              ))}
            </tr>
          </tbody>
        </StyledTable>
      </Row>
    </Card>
  );
});

function PerformanceAndProductionFlow() {
  const [cardData, setCardData] = useState<flowData[]>([]); // flowData 배열
  const [loading, setLoading] = useState(true); // 로딩 상태 추가
  const data = getData();
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [modelName, setModelName] = useState<string>("");
  const [productNumber, setProductNumber] = useState<string>("");
  const { userName } = useNameContext();
  const currentDate = getDateTime();
  const [search, setSearch] = useState<boolean>(false);

  const componentRef = useRef<any>();
  const handlePageData = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get(FLOWDATA_API);
      requestIdleCallback(() => setCardData(response.data)); // idle 타임에 데이터 세팅

      setSearch(false);
    } catch (error) {
      console.log("/report/flow 에러" + error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    handlePageData();
  }, []);

  const handleSearch = () => {
    apiClient
      .get(
        `${FLOWDATA_API}/${startDate}/${endDate}?modelName=${modelName}&partNumber=${productNumber}`
      )
      .then((response) => {
        console.log(response.data);
        setCardData(response.data);
        setSearch(true);
      })
      .catch((error) => {
        if (error.response.status === 500) {
          alert("검색조건을 확인해주세요");
        }
      });
  };

  return (
    <>
      <StyledComponent ref={componentRef}>
        <div className="print-info">
          <p>출력: {userName}</p>
          <p>출력시간: {currentDate}</p>
        </div>
        <h1 className="mb-4 mt-3">
          {search ? "검색결과" : `${data} 실적 및 생산 흐름`}
        </h1>
        <Row className="justify-content-end align-items-center">
          <Col xs="auto">
            <TextField
              label="품번"
              value={productNumber}
              onChange={setProductNumber}
            />
          </Col>
          <Col xs="auto">
            <TextField
              label="모델명"
              value={modelName}
              onChange={setModelName}
            />
          </Col>
          <Col xs="auto">
            <DateField
              label="조회기간"
              value={startDate}
              onChange={setStartDate}
            />
          </Col>
          <Col xs="auto">
            <span className="ms-2">~</span>
          </Col>
          <Col xs="auto">
            <DateField
              label=""
              value={endDate}
              onChange={setEndDate}
              minDate={startDate}
            />
          </Col>
          <Col xs="auto">
            <Button variant="light" onClick={handleSearch}>
              <img src={searchIcon} alt="Search" width="20" height="20" />
            </Button>
          </Col>
        </Row>
        {loading ? (
          <div className="text-center my-5">
            <Spinner animation="border" variant="primary" />
            <h4>데이터를 불러오는 중입니다...</h4>
          </div>
        ) : (
          cardData.map((e, index) => (
            <Suspense fallback={<Spinner />} key={index}>
              <CustomCard data={e} />
            </Suspense>
          ))
        )}
        <PrintPage handlePrintRef={componentRef}></PrintPage>
      </StyledComponent>
    </>
  );
}

export default PerformanceAndProductionFlow;
