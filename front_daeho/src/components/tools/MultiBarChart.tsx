import {
  Chart as ChartJS,
  LinearScale,
  CategoryScale,
  BarElement,
  PointElement,
  LineElement,
  Legend,
  Tooltip,
  LineController,
  BarController,
  Title,
  Filler,
  ArcElement,
} from "chart.js";
import { Chart } from "react-chartjs-2";

ChartJS.register(
  LinearScale,
  CategoryScale,
  BarElement,
  PointElement,
  LineElement,
  Legend,
  Tooltip,
  LineController,
  BarController,
  Title,
  Filler,
  ArcElement
);

interface MultiBarChartProps {
  chartData: {
    [key: string]: {
      planQty: number;
      inputQty: number;
      completedQty: number;
      inputCost: number;
      completedRate: number;
    };
  };
  width: string;
  height: string;
}

function MultiBarChart({ chartData, width, height }: MultiBarChartProps) {
  const labels = Object.keys(chartData);
  const data = {
    labels,
    datasets: [
      {
        type: "bar" as const,
        label: "계획수량",
        backgroundColor: "#3498db", // 파란색 (계획 수량)
        data: labels.map((label) => chartData[label]?.planQty || 0),
        borderColor: "white",
      },
      {
        type: "bar" as const,
        label: "투입수량",
        backgroundColor: "#2ecc71", // 녹색 (투입 수량)
        data: labels.map((label) => chartData[label]?.inputQty || 0),
        borderColor: "white",
      },
      {
        type: "bar" as const,
        label: "완료수량",
        backgroundColor: "#f1c40f", // 노란색 (완료 수량)
        data: labels.map((label) => chartData[label]?.completedQty || 0),
        borderColor: "white",
      },
      {
        type: "line" as const,
        label: "완료율 (%)",
        borderColor: "#e74c3c", // 빨간색 (완료율)
        backgroundColor: "#e74c3c",
        fill: false,
        data: labels.map((label) => chartData[label]?.completedRate || 0),
        yAxisID: "y-axis-2",
      },
    ],
  };

  const options: any = {
    plugins: {
      legend: {
        position: "top",
      },
      title: {
        display: true,
        text: "계획수량, 투입수량, 완료수량, 완료율, 투입금액 비교",
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 10,
        },
      },
      "y-axis-2": {
        type: "linear",
        position: "right",
        beginAtZero: true,
        ticks: {
          callback: function (value: number) {
            return value + "%";
          },
        },
      },
    },
  };
  console.log(data);
  return (
    <Chart
      type="bar"
      data={data}
      options={options}
      width={width}
      height={height}
    />
  );
}

export default MultiBarChart;
