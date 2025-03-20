package codehows.com.daehoint.entity.mes;

import codehows.com.daehoint.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "daily_work_loss")
public class DailyWorkLoss extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DailyWorkLoss_id")
	private Long id;

	@Setter
	private String category;

	// 발생일자
	private String lossEffectDate;

	// 작업지시번호
	private String lotNo;

	// 발생부서
	private String lossEffectDept;

	// 유실시간(분)
	private Double lossTime;

	// 투입인원
	private Double lossWorker;

	// 총합계시간(분)
	private Double lossTimeTotal;

	// 사유구분
	private String lossReason;

	// 세부내용
	private String lossContents;

	// 조치내용
	private String lossMeasure;

	// 발생금액
	private Double lossAmount;

	// 책임부서 1
	private String lossBlameDept01;

	// 책임비율1
	private Double lossRate01;

	// 책임부서 2
	private String lossBlameDept02;

	// 책임비율2
	private Double lossRate02;

	// 책임부서 3
	private String lossBlameDept03;

	// 책임비율3
	private Double lossRate03;

	// 진행상태
	private String stateProgressing;

	// 비고
	private String remark;
	//00시 마무리 데이터
	private boolean snapShot;

	public void updateDailyWorkLoss(String category){
		this.category = category;
	}
}
