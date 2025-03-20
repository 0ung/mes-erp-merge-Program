import { ColDef, ColGroupDef, ValueGetterParams } from "ag-grid-community";
import CustomButton from "./CustomButton";

const numberFormatter = (params: any) => {
  if (params.value != null) {
    return params.value.toLocaleString("ko-KR", { maximumFractionDigits: 0 });
  }
  return "";
};

export const attendanceStatus: (ColDef | ColGroupDef)[] = [
  {
    headerName: "보유인원(명)",
    children: [
      {
        headerName: "생산(간접인원)",
        field: "productionPersonnel",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "직접",
        field: "directPersonnel",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "지원부서",
        field: "supportPersonnel",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "기타",
        field: "etcPersonnel",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "합계",
        field: "totalPersonnel",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "보유공수(분)",
    children: [
      {
        headerName: "간접",
        field: "indirectManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "직접",
        field: "directManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "합계",
        field: "totalManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "근태현황(명,분)",
    children: [
      {
        headerName: "직접인원",
        children: [
          {
            headerName: "년차",
            children: [
              {
                headerName: "인원",
                field: "directYearlyLeavePersonnel",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
              {
                headerName: "시간",
                field: "directYearlyLeaveHours",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
            ],
          },
          {
            headerName: "시간차",
            children: [
              {
                headerName: "인원",
                field: "directPartTimePersonnel",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
              {
                headerName: "시간",
                field: "directPartTimeHours",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
            ],
          },
          {
            headerName: "기타",
            children: [
              {
                headerName: "인원",
                field: "directEtcPersonnel",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
              {
                headerName: "시간",
                field: "directEtcPersonnelTime",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
            ],
          },
          {
            headerName: "합계",
            children: [
              {
                headerName: "인원",
                field: "directTotalPersonnel",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
              {
                headerName: "시간",
                field: "directTotalPersonnelTime",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
            ],
          },
        ],
      },
      {
        headerName: "간접인원",
        children: [
          {
            headerName: "년차",
            children: [
              {
                headerName: "인원",
                field: "subYearlyLeavePersonnel",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
              {
                headerName: "시간",
                field: "subYearlyLeaveHours",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
            ],
          },
          {
            headerName: "시간차",
            children: [
              {
                headerName: "인원",
                field: "subPartTimePersonnel",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
              {
                headerName: "시간",
                field: "subPartTimeHours",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
            ],
          },
          {
            headerName: "합계",
            children: [
              {
                headerName: "인원",
                field: "subTotalPersonnel",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
              {
                headerName: "시간",
                field: "subTotalHours",
                valueFormatter: (params) =>
                  params.value
                    ? Math.round(params.value).toLocaleString("ko-KR")
                    : "0",
              },
            ],
          },
        ],
      },
    ],
  },
  {
    headerName: "가용인원 & 공수",
    children: [
      {
        headerName: "직접인원",
        children: [
          {
            headerName: "인원",
            field: "directMan",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "시간",
            field: "directTime",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
      {
        headerName: "간접인원",
        children: [
          {
            headerName: "인원",
            field: "subMan",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "시간",
            field: "subTime",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
      {
        headerName: "지원(기타)",
        children: [
          {
            headerName: "인원",
            field: "etcMan",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "시간",
            field: "etcTime",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
      {
        headerName: "합계",
        children: [
          {
            headerName: "인원",
            field: "totalMan",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "시간",
            field: "totalTime",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
    ],
  },
  {
    headerName: "비고",
    field: "attendanceRemark",
  },
];

export const manPowerInputManage: (ColDef | ColGroupDef)[] = [
  {
    headerName: "공수관리",
    children: [
      {
        headerName: "가용인원",
        field: "availablePersonnel",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "가용공수",
        field: "availableManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "표준공수",
        field: "standardManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "비생산공수",
        field: "nonProductiveManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "부하공수",
        field: "loadManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "정지공수",
        field: "stoppedManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "재작업공수",
        field: "reworkManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "실동공수",
        field: "actualManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "작업공수",
        field: "workingManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "생산(작업)효율 관리",
    children: [
      {
        headerName: "작업능률",
        field: "workEfficiency",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "실동효율",
        field: "actualEfficiency",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "Loss율",
        field: "lossRate",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "공수투입율",
        field: "manHourInputRate",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "공수가동율",
        field: "manHourOperationRate",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "공수종합효율",
        field: "totalEfficiency",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "잔업(특근)지원",
        children: [
          {
            headerName: "인원",
            field: "specialSupportPersonnel",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "공수",
            field: "specialSupportManHours",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "추가투입율",
            field: "additionalInputRate",
            width: 150,
            valueFormatter: (params) =>
              params.value ? params.value.toFixed(0) + "%" : "0%",
          },
        ],
      },
    ],
  },
  {
    headerName: "설비효율관리(8h 기준)",
    children: [
      {
        headerName: "Flux 설비",
        children: [
          {
            headerName: "가동시간",
            field: "fluxEquipmentRunningTime",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "가동율",
            field: "fluxEquipmentRunningRate",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
      {
        headerName: "Soldering 설비",
        children: [
          {
            headerName: "가동시간",
            field: "solderingEquipmentRunningTime",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "가동율",
            field: "solderingEquipmentRunningRate",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
    ],
  },
  {
    headerName: "비고",
    field: "manPowerRemark",
  },
];

export const costAnalyze: (ColDef | ColGroupDef)[] = [
  {
    headerName: "총재료비",
    children: [
      {
        headerName: "원재료비",
        field: "rawMaterialCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "부자재",
        field: "subsidiaryMaterialCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "합계",
        field: "totalMaterialCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "총가공비",
    children: [
      {
        headerName: "생산가공비(사내)",
        field: "productionCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "SM/IM",
        field: "smImCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "공정내 외주",
        field: "externalProcessingCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "합계",
        field: "totalProductionCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "총생산실적금액",
    field: "totalProductionAmount",
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  {
    headerName: "손실비용",
    children: [
      {
        headerName: "불량",
        children: [
          {
            headerName: "수량",
            field: "lossHandlingCnt",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "비용",
            field: "lossHandlingCost",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
      {
        headerName: "정지 & 비생산",
        children: [
          {
            headerName: "시간",
            field: "nonProductiveTimeHour",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "비용",
            field: "nonProductiveTimeCost",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
      {
        headerName: "재작업",
        children: [
          {
            headerName: "시간",
            field: "reworkTimeCnt",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "비용",
            field: "reworkTimeCost",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
      {
        headerName: "합계",
        children: [
          {
            headerName: "비용",
            field: "totalLossCost",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
    ],
  },
  {
    headerName: "총생산 직접투입 비용",
    field: "totalProductionDirectInputCost",
    width: 150,
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
];

export const manufacturingCostAnalysis: (ColDef | ColGroupDef)[] = [
  {
    headerName: "제조경비",
    children: [
      {
        headerName: "직접비",
        field: "directPersonnelCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "간접비",
        field: "indirectPersonnelCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "일반관리비",
        field: "generalManagementCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "판관비",
        field: "salesCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "설비감가",
        field: "equipmentDepreciationCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "기타",
        field: "otherCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "합계",
        field: "totalManufacturingCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "총생산 비용",
    field: "totalProductCost",
    width: 150,
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  {
    headerName: "총생산 견적금액",
    field: "totalEstimateCost",
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  {
    headerName: "총생산 이익",
    field: "totalProfit",
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  {
    headerName: "총생산 이익율",
    field: "netProfit",
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  {
    headerName: "투자비용",
    children: [
      {
        headerName: "치구/설비/환경",
        field: "investCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "지출(인건비)",
    field: "totalExpenditure",
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  {
    headerName: "비고",
    field: "costRemark",
  },
];

//메인 생산일보 끝

export const productionPerformanceStatus: (ColDef | ColGroupDef)[] = [
  {
    headerName: "No",
    valueGetter: (params: ValueGetterParams) => {
      if (params.node && params.node.rowIndex !== null) {
        return params.node.rowIndex + 1;
      }
      return null;
    }, // 0부터 시작하므로 +1
  },
  {
    headerName: "Lot No",
    field: "lotNo",
    autoHeight: true,
  },
  {
    headerName: "제품명",
    field: "productName",
    autoHeight: true,
  },
  {
    headerName: "model No",
    field: "modelNo",
    autoHeight: true,
  },
  {
    headerName: "사양",
    field: "specification",
    autoHeight: true,
  },
  {
    headerName: "생산팀",
    field: "depart",
    autoHeight: true,
  }, {
    headerName: "대호코드",
    field: "itemCd",
    autoHeight: true,
  }, {
    headerName: "공정명",
    field: "itemName",
    autoHeight: true,
  },
  {
    headerName: "단위",
    field: "unit",
  },
  {
    headerName: "계획수량",
    field: "plannedQuantity",
  },
  {
    headerName: "투입수량",
    field: "inputQuantity",
  },
  {
    headerName: "불량수량",
    field: "defectiveQuantity",
  },
  {
    headerName: "불량율(%)",
    field: "defectRate",
    valueFormatter: (params) =>
      params.value ? params.value.toFixed(0) + "%" : "0%",
  },
  {
    headerName: "생산완료수량",
    field: "completedQuantity",
  },
  {
    headerName: "달성율(%)",
    field: "achievementRate",
    valueFormatter: (params) => {
      if (params.value <= 0) {
        return "0%";
      } else {
        return params.value.toFixed(0) + "%";
      }
    },
  },
  {
    headerName: "재공수량",
    field: "workInProgressQuantity",
  },
  {
    headerName: "재공수량",
    children: [
      {
        headerName: "재료비",
        field: "materialCost",
        cellStyle: (params) => {
          if (params.value <= 0) {
            return { backgroundColor: "red", color: "white" };
          }
        },
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "공수(초)",
        field: "manHours",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(1) : "0.0",
      },
      {
        headerName: "가공비",
        field: "processingCost",
        cellStyle: (params) => {
          if (params.value <= 0) {
            return { backgroundColor: "red", color: "white" };
          }
        },
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "소계",
        field: "subtotal",
        cellStyle: (params) => {
          if (params.value <= 0) {
            return { backgroundColor: "red", color: "white" };
          }
        },
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "공정 생산 견적가",
    children: [
      {
        headerName: "/1set",
        field: "pricePerSet",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "총생산",
        field: "totalProduction",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "총 생산 실적",
    children: [
      {
        headerName: "재료비",
        field: "performanceMaterialCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "가공비",
        field: "performanceProcessingCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "실적금액",
        field: "totalPerformanceAmount",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "월간 누적 생산량",
    field: "monthlyCumulativeProduction",
  },
];
export const manInputManage: (ColDef | ColGroupDef)[] = [
  {
    headerName: "공수관리",
    children: [
      {
        headerName: "가용인원",
        field: "availablePersonnel",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "표준공수",
        field: "standardManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "가용공수",
        field: "availableManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "비생산공수",
        field: "nonProductiveManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "부하공수",
        field: "workloadManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "정지공수",
        field: "stopManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "재작업공수",
        field: "reworkManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "실동공수",
        field: "actualManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "작업공수",
        field: "workingManHours",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "생산(작업)효율관리",
    children: [
      {
        headerName: "작업능률",
        field: "workEfficiency",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "실동효율",
        field: "actualEfficiency",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "Loss율",
        field: "lossRate",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "공수투입율",
        field: "manHourInputRate",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "공수가동율",
        field: "manHourOperationRate",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "공수종합효율",
        field: "overallManHourEfficiency",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "잔업(특근) · 지원",
        children: [
          {
            headerName: "인원",
            field: "overtimePersonnel",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "공수",
            field: "overtimeManHours",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "추가투입율",
            field: "additionalInputRate",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
    ],
  },
  {
    headerName: "설비효율관리(8h 기준)",
    children: [
      {
        headerName: "Flux 설비",
        children: [
          {
            headerName: "전원 ON 시간",
            field: "fluxOnTime",
            valueFormatter: (params) =>
              params.value ? params.value.toFixed(1) : "0.0",
          },
          {
            headerName: "실가동시간",
            field: "fluxOperatingTime",
            valueFormatter: (params) =>
              params.value ? params.value.toFixed(1) : "0.0",
          },
          {
            headerName: "실가동율",
            field: "fluxOperatingRate",
            valueFormatter: (params) =>
              params.value ? params.value.toLocaleString("ko-KR") : "0%",
          },
        ],
      },
      {
        headerName: "Soldering 설비",
        children: [
          {
            headerName: "전원 ON 시간",
            field: "solderingOnTime",
            valueFormatter: (params) =>
              params.value ? params.value.toFixed(1) : "0.0",
          },
          {
            headerName: "실가동시간",
            field: "solderingOperatingTime",
            valueFormatter: (params) =>
              params.value ? params.value.toFixed(1) : "0.0",
          },
          {
            headerName: "실가동율",
            field: "solderingOperatingRate",
            valueFormatter: (params) =>
              params.value ? params.value.toLocaleString("ko-KR") : "0%",
          },
        ],
      },
    ],
  },
  {
    headerName: "비고",
    field: "remarks",
  },
];
export const productionCostAnalzye: (ColDef | ColGroupDef)[] = [
  {
    headerName: "공정 총재료비",
    children: [
      {
        headerName: "총생산재료비 합계",
        field: "totalProductionMaterialCostSum",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "공정사용 부자재 합계",
        field: "processUsageSubMaterialSum",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "총합계",
        field: "materialTotalSum",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "공정 총가공비",
    children: [
      {
        headerName: "총 생산 가공비 합계",
        field: "totalProductionProcessingCostSum",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "공정내 외주작업 합계",
        field: "processInOutsourcingWorkSum",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "총합계",
        field: "processTotalSum",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "총 생산 실적 합계",
    field: "totalProductionActualSum",
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  {
    headerName: "손실비용",
    children: [
      {
        headerName: "불량",
        children: [
          {
            headerName: "수량",
            field: "defectiveQuantity",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "비용",
            field: "defectiveCost",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
      {
        headerName: "정지 & 비생산",
        children: [
          {
            headerName: "시간",
            field: "stopAndNonproductiveHours",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "비용",
            field: "stopAndNonproductiveCost",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
      {
        headerName: "재작업",
        children: [
          {
            headerName: "시간",
            field: "reworkHours",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
          {
            headerName: "비용",
            field: "reworkCost",
            valueFormatter: (params) =>
              params.value
                ? Math.round(params.value).toLocaleString("ko-KR")
                : "0",
          },
        ],
      },
      {
        headerName: "비용합계",
        field: "totalCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "제조경비",
    children: [
      {
        headerName: "제조간접비",
        field: "manufacturingExpenseIndirect",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "일반관리비",
        field: "manufacturingExpenseGeneralAdmin",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "판관비",
        field: "manufacturingExpenseSellingAndAdmin",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "설비감가 및 기타",
        field: "manufacturingExpenseDepreciationEtc",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "합계",
        field: "manufacturingExpenseTotal",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "공정 총생산",
    children: [
      {
        headerName: "견적가 합계",
        field: "estimateCostTotal",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "투입금액",
        field: "processTotalProductionInputAmount",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "실적이익",
        field: "processTotalProductionActualProfit",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "이익율",
        field: "processTotalProductionProfitRate",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "손실율",
        field: "processTotalProductionLossRate",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "재료비율",
        field: "processTotalProductionMaterialRate",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
      {
        headerName: "가공비율",
        field: "processTotalProductionProcessingRate",
        valueFormatter: (params) =>
          params.value ? params.value.toFixed(0) + "%" : "0%",
      },
    ],
  },
];

export const techProblem: (ColDef | ColGroupDef)[] = [
  { headerName: "NO", field: "no" },
  { headerName: "구분", field: "category" },
  { headerName: "내용", field: "description" },
  { headerName: "인원", field: "personnel" },
  {
    headerName: "공수",
    field: "manHours",
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  {
    headerName: "비용",
    field: "cost",
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  { headerName: "처리결과", field: "result" },
  { headerName: "진행사항", field: "progress" },
  { headerName: "책임부서 1", field: "responsibleDept1" },
  { headerName: "책임부서 2", field: "responsibleDept2" },
  { headerName: "책임부서 비고", field: "deptNote" },
];

export const stopRisk: (ColDef | ColGroupDef)[] = [
  { headerName: "NO", field: "no" },
  { headerName: "구분", field: "category" },
  { headerName: "내용", field: "description" },
  { headerName: "인원", field: "personnel" },
  {
    headerName: "공수",
    field: "manHours",
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  {
    headerName: "비용",
    field: "cost",
    valueFormatter: (params) =>
      params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
  },
  { headerName: "처리결과", field: "result" },
  { headerName: "진행사항", field: "progress" },
  { headerName: "책임부서 1", field: "responsibleDept1" },
  { headerName: "책임부서 2", field: "responsibleDept2" },
  { headerName: "책임부서 비고", field: "deptNote" },
];

export const userColumns: (ColDef | ColGroupDef)[] = [
  {
    headerName: "이름",
    field: "name",
    sortable: true,
    resizable: true,
    filter: true,
  },
  {
    headerName: "아이디",
    field: "id",
    sortable: true,
    resizable: true,
    filter: true,
  },
  {
    headerName: "직급",
    field: "rank",
    sortable: true,
    resizable: true,
    filter: true,
  },
  {
    headerName: "권한",
    field: "auth",
    sortable: true,
    resizable: true,
    filter: true,
  },
  {
    headerName: "수정",
    field: "edit",
    cellRenderer: (params: any) => (
      <CustomButton onClick={() => params.context.openEditModal(params.data)}>
        수정
      </CustomButton>
    ),
  },
  {
    headerName: "비밀번호 초기화",
    field: "resetPassword",
    cellRenderer: (params: any) => (
      <CustomButton onClick={() => params.context.handlePassword(params.data)}>
        초기화
      </CustomButton>
    ),
  },
];

export const processStock: (ColDef | ColGroupDef)[] = [
  {
    headerName: "제품명",
    field: "productName",
  },
  {
    headerName: "Model No",
    field: "modelNo",
  },
  {
    headerName: "사양",
    field: "specification",
  },
  {
    headerName: "Unit",
    children: [
      {
        headerName: "자재비",
        field: "materialCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "가공비",
        field: "processingCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "합계",
        field: "totalCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "공정재공",
    children: [
      {
        headerName: "수량",
        field: "wipQuantity",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "비용",
        field: "wipCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "공정완료(QC검사대기)",
    children: [
      {
        headerName: "수량",
        field: "qcPendingQuantity",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "비용",
        field: "qcPendingCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "공정투입대기(QC검사완료)",
    children: [
      {
        headerName: "수량",
        field: "qcPassedQuantity",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "비용",
        field: "qcPassedCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "불량",
    children: [
      {
        headerName: "수량",
        field: "defectiveQuantity",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "비용",
        field: "defectiveCost",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "합계",
    children: [
      {
        headerName: "수량",
        field: "totalQuantity",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
      {
        headerName: "비용",
        field: "totalCostSummary",
        valueFormatter: (params) =>
          params.value ? Math.round(params.value).toLocaleString("ko-KR") : "0",
      },
    ],
  },
  {
    headerName: "비고",
    field: "remarks",
  },
];

export const LTUnusedMaterial: (ColDef | ColGroupDef)[] = [
  {
    headerName: "No",
    valueGetter: (params: ValueGetterParams) => {
      if (params.node && params.node.rowIndex !== null) {
        return params.node.rowIndex + 1;
      }
      return null;
    },
  },
  {
    headerName: "Parts Code",
    field: "ItemCd",
  },
  {
    headerName: "부품명",
    field: "ItemName",
  },
  {
    headerName: "발생일",
    field: "DateOfOccurrence",
  },
  {
    headerName: "사유",
    field: "Reason",
  },
  {
    headerName: "적용내용",
    field: "ApplyContents",
  },
  {
    headerName: "책임구분",
    field: "ResponsibilityClassification",
  },
  {
    headerName: "현재재고",
    field: "StockQty",
  },
  {
    headerName: "월평균 사용",
    children: [
      {
        headerName: "발생 전",
        field: "BeforeOccurrenceUsingQty",
      },
      {
        headerName: "발생 후",
        field: "AfterOccurrenceUsingQty",
      },
    ],
  },
  {
    headerName: "처리내용",
    children: [
      {
        headerName: "장기재고",
        field: "LongTermInventory",
      },
      {
        headerName: "불용재고",
        field: "InsolvencyStock",
      },
    ],
  },
  {
    headerName: "처리결과",
    children: [
      {
        headerName: "재판매",
        field: "Resale",
      },
      {
        headerName: "폐기",
        field: "Disuse",
      },
    ],
  },
];

export const DailyMaterialCost: (ColDef | ColGroupDef)[] = [
  { headerName: "구분", field: "category" },
  {
    headerName: "매출계획(월)",
    field: "monthlySalesPlan",
    valueFormatter: numberFormatter,
  },
  {
    headerName: "구매계획(월)",
    field: "monthlyPurchasePlan",
    valueFormatter: numberFormatter,
  },
  {
    headerName: "발주 금액",
    children: [
      {
        headerName: "직거래",
        children: [
          {
            headerName: "일간금액",
            field: "dailyDirectTransactionAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "주간금액",
            field: "weeklyDirectTransactionAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "월간금액",
            field: "monthlyDirectTransactionAmount",
            valueFormatter: numberFormatter,
          },
        ],
      },
      {
        headerName: "사급",
        children: [
          {
            headerName: "일간금액",
            field: "dailySubcontractAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "주간금액",
            field: "weeklySubcontractAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "월간금액",
            field: "monthlySubcontractAmount",
            valueFormatter: numberFormatter,
          },
        ],
      },
      {
        headerName: "합계",
        children: [
          {
            headerName: "일간금액",
            field: "dailyTotalAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "주간금액",
            field: "weeklyTotalAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "월간금액",
            field: "monthlyTotalAmount",
            valueFormatter: numberFormatter,
          },
        ],
      },
    ],
  },
  {
    headerName: "입고 금액",
    children: [
      {
        headerName: "직거래",
        children: [
          {
            headerName: "일간금액",
            field: "dailyDirectReceiptAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "주간금액",
            field: "weeklyDirectReceiptAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "월간금액",
            field: "monthlyDirectReceiptAmount",
            valueFormatter: numberFormatter,
          },
        ],
      },
      {
        headerName: "사급",
        children: [
          {
            headerName: "일간금액",
            field: "dailySubcontractReceiptAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "주간금액",
            field: "weeklySubcontractReceiptAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "월간금액",
            field: "monthlySubcontractReceiptAmount",
            valueFormatter: numberFormatter,
          },
        ],
      },
      {
        headerName: "합계",
        children: [
          {
            headerName: "일간금액",
            field: "dailyTotalReceiptAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "주간금액",
            field: "weeklyTotalReceiptAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "월간금액",
            field: "monthlyTotalReceiptAmount",
            valueFormatter: numberFormatter,
          },
        ],
      },
    ],
  },
  {
    headerName: "입고 대기 금액",
    children: [
      {
        headerName: "직거래",
        children: [
          {
            headerName: "일간금액",
            field: "dailyPendingDirectAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "주간금액",
            field: "weeklyPendingDirectAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "월간금액",
            field: "monthlyPendingDirectAmount",
            valueFormatter: numberFormatter,
          },
        ],
      },
      {
        headerName: "사급",
        children: [
          {
            headerName: "일간금액",
            field: "dailyPendingSubcontractAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "주간금액",
            field: "weeklyPendingSubcontractAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "월간금액",
            field: "monthlyPendingSubcontractAmount",
            valueFormatter: numberFormatter,
          },
        ],
      },
      {
        headerName: "합계",
        children: [
          {
            headerName: "일간금액",
            field: "dailyPendingTotalAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "주간금액",
            field: "weeklyPendingTotalAmount",
            valueFormatter: numberFormatter,
          },
          {
            headerName: "월간금액",
            field: "monthlyPendingTotalAmount",
            valueFormatter: numberFormatter,
          },
        ],
      },
    ],
  },
];

export const StockStatus: (ColDef | ColGroupDef)[] = [
  {
    headerName: "구분",
    field: "category",
  },
  {
    headerName: "자재 재고 현황",
    children: [
      {
        headerName: "직구매자재",
        field: "directPurchaseMaterial",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "사급자재",
        field: "subcontractMaterial",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "합계",
        field: "totalMaterial",
        valueFormatter: numberFormatter,
      },
    ],
  },
];

export const ModelPurchasePlan: (ColDef | ColGroupDef)[] = [
  {
    headerName: "No",
    valueGetter: (params: any) => {
      if (params.node && params.node.rowIndex !== null) {
        return params.node.rowIndex + 1;
      }
      return null;
    },
  },
  {
    headerName: "구분",
    field: "category",
  },
  {
    headerName: "모델별",
    children: [
      {
        headerName: "매출계획 (월)",
        field: "salesPlanMonthly",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "재료비 비율",
        field: "materialCostRatio",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "구매계획 (월)",
        field: "purchasePlanMonthly",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "발주금액 (월)",
        field: "orderAmountMonthly",
        valueFormatter: numberFormatter,
      },
    ],
  },
  {
    headerName: "비고",
    field: "remarks",
  },
];

export const ModelReceiptStatus: (ColDef | ColGroupDef)[] = [
  {
    headerName: "No",
    valueGetter: (params: any) => {
      if (params.node && params.node.rowIndex !== null) {
        return params.node.rowIndex + 1;
      }
      return null;
    },
  },
  {
    headerName: "구분",
    field: "category",
  },
  {
    headerName: "입고 현황",
    children: [
      {
        headerName: "직구매 입고",
        field: "directPurchaseReceipt",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "사급입고",
        field: "subcontractReceipt",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "자재입고 합계",
        field: "totalMaterialReceipt",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "입고비중 / 월 (매출계획 기준)",
        field: "receiptRatioMonthly",
        valueFormatter: numberFormatter,
      },
    ],
  },
  {
    headerName: "비고",
    field: "remarks",
  },
];

export const WarehouseMaterialStatus: (ColDef | ColGroupDef)[] = [
  {
    headerName: "구분",
    field: "category",
  },
  {
    headerName: "전장",
    children: [
      {
        headerName: "대기",
        field: "wiringWaiting",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "공정진행",
        field: "wiringInProcess",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "합계",
        field: "wiringTotal",
        valueFormatter: numberFormatter,
      },
    ],
  },
  {
    headerName: "기구",
    children: [
      {
        headerName: "대기",
        field: "mechanismWaiting",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "공정진행",
        field: "mechanismInProcess",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "합계",
        field: "mechanismTotal",
        valueFormatter: numberFormatter,
      },
    ],
  },
  {
    headerName: "포장",
    children: [
      {
        headerName: "대기",
        field: "packingWaiting",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "공정진행",
        field: "packingInProcess",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "합계",
        field: "packingTotal",
        valueFormatter: numberFormatter,
      },
    ],
  },
  {
    headerName: "부자재",
    children: [
      {
        headerName: "대기",
        field: "subMaterialsWaiting",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "공정진행",
        field: "subMaterialsInProcess",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "합계",
        field: "subMaterialsTotal",
        valueFormatter: numberFormatter,
      },
    ],
  },
  {
    headerName: "기타",
    children: [
      {
        headerName: "대기",
        field: "otherWaiting",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "공정진행",
        field: "otherInProcess",
        valueFormatter: numberFormatter,
      },
      {
        headerName: "합계",
        field: "otherTotal",
        valueFormatter: numberFormatter,
      },
    ],
  },
];
