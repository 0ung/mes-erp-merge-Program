package codehows.com.daehoint.dto;

import codehows.com.daehoint.entity.erp.VLGWHStockWHAmtList;
import codehows.com.daehoint.entity.erp.VPUORDAmtList;
import codehows.com.daehoint.entity.mes.MaterialPurchasePlanMonthly;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseAndReceiptResponse {
    private List<DailyMaterialCostDTO> dailyMaterialCost;
    private List<StockStatusDTO> stockStatus;
    private List<ModelPurchasePlanDTO> modelPurchasePlan;
    private List<ModelReceiptStatusDTO> modelReceiptStatus;
    private List<WarehouseMaterialStatusDTO> warehouseMaterialStatus;

    private static Double sum(Double a, Double b) {
        if (a == null)
            a = 0.0;
        if (b == null)
            b = 0.0;
        return a + b;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DailyMaterialCostDTO {
        @Setter
        private String category;
        private Double monthlySalesPlan;
        private Double monthlyPurchasePlan;

        private Double dailyDirectTransactionAmount;
        private Double weeklyDirectTransactionAmount;
        private Double monthlyDirectTransactionAmount;

        private Double dailySubcontractAmount;
        private Double weeklySubcontractAmount;
        private Double monthlySubcontractAmount;

        private Double dailyTotalAmount;
        private Double weeklyTotalAmount;
        private Double monthlyTotalAmount;

        private Double dailyDirectReceiptAmount;
        private Double weeklyDirectReceiptAmount;
        private Double monthlyDirectReceiptAmount;

        private Double dailySubcontractReceiptAmount;
        private Double weeklySubcontractReceiptAmount;
        private Double monthlySubcontractReceiptAmount;

        private Double dailyTotalReceiptAmount;
        private Double weeklyTotalReceiptAmount;
        private Double monthlyTotalReceiptAmount;

        private Double dailyPendingDirectAmount;
        private Double weeklyPendingDirectAmount;
        private Double monthlyPendingDirectAmount;

        private Double dailyPendingSubcontractAmount;
        private Double weeklyPendingSubcontractAmount;
        private Double monthlyPendingSubcontractAmount;

        private Double dailyPendingTotalAmount;
        private Double weeklyPendingTotalAmount;
        private Double monthlyPendingTotalAmount;

        /**
         * <b>VPUORDAmtList</b> 및 <b>MaterialPurchasePlanMonthly</b>를 이용하여 생성자를 초기화합니다.
         *
         * @param vpuordAmtList               발주 데이터
         * @param materialPurchasePlanMonthly 월별 구매 계획 데이터
         */
        public DailyMaterialCostDTO(VPUORDAmtList vpuordAmtList,
                                    MaterialPurchasePlanMonthly materialPurchasePlanMonthly) {
            this.category = Optional.ofNullable(vpuordAmtList.getSortation()).orElse("데이터 없음");
            this.monthlySalesPlan = Double.parseDouble(Optional.ofNullable(materialPurchasePlanMonthly.getSalesPlanAmt()).orElse("0").replaceAll(",", ""));
            this.monthlyPurchasePlan = Optional.ofNullable(materialPurchasePlanMonthly.getMaterialPurchasingPlanAmt()).orElse(0.0);

            // 발주
            this.dailyDirectTransactionAmount = Optional.ofNullable(vpuordAmtList.getOrderDirectTransactionDailyAmount()).orElse(0.0);
            this.monthlyDirectTransactionAmount = Optional.ofNullable(vpuordAmtList.getOrderDirectTransactionMonthlyAmount()).orElse(0.0);

            this.dailySubcontractAmount = Optional.ofNullable(vpuordAmtList.getOrderSaidDailyAmount()).orElse(0.0);
            this.monthlySubcontractAmount = Optional.ofNullable(vpuordAmtList.getOrderSaidMonthlyAmount()).orElse(0.0) ;

            this.dailyTotalAmount = dailyDirectTransactionAmount + dailySubcontractAmount;
            this.monthlyTotalAmount = monthlyDirectTransactionAmount + monthlySubcontractAmount;

            // 입고
            this.dailyDirectReceiptAmount = Optional.ofNullable(vpuordAmtList.getReceivingDirectTransactionDailyAmount()).orElse(0.0);
            this.monthlyDirectReceiptAmount = Optional.ofNullable(vpuordAmtList.getReceivingDirectTransactionMonthlyAmount()).orElse(0.0);

            this.dailySubcontractReceiptAmount = Optional.ofNullable(vpuordAmtList.getReceiptSaidDailyAmount()).orElse(0.0);
            this.monthlySubcontractReceiptAmount = Optional.ofNullable(vpuordAmtList.getReceivingPaymentMonthlyAmount()).orElse(0.0);

            this.dailyTotalReceiptAmount = dailyDirectReceiptAmount + dailySubcontractReceiptAmount;
            this.monthlyTotalReceiptAmount = monthlyDirectReceiptAmount + monthlySubcontractReceiptAmount;

            // 납품
            this.dailyPendingDirectAmount = Optional.ofNullable(vpuordAmtList.getDeliveryDirectTransactionDailyAmount()).orElse(0.0);
            this.monthlyPendingDirectAmount = Optional.ofNullable(vpuordAmtList.getDeliveryDirectTransactionMonthlyAmount()).orElse(0.0);

            this.dailyPendingSubcontractAmount = Optional.ofNullable(vpuordAmtList.getDeliverySaidDailyAmount()).orElse(0.0);
            this.monthlyPendingSubcontractAmount = Optional.ofNullable(vpuordAmtList.getDeliverySalaryMonthlyAmount()).orElse(0.0);

            this.dailyPendingTotalAmount = dailyPendingDirectAmount + dailyPendingSubcontractAmount;
            this.monthlyPendingTotalAmount = monthlyPendingDirectAmount + monthlyPendingSubcontractAmount;
        }

        /**
         * 주별 데이터를 계산하여 초기화합니다.
         *
         * @param vpuordAmtLists 발주 데이터 리스트
         */
        public void monthly(List<VPUORDAmtList> vpuordAmtLists) {
            // 발주 관련 null 처리
            if (vpuordAmtLists != null) {
                this.weeklyDirectTransactionAmount = vpuordAmtLists.stream()
                        .mapToDouble(list-> Optional.ofNullable(list.getOrderDirectTransactionDailyAmount()).orElse(0.0))
                        .sum() + dailyDirectTransactionAmount;

                this.weeklySubcontractAmount = vpuordAmtLists.stream()
                        .mapToDouble(list -> Optional.ofNullable(list.getOrderSaidDailyAmount()).orElse(0.0)).sum() + dailySubcontractAmount;

                this.weeklyTotalAmount = weeklyDirectTransactionAmount + weeklySubcontractAmount;

                this.weeklyDirectReceiptAmount = vpuordAmtLists.stream()
                        .mapToDouble(list -> Optional.ofNullable(list.getReceivingDirectTransactionDailyAmount()).orElse(0.0)).sum()
                        + dailyDirectReceiptAmount;

                this.weeklySubcontractReceiptAmount = vpuordAmtLists.stream()
                        .mapToDouble(list -> Optional.ofNullable(list.getReceiptSaidDailyAmount()).orElse(0.0))
                        .sum() + dailySubcontractReceiptAmount;

                this.weeklyTotalReceiptAmount = weeklyDirectReceiptAmount + weeklySubcontractReceiptAmount;

                this.weeklyPendingDirectAmount = vpuordAmtLists.stream()
                        .mapToDouble( list -> Optional.ofNullable(list.getDeliveryDirectTransactionDailyAmount()).orElse(0.0)).sum()
                        + dailyPendingDirectAmount;

                this.weeklyPendingSubcontractAmount = vpuordAmtLists.stream()
                        .mapToDouble(list -> Optional.ofNullable(list.getDeliverySaidDailyAmount()).orElse(0.0))
                        .sum() + dailyPendingSubcontractAmount;
                this.weeklyPendingTotalAmount = weeklyPendingDirectAmount + weeklyPendingSubcontractAmount;
            } else {
                // vpuordAmtListDays가 null일 경우, 기존 daily 값을 그대로 사용
                this.weeklyDirectTransactionAmount = dailyDirectTransactionAmount;
                this.weeklySubcontractAmount = dailySubcontractAmount;
                this.weeklyDirectReceiptAmount = dailyDirectReceiptAmount;
                this.weeklySubcontractReceiptAmount = dailySubcontractReceiptAmount;
                this.weeklyPendingDirectAmount = dailyPendingDirectAmount;
                this.weeklyPendingSubcontractAmount = dailyPendingSubcontractAmount;
            }
        }

        /**
         * 데이터를 합산합니다.
         *
         * @param source 다른 DTO
         */
        public void merge(DailyMaterialCostDTO source) {
            this.dailyDirectTransactionAmount = sum(this.dailyDirectTransactionAmount,
                    source.getDailyDirectTransactionAmount());
            this.weeklyDirectTransactionAmount = sum(this.weeklyDirectTransactionAmount,
                    source.getWeeklyDirectTransactionAmount());
            this.monthlyDirectTransactionAmount = sum(this.monthlyDirectTransactionAmount,
                    source.getMonthlyDirectTransactionAmount());

            this.dailySubcontractAmount = sum(this.dailySubcontractAmount, source.getDailySubcontractAmount());
            this.weeklySubcontractAmount = sum(this.weeklySubcontractAmount, source.getWeeklySubcontractAmount());
            this.monthlySubcontractAmount = sum(this.monthlySubcontractAmount, source.getMonthlySubcontractAmount());

            this.dailyTotalAmount = sum(this.dailyTotalAmount, source.getDailyTotalAmount());
            this.weeklyTotalAmount = sum(this.weeklyTotalAmount, source.getWeeklyTotalAmount());
            this.monthlyTotalAmount = sum(this.monthlyTotalAmount, source.getMonthlyTotalAmount());

            this.dailyDirectReceiptAmount = sum(this.dailyDirectReceiptAmount, source.getDailyDirectReceiptAmount());
            this.weeklyDirectReceiptAmount = sum(this.weeklyDirectReceiptAmount, source.getWeeklyDirectReceiptAmount());
            this.monthlyDirectReceiptAmount = sum(this.monthlyDirectReceiptAmount,
                    source.getMonthlyDirectReceiptAmount());

            this.dailySubcontractReceiptAmount = sum(this.dailySubcontractReceiptAmount,
                    source.getDailySubcontractReceiptAmount());
            this.weeklySubcontractReceiptAmount = sum(this.weeklySubcontractReceiptAmount,
                    source.getWeeklySubcontractReceiptAmount());
            this.monthlySubcontractReceiptAmount = sum(this.monthlySubcontractReceiptAmount,
                    source.getMonthlySubcontractReceiptAmount());

            this.dailyTotalReceiptAmount = sum(this.dailyTotalReceiptAmount, source.getDailyTotalReceiptAmount());
            this.weeklyTotalReceiptAmount = sum(this.weeklyTotalReceiptAmount, source.getWeeklyTotalReceiptAmount());
            this.monthlyTotalReceiptAmount = sum(this.monthlyTotalReceiptAmount, source.getMonthlyTotalReceiptAmount());

            this.dailyPendingDirectAmount = sum(this.dailyPendingDirectAmount, source.getDailyPendingDirectAmount());
            this.weeklyPendingDirectAmount = sum(this.weeklyPendingDirectAmount, source.getWeeklyPendingDirectAmount());
            this.monthlyPendingDirectAmount = sum(this.monthlyPendingDirectAmount,
                    source.getMonthlyPendingDirectAmount());

            this.dailyPendingSubcontractAmount = sum(this.dailyPendingSubcontractAmount,
                    source.getDailyPendingSubcontractAmount());
            this.weeklyPendingSubcontractAmount = sum(this.weeklyPendingSubcontractAmount,
                    source.getWeeklyPendingSubcontractAmount());
            this.monthlyPendingSubcontractAmount = sum(this.monthlyPendingSubcontractAmount,
                    source.getMonthlyPendingSubcontractAmount());

            this.dailyPendingTotalAmount = sum(this.dailyPendingTotalAmount, source.getDailyPendingTotalAmount());
            this.weeklyPendingTotalAmount = sum(this.weeklyPendingTotalAmount, source.getWeeklyPendingTotalAmount());
            this.monthlyPendingTotalAmount = sum(this.monthlyPendingTotalAmount, source.getMonthlyPendingTotalAmount());
        }

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StockStatusDTO {
        @Setter
        private String category;
        private Double directPurchaseMaterial;
        private Double subcontractMaterial;
        private Double totalMaterial;

        /**
         * 데이터를 합산합니다.
         *
         * @param statusDTO 다른 DTO
         */
        public void merge(StockStatusDTO statusDTO) {
            this.directPurchaseMaterial = sum(statusDTO.getDirectPurchaseMaterial(), directPurchaseMaterial);
            this.subcontractMaterial = sum(statusDTO.getSubcontractMaterial(), subcontractMaterial);
            this.totalMaterial = sum(statusDTO.getTotalMaterial(), totalMaterial);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ModelPurchasePlanDTO {
        private int no;
        private String category;
        private Double monthlySalesPlan;
        private Double materialCostRatio;
        private Double monthlyPurchasePlan;
        private Double monthlyOrderAmount;
        private String remarks;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ModelReceiptStatusDTO {
        private int no;
        private String category;
        private Double directPurchaseReceipt;
        private Double subcontractReceipt;
        private Double totalMaterialReceipt;
        private Double receiptRatioMonthly;
        private String remarks;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class WarehouseMaterialStatusDTO {
        @Setter
        private String category;
        private Double wiringWaiting;
        private Double wiringInProcess;
        private Double wiringTotal;
        private Double mechanismWaiting;
        private Double mechanismInProcess;
        private Double mechanismTotal;
        private Double packingWaiting;
        private Double packingInProcess;
        private Double packingTotal;
        private Double subMaterialsWaiting;
        private Double subMaterialsInProcess;
        private Double subMaterialsTotal;
        private Double otherWaiting;
        private Double otherInProcess;
        private Double otherTotal;

        public WarehouseMaterialStatusDTO(VLGWHStockWHAmtList vlgwhStockWHAmtList) {
            this.category = vlgwhStockWHAmtList.getSortation();
            this.wiringWaiting = vlgwhStockWHAmtList.getFullLengthStandby();
            this.wiringInProcess = vlgwhStockWHAmtList.getFullLengthProcessInProgress();
            this.wiringTotal = vlgwhStockWHAmtList.getFullLengthTotal();

            this.mechanismWaiting = vlgwhStockWHAmtList.getInstrumentStandby();
            this.mechanismInProcess = vlgwhStockWHAmtList.getMechanismProcessInProgress();
            this.mechanismTotal = vlgwhStockWHAmtList.getMechanismTotal();

            this.packingWaiting = vlgwhStockWHAmtList.getPackagingStandby();
            this.packingInProcess = vlgwhStockWHAmtList.getPackagingProcessInProgress();
            this.packingTotal = vlgwhStockWHAmtList.getPackagingTotal();

            this.subMaterialsWaiting = vlgwhStockWHAmtList.getSubsidiaryMaterialStandby();
            this.subMaterialsInProcess = vlgwhStockWHAmtList.getSubMaterialsProcessing();
            this.subMaterialsTotal = vlgwhStockWHAmtList.getSubMaterialsTotal();

            this.otherWaiting = vlgwhStockWHAmtList.getOtherStandby();
            this.otherInProcess = vlgwhStockWHAmtList.getOtherProcessInProgress();
            this.otherTotal = vlgwhStockWHAmtList.getOtherTotal();
        }
    }
}
