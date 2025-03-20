package codehows.com.daehoint.dto.sync;

import lombok.Data;

@Data
public class VPUORDAmtListDayDTO {
	//구분
	private String sortation;
	//발주-직거래-일간금액
	private Double orderDirectTransactionDailyAmount;
	//발주-직거래-월간금액
	private Double orderDirectTransactionMonthlyAmount;
	//발주-사급-일간금액
	private Double orderSaidDailyAmount;
	//발주-사급-월간금액
	private Double orderSaidMonthlyAmount;
	//입고-직거래-일간금액
	private Double receivingDirectTransactionDailyAmount;
	//입고-직거래-월간금액
	private Double receivingDirectTransactionMonthlyAmount;
	//입고-사급-일간금액
	private Double receiptSaidDailyAmount;
	//입고-직거래-월간금액
	private Double receivingPaymentMonthlyAmount;
	//납품-직거래-일간금액
	private Double deliveryDirectTransactionDailyAmount;
	//납품-직거래-월간금액
	private Double deliveryDirectTransactionMonthlyAmount;
	//납품-사급-일간금액
	private Double deliverySaidDailyAmount;
	//납품-사급-월간금액
	private Double deliverySalaryMonthlyAmount;

}
