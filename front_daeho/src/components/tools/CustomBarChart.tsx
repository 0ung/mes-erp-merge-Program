import React from "react";
import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

// Chart.js에 필요한 스케일과 요소 등록
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

// BarchartProps 타입 정의
type BarchartProps = {
  data: {
    planQuantity: number;
    inputQuantity: number;
    completedQuantity: number;
    defectQuantity: number;
  };
  title?: string; // 차트 제목 (선택적)
};

const CustomBarChart: React.FC<BarchartProps> = ({
  data,
  title = "생산 수량 분석",
}) => {
  // 차트 데이터 형식으로 변환
  const chartData = {
    labels: ["계획 수량", "투입 수량", "완료 수량", "불량 수량"], // X축 레이블
    datasets: [
      {
        label: "Quantity",
        data: [
          data.planQuantity,
          data.inputQuantity,
          data.completedQuantity,
          data.defectQuantity,
        ], // Y축 값 (각 수량)
        backgroundColor: ["#3498db", "#2ecc71", "#f1c40f", "#e74c3c"], // 각 항목별 색상
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        display: false, // 범례 숨김
      },
      title: {
        display: true,
        text: title, // 차트 제목
      },
    },
    scales: {
      y: {
        title: {
          display: true,
          text: "수량", // Y축 제목
        },
        beginAtZero: true, // Y축 값 0부터 시작
      },
    },
  };

  return <Bar data={chartData} options={options} />;
};

export default CustomBarChart;
