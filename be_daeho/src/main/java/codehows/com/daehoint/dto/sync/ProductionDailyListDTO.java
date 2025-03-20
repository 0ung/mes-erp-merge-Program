package codehows.com.daehoint.dto.sync;

import lombok.Data;

@Data
public class ProductionDailyListDTO {

	// 생산시작일
	private String StartTime;

	// 작업지시번호
	private String LotId;

	// MODEL명
	private String CategoryItemValue03;

	//생산팀
	private String department;

	//대호코드
	private String itemCd;

	//공정명
	private String ItemName;

	// 계획수량
	private String Qty;

	// 투입수량
	private String InQty;

	// 완료수량
	private String OutQty;

	// 불량수량
	private String DefectQty;

	// 생산수량구분
	private String GubunName;

	// 08:00~09:00
	private String Time01;

	// 09:00~10:00
	private String Time02;

	// 10:10~11:10
	private String Time03;

	// 11:10~12:20
	private String Time04;

	// 13:10~14:10
	private String Time05;

	// 14:10~15:00
	private String Time06;

	// 15:10~16:10
	private String Time07;

	// 16:10~17:10
	private String Time08;

	// 17:10~18:00
	private String Time09;

	// 18:20~19:20
	private String Time10;

	// 19:20~20:20
	private String Time11;

	// 진행상태
	private String LotState;

}
