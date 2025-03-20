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
@Table(name = "worker_retention")
public class WorkerRetention extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "WorkerRetention_id")
	private Long id;

	// 작업일
	private String workDate;

	// 부서
	private String department;

	// 워크센터
	private String workshopId;

	// 총원
	private Double totPerson;

	// 직접인원
	private Double prodPerson;

	// 간접인원
	private Double managePerson;

	// 실투입간접인원
	private Double prodManagePerson;

	// 정상인원
	private Double workPerson;

	// 정상인원근무시간
	private Double workTime;

	// 가동율산정시간
	private Double rateTime;

	// 외부지원인원
	private Double addWorkPerson;

	// 외부지원인원시간(분)
	private Double addWorkTime;

	// 기타인원
	private Double etcPerson;

	// 기타인원시간(분)
	private Double etcTime;

	// 직접인원 연장
	private Double overWorkPerson;

	// 직접인원연장시간(분)
	private Double overWorkTime;

	// 간접인원연장
	private Double overManagePerson;

	// 간접인원연장시간(분)
	private Double overManageTime;

	// 외부지원인원연장
	private Double overAddPerson;

	// 외부지원인원연장시간(분)
	private Double overAddTime;

	// 간접 연차 인원
	private Double invalidManagePerson;

	// 간접 연차 시간
	private Double invalidManageTime;

	// 간접 시간차
	private Double invalidAddPerson;

	// 간접 시간차 시간
	private Double invalidAddTime;

	// 휴가인원
	private Double managePersonYearly;

	// 휴가계(분)
	private Double managePersonYearlyHour;

	// 직접인원 시간차
	private Double managePersonPartTime;

	// 직업지원 시간차 시간
	private Double managePersonPartTimeHour;

	// 병가인원
	private Double wkPercntHoli;

	// 병가소계(분)
	private Double wkPercntHoliTime;

	// 교육인원
	private Double prodPersonPartTime;

	// 교육인원소계(분)
	private Double prodPersonPartTimeHour;

	// 결근인원
	private Double wkPercntSick;

	// 결근인원소계(분)
	private Double wkPercntSickTime;

	// 지각인원
	private Double wkPercntEdu;

	// 지각인원소계(분)
	private Double wkPercntEduTime;

	// 조퇴인원
	private Double wkPercntAbsent;

	// 조퇴소계(분)
	private Double wkPercntAbsentTime;

	// 외출인원
	private Double wkPercntLate;

	// 외출소계(분)
	private Double wkPercntLateTime;

	// 기타인원
	private Double wkPercntEarly;

	// 기타소계(분)
	private Double wkPercntEarlyTime;

	// 근무공수(분)
	private Double workManHour;

	// 총보유공수(초)
	private Double totManHour;

	//00시 마무리 데이터
	private boolean snapShot;
}
