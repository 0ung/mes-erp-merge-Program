import { MaterialCost, ModelPurchasePlan, ModelReceiptStatus, StockStatus, WarehouseMaterialStatus } from "../../pages/PurchaseAndReceipt";

export function getData() {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0"); // 월은 0부터 시작하므로 +1
  const day = String(today.getDate()).padStart(2, "0");

  return year + "년 " + month + "월 " + day + "일";
}

export function getDateTime() {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0"); // 월은 0부터 시작하므로 +1
  const day = String(today.getDate()).padStart(2, "0");

  const hours = String(today.getHours()).padStart(2, "0"); // 시
  const minutes = String(today.getMinutes()).padStart(2, "0"); // 분
  const seconds = String(today.getSeconds()).padStart(2, "0"); // 초

  return `${year}년 ${month}월 ${day}일 ${hours}시 ${minutes}분 ${seconds}초`;
}

export function parseJWT(token: string | null) {
  if (!token) {
    return null;
  }

  // JWT 구조는 header.payload.signature 형식이므로 '.'로 나눈다.
  const tokenParts = token.split(".");

  if (tokenParts.length !== 3) {
    throw new Error("Invalid JWT token");
  }

  // 페이로드 부분은 두 번째 요소에 있음
  const base64Url = tokenParts[1];

  // base64Url을 base64로 변환한 후 디코딩
  const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
  const jsonPayload = decodeURIComponent(
    atob(base64)
      .split("")
      .map(function (c) {
        return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
      })
      .join("")
  );

  return JSON.parse(jsonPayload); // JSON 객체로 변환 후 반환
}

export const calculateProductionDataTotals = (data: any[]) => {
  const totals = {
    lotNo: "합계", // 합계 행을 나타내는 필드
    plannedQuantity: 0,
    inputQuantity: 0,
    defectiveQuantity: 0,
    defectRate: 0,
    completedQuantity: 0,
    achievementRate: 0,
    workInProgressQuantity: 0,
    materialCost: 0,
    manHours: 0,
    processingCost: 0,
    subtotal: 0,
    pricePerSet: 0,
    totalProduction: 0,
    performanceMaterialCost: 0,
    performanceProcessingCost: 0,
    totalPerformanceAmount: 0,
    monthlyCumulativeProduction: 0,
  };

  data.forEach((row) => {
    totals.plannedQuantity += row.plannedQuantity || 0;
    totals.inputQuantity += row.inputQuantity || 0;
    totals.defectiveQuantity += row.defectiveQuantity || 0;
    totals.completedQuantity += row.completedQuantity || 0;
    totals.workInProgressQuantity += row.workInProgressQuantity || 0;
    totals.materialCost += row.materialCost || 0;
    totals.manHours += row.manHours || 0;
    totals.processingCost += row.processingCost || 0;
    totals.subtotal += row.subtotal || 0;
    totals.pricePerSet += row.pricePerSet || 0;
    totals.totalProduction += row.totalProduction || 0;
    totals.performanceMaterialCost += row.performanceMaterialCost || 0;
    totals.performanceProcessingCost += row.performanceProcessingCost || 0;
    totals.totalPerformanceAmount += row.totalPerformanceAmount || 0;
    totals.monthlyCumulativeProduction += row.monthlyCumulativeProduction || 0;
    totals.achievementRate += row.achievementRate || 0;
    totals.defectRate += row.defectRate || 0;
  });

  // 평균 비율 필드는 개수에 따라 나눕니다
  const totalRows = data.length || 1;
  totals.achievementRate /= totalRows;
  totals.defectRate /= totalRows;
  return totals;
};

