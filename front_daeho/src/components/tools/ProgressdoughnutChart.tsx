import { Doughnut } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";

ChartJS.register(ArcElement, Tooltip, Legend);

function ProgressDoughnutChart({
  progress,
  width,
}: {
  progress: number;
  width: string;
}) {
  // 차트 데이터 설정
  const data = {
    labels: ["진행률", "남은 진행률"],
    datasets: [
      {
        data: [progress, progress > 100 ? 0 : 100 - progress], // 진행률과 남은 진행률 데이터
        backgroundColor: ["#007bff", "#d3d3d3"], // 진행률 색상, 남은 진행률 색상
        borderWidth: 0,
        cutout: "80%",
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: { display: false }, // 범례 비활성화
    },
  };

  const plugins = [
    {
      id: "text",
      beforeDraw: function (chart: any) {
        const ctx = chart.ctx;
        const width = chart.width;
        const height = chart.height;
        ctx.restore();
        const fontSize = (height / 114).toFixed(2);
        ctx.font = `${fontSize}em sans-serif`;
        ctx.textBaseline = "middle";

        // progress 값이 100을 초과하면 "초과 생산" 텍스트 표시
        const text = `${progress.toFixed(2)}%`;
        const textX = Math.round((width - ctx.measureText(text).width) / 2);
        const textY = height / 2;
        ctx.fillText(text, textX, textY);
        ctx.save();
      },
    },
  ];

  return (
    <div style={{ width: width, margin: "auto" }}>
      <Doughnut data={data} options={options} plugins={plugins} />
    </div>
  );
}

export default ProgressDoughnutChart;
