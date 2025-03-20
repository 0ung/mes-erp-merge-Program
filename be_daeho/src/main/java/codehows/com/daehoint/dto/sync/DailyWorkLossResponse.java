package codehows.com.daehoint.dto.sync;

import lombok.Data;

@Data
public class DailyWorkLossResponse {
	// 발생일자
	private String LossEffectDate;

	// 작업지시번호
	private String LotNo;

	// 발생부서
	private String LossEffectDept;

	// 유실시간(분)
	private Double LossTime;

	// 투입인원
	private Double LossWorker;

	// 총합계시간(분)
	private Double LossTimeTotal;

	// 사유구분
	private String LossReason;

	// 세부내용
	private String LossContents;

	// 조치내용
	private String LossMeasure;

	// 발생금액
	private Double LossAmount;

	// 책임부서 1
	private String LossBlameDept01;

	// 책임비율1
	private Double LossRate01;

	// 책임부서 2
	private String LossBlameDept02;

	// 책임비율2
	private Double LossRate02;

	// 책임부서 3
	private String LossBlameDept03;

	// 책임비율3
	private Double LossRate03;

	// 진행상태0
	private String StateProgressing;

	// 비고
	private String Remark;

}