export const caluMaterialCostData = (data: MaterialCost[]) => {
  console.log("데이터 ", data);
  const totals = {
    category: "합계",
    monthlySalesPlan: 0,
    monthlyPurchasePlan: 0,
    dailyDirectTransactionAmount: 0,
    weeklyDirectTransactionAmount: 0,
    monthlyDirectTransactionAmount: 0,
    dailySubcontractAmount: 0,
    weeklySubcontractAmount: 0,
    monthlySubcontractAmount: 0,
    dailyTotalAmount: 0,
    weeklyTotalAmount: 0,
    monthlyTotalAmount: 0,
    dailyDirectReceiptAmount: 0,
    weeklyDirectReceiptAmount: 0,
    monthlyDirectReceiptAmount: 0,
    dailySubcontractReceiptAmount: 0,
    weeklySubcontractReceiptAmount: 0,
    monthlySubcontractReceiptAmount: 0,
    dailyTotalReceiptAmount: 0,
    weeklyTotalReceiptAmount: 0,
    monthlyTotalReceiptAmount: 0,
    dailyPendingDirectAmount: 0,
    weeklyPendingDirectAmount: 0,
    monthlyPendingDirectAmount: 0,
    dailyPendingSubcontractAmount: 0,
    weeklyPendingSubcontractAmount: 0,
    monthlyPendingSubcontractAmount: 0,
    dailyPendingTotalAmount: 0,
    weeklyPendingTotalAmount: 0,
    monthlyPendingTotalAmount: 0
  }

  // 각 데이터 항목을 totals에 더함
  data.forEach(item => {
    totals.dailyDirectTransactionAmount += item.dailyDirectTransactionAmount || 0;
    totals.weeklyDirectTransactionAmount += item.weeklyDirectTransactionAmount || 0;
    totals.monthlyDirectTransactionAmount += item.monthlyDirectTransactionAmount || 0;
    totals.dailySubcontractAmount += item.dailySubcontractAmount || 0;
    totals.weeklySubcontractAmount += item.weeklySubcontractAmount || 0;
    totals.monthlySubcontractAmount += item.monthlySubcontractAmount || 0;
    totals.dailyTotalAmount += item.dailyTotalAmount || 0;
    totals.weeklyTotalAmount += item.weeklyTotalAmount || 0;
    totals.monthlyTotalAmount += item.monthlyTotalAmount || 0;
    totals.dailyDirectReceiptAmount += item.dailyDirectReceiptAmount || 0;
    totals.weeklyDirectReceiptAmount += item.weeklyDirectReceiptAmount || 0;
    totals.monthlyDirectReceiptAmount += item.monthlyDirectReceiptAmount || 0;
    totals.dailySubcontractReceiptAmount += item.dailySubcontractReceiptAmount || 0;
    totals.weeklySubcontractReceiptAmount += item.weeklySubcontractReceiptAmount || 0;
    totals.monthlySubcontractReceiptAmount += item.monthlySubcontractReceiptAmount || 0;
    totals.dailyTotalReceiptAmount += item.dailyTotalReceiptAmount || 0;
    totals.weeklyTotalReceiptAmount += item.weeklyTotalReceiptAmount || 0;
    totals.monthlyTotalReceiptAmount += item.monthlyTotalReceiptAmount || 0;
    totals.dailyPendingDirectAmount += item.dailyPendingDirectAmount || 0;
    totals.weeklyPendingDirectAmount += item.weeklyPendingDirectAmount || 0;
    totals.monthlyPendingDirectAmount += item.monthlyPendingDirectAmount || 0;
    totals.dailyPendingSubcontractAmount += item.dailyPendingSubcontractAmount || 0;
    totals.weeklyPendingSubcontractAmount += item.weeklyPendingSubcontractAmount || 0;
    totals.monthlyPendingSubcontractAmount += item.monthlyPendingSubcontractAmount || 0;
    totals.dailyPendingTotalAmount += item.dailyPendingTotalAmount || 0;
    totals.weeklyPendingTotalAmount += item.weeklyPendingTotalAmount || 0;
    totals.monthlyPendingTotalAmount += item.monthlyPendingTotalAmount || 0;
  });
  totals.monthlySalesPlan += data[0].monthlySalesPlan || 0;
  totals.monthlyPurchasePlan += data[0].monthlyPurchasePlan || 0;
  return totals;
}

