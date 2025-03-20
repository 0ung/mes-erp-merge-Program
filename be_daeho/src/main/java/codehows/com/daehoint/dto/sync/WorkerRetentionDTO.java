package codehows.com.daehoint.dto.sync;

import lombok.Data;

@Data
public class WorkerRetentionDTO {
	// 작업일
	private String WorkDate;

	// 부서
	private String Department;

	// 워크센터
	private String WorkshopId;

	// 총원
	private Double TotPerson;

	// 직접인원
	private Double ProdPerson;

	// 간접인원
	private Double ManagePerson;

	// 실투입간접인원
	private Double ProdManagePerson;

	// 정상인원
	private Double WorkPerson;

	// 정상인원근무시간
	private Double WorkTime;

	// 가동율산정시간
	private Double RateTime;

	// 외부지원인원
	private Double AddWorkPerson;

	// 외부지원인원시간(분)
	private Double AddWorkTime;

	// 기타인원
	private Double EtcPerson;

	// 기타인원시간(분)
	private Double EtcTime;

	// 직접인원 연장
	private Double OverWorkPerson;

	// 직접인원연장시간(분)
	private Double OverWorkTime;

	// 간접인원연장
	private Double OverManagePerson;

	// 간접인원연장시간(분)
	private Double OverManageTime;

	// 외부지원인원연장
	private Double OverAddPerson;

	// 외부지원인원연장시간(분)
	private Double OverAddTime;

	// 간접 연차 인원
	private Double InvalidManagePerson;

	// 간접 연차 시간
	private Double InvalidManageTime;

	// 간접 시간차
	private Double InvalidAddPerson;

	// 간접 시간차 시간
	private Double InvalidAddTime;

	// 휴가인원
	private Double ManagePersonYearly;

	// 휴가계(분)
	private Double ManagePersonYearlyHour;

	// 직접인원 시간차
	private Double ManagePersonPartTime;

	// 직업지원 시간차 시간
	private Double ManagePersonPartTimeHour;

	// 병가인원
	private Double WkPercntHoli;

	// 병가소계(분)
	private Double WkPercntHoliTime;

	// 교육인원
	private Double ProdPersonPartTime;

	// 교육인원소계(분)
	private Double ProdPersonPartTimeHour;

	// 결근인원
	private Double WkPercntSick;

	// 결근인원소계(분)
	private Double WkPercntSickTime;

	// 지각인원
	private Double WkPercntEdu;

	// 지각인원소계(분)
	private Double WkPercntEduTime;

	// 조퇴인원
	private Double WkPercntAbsent;

	// 조퇴소계(분)
	private Double WkPercntAbsentTime;

	// 외출인원
	private Double WkPercntLate;

	// 외출소계(분)
	private Double WkPercntLateTime;

	// 기타인원
	private Double WkPercntEarly;

	// 기타소계(분)
	private Double WkPercntEarlyTime;

	// 근무공수(분)
	private Double WorkManHour;

	// 총보유공수(초)
	private Double TotManHour;

	//???
	private Double LOSSEFFECTDATEFR;

}
