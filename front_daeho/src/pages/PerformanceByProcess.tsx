import React, { useEffect, useRef, useState, useMemo, Suspense } from "react";
import { Card, Row, Col, Button, Spinner } from "react-bootstrap";
import CustomBarChart from "../components/tools/CustomBarChart";
import { FaIndustry, FaBoxOpen, FaCog } from "react-icons/fa";
import styled from "styled-components";
import { getData, getDateTime } from "../components/tools/utils";
import PrintPage from "../components/tools/PrintPage";
import apiClient from "../apiClient";
import { PERFORMANCE_API } from "../constants/API";
import { DateField, TextField } from "../components/tools/Search";
import searchIcon from "../asset/search.svg";
import { useNameContext } from "../context/nameProvider";

const timeSlots = [
  "8:00~9:00",
  "9:00~10:00",
  "10:10~11:00",
  "11:00~12:20",
  "13:10~14:00",
  "14:00~15:30",
  "15:40~16:00",
  "16:00~17:00",
  "17:00~18:00",
  "18:20~19:20",
  "19:20~20:20",
];

interface QuantityDataDTO {
  planQuantity: number;
  inputQuantity: number;
  completedQuantity: number;
  defectQuantity: number;
}

interface PerformanceByProcessDTO {
  depart: string;
  process: string;
  modelName: string;
  processStatus: string;
  quantityData: QuantityDataDTO;
  planData: string[];
  inputData: string[];
  completedData: string[];
}

const CustomCard = React.memo<PerformanceByProcessDTO>(
  ({
    depart,
    process,
    modelName,
    processStatus,
    quantityData,
    planData,
    inputData,
    completedData,
  }) => {
    const chartData = useMemo(() => quantityData, [quantityData]);

    return (
      <Card
        className="p-4 mb-4 mt-3 shadow-lg"
        style={{
          backgroundColor:
            depart === "lotResultList에 미반영" || depart === "미정"
              ? "rgba(255, 0, 0, 0.1)"
              : "#ffffff",
        }}
      >
        <Row>
          <Col md={4} className="text-start">
            <h5 className="fs-3 mb-4 d-flex align-items-center">
              <FaIndustry className="me-2" />
              <b>{depart}</b>
            </h5>
            <div className="mb-3">
              <p className="mb-2 fs-5 d-flex align-items-center">
                <FaCog className="me-2" />
                <b>공정:</b> {process}
              </p>
              <p className="mb-2 fs-5 d-flex align-items-center">
                <FaBoxOpen className="me-2" />
                <b>모델명:</b> {modelName}
              </p>
              <p className="mb-2 fs-5 d-flex align-items-center">
                <FaCog className="me-2" />
                <b>진행상태:</b> {processStatus}
              </p>
            </div>
          </Col>
          <Col md={6}>
            <div style={{ width: "100%", height: "300px" }}>
              <CustomBarChart data={chartData} />
            </div>
          </Col>
        </Row>
        <hr className="mt-4" />
        <Row className="justify-content-center " style={{ fontSize: 12 }}>
          <table className=" table table-bordered table-striped">
            <thead className="table-light">
              <tr>
                <th rowSpan={2}>생산수량</th>
                {timeSlots.map((slot, index) => (
                  <th key={index}>{slot}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              <tr>
                <th>계획수량</th>
                {planData.map((value, index) => (
                  <td key={index}>{value === "" ? "0" : value}</td>
                ))}
              </tr>
              <tr>
                <th>투입수량</th>
                {inputData.map((value, index) => (
                  <td key={index}>{value === "" ? "0" : value}</td>
                ))}
              </tr>
              <tr>
                <th>완료수량</th>
                {completedData.map((value, index) => (
                  <td key={index}>{value === "" ? "0" : value}</td>
                ))}
              </tr>
            </tbody>
          </table>
        </Row>
      </Card>
    );
  }
);

const StyledComponent = styled.div`
  .print-info {
    display: none;
  }

  @media print {
    max-width: 500mm;
    font-size: 10px;
    @page {
      size: A4 landscape;
    }
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
    /* 셀과 헤더의 너비를 비율에 따라 조정 */
    .ag-cell,
    .ag-header-cell {
      font-size: 11px;
    }
  }
`;

function PerformanceByProcess() {
  const [performData, setPerformData] = useState<PerformanceByProcessDTO[]>([]);
  const { userName } = useNameContext();
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [modelName, setModelName] = useState<string>("");
  const [process, setProcess] = useState<string>("");
  const currentDate = getDateTime();
  const componentRef = useRef<any>();
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState(false);

  const handlePageData = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get(PERFORMANCE_API);
      setPerformData(response.data);
      console.log(response.data);
    } catch (error) {
      console.error("데이터를 가져오는 데 실패했습니다.", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    handlePageData();
  }, []);

  const today = getData();
  const handleSearch = () => {
    apiClient
      .get(
        `${PERFORMANCE_API}/${startDate}/${endDate}?modelName=${modelName}&process=${process}`
      )
      .then((response) => {
        console.log(response.data);
        setPerformData(response.data);
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
          {search ? "검색결과" : `${today} 공정별 실적`}
        </h1>
        <Row className="justify-content-end align-items-center g-2">
          <Col xs="auto">
            <TextField
              label="모델명"
              value={modelName}
              onChange={setModelName}
            />
          </Col>
          <Col xs="auto">
            <TextField label="공정" value={process} onChange={setProcess} />
          </Col>
          <Col xs="auto">
            <DateField
              label="시작일"
              value={startDate}
              onChange={setStartDate}
            />
          </Col>
          <Col xs="auto">
            <span className="ms-2">~</span>
          </Col>
          <Col xs="auto">
            <DateField
              label="종료일"
              value={endDate}
              onChange={setEndDate}
              minDate={startDate}
            />
          </Col>
          <Col xs="auto">
            <Button variant="light" onClick={handleSearch}>
              <img src={searchIcon} alt="Search" width="20" height="20" />{" "}
            </Button>
          </Col>
        </Row>

        {loading ? (
          <div className="text-center my-5">
            <Spinner animation="border" variant="primary" />
            <h4>데이터를 불러오는 중입니다...</h4>
          </div>
        ) : (
          performData.map((e, index) => (
            <Suspense fallback={<Spinner />} key={index}>
              <CustomCard {...e} />
            </Suspense>
          ))
        )}

        <PrintPage handlePrintRef={componentRef} />
      </StyledComponent>
    </>
  );
}

export default PerformanceByProcess;