export const caluStockStatusData = (data: StockStatus[]) => {
  const totals = {
    category: "합계",
    directPurchaseMaterial: 0,
    subcontractMaterial: 0,
    totalMaterial: 0,
  };

  // 데이터에서 값을 합산
  data.forEach(item => {
    totals.directPurchaseMaterial += item.directPurchaseMaterial || 0;
    totals.subcontractMaterial += item.subcontractMaterial || 0;
    totals.totalMaterial += item.totalMaterial || 0;
  });

  return totals;
};

export const caluModelPurchasePlanData = (data: ModelPurchasePlan[] | null) => {
  const totals = {
    category: "합계",
    salesPlanMonthly: 0,
    materialCostRatio: 0,
    purchasePlanMonthly: 0,
    orderAmountMonthly: 0,
  };

  // 데이터가 null이 아닌지 확인 후 합산
  if (data) {
    data.forEach(item => {
      totals.salesPlanMonthly += item.salesPlanMonthly != null ? item.salesPlanMonthly : 0;
      totals.materialCostRatio += item.materialCostRatio != null ? item.materialCostRatio : 0;
      totals.purchasePlanMonthly += item.purchasePlanMonthly != null ? item.purchasePlanMonthly : 0;
      totals.orderAmountMonthly += item.orderAmountMonthly != null ? item.orderAmountMonthly : 0;
    });
  }

  return totals;
};


export const caluModelReceiptStatusData = (data: ModelReceiptStatus[] | null) => {
  const totals = {
    category: "합계",
    directPurchaseReceipt: 0,
    subcontractReceipt: 0,
    totalMaterialReceipt: 0,
    receiptRatioMonthly: 0,
  };

  // 데이터가 null이 아닌지 확인 후 합산
  if (data) {
    data.forEach(item => {
      totals.directPurchaseReceipt += item.directPurchaseReceipt != null ? item.directPurchaseReceipt : 0;
      totals.subcontractReceipt += item.subcontractReceipt != null ? item.subcontractReceipt : 0;
      totals.totalMaterialReceipt += item.totalMaterialReceipt != null ? item.totalMaterialReceipt : 0;
      totals.receiptRatioMonthly += item.receiptRatioMonthly != null ? item.receiptRatioMonthly : 0;
    });
  }

  return totals;
};

export const caluWarehouseMaterialStatusData = (data: WarehouseMaterialStatus[]) => {
  const totals = {
    category: "합계",
    wiringWaiting: 0,
    wiringInProcess: 0,
    wiringTotal: 0,
    mechanismWaiting: 0,
    mechanismInProcess: 0,
    mechanismTotal: 0,
    packingWaiting: 0,
    packingInProcess: 0,
    packingTotal: 0,
    subMaterialsWaiting: 0,
    subMaterialsInProcess: 0,
    subMaterialsTotal: 0,
    otherWaiting: 0,
    otherInProcess: 0,
    otherTotal: 0,
  };

  // 데이터에서 값을 합산
  data.forEach(item => {
    totals.wiringWaiting += item.wiringWaiting || 0;
    totals.wiringInProcess += item.wiringInProcess || 0;
    totals.wiringTotal += item.wiringTotal || 0;
    totals.mechanismWaiting += item.mechanismWaiting || 0;
    totals.mechanismInProcess += item.mechanismInProcess || 0;
    totals.mechanismTotal += item.mechanismTotal || 0;
    totals.packingWaiting += item.packingWaiting || 0;
    totals.packingInProcess += item.packingInProcess || 0;
    totals.packingTotal += item.packingTotal || 0;
    totals.subMaterialsWaiting += item.subMaterialsWaiting || 0;
    totals.subMaterialsInProcess += item.subMaterialsInProcess || 0;
    totals.subMaterialsTotal += item.subMaterialsTotal || 0;
    totals.otherWaiting += item.otherWaiting || 0;
    totals.otherInProcess += item.otherInProcess || 0;
    totals.otherTotal += item.otherTotal || 0;
  });

  return totals;
};
