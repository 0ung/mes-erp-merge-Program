package codehows.com.daehoint.constants;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * <b>Rank 열거형</b><br>
 * 프로젝트에서 관리되는 직급 정보를 정의하는 열거형 클래스입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - 각 직급에 대한 한글 이름(`koreanName`)을 제공.<br><br>
 *
 * <b>구성 요소:</b><br>
 * 1. <b>`CEO`</b>: 대표이사.<br>
 * 2. <b>`EXECUTIVE_DIRECTOR`</b>: 전무.<br>
 * 3. <b>`DIRECTOR`</b>: 이사.<br>
 * 4. <b>`GENERAL_MANAGER`</b>: 실장.<br>
 * 5. <b>`TEAM_LEADER`</b>: 팀장.<br>
 * 6. <b>`PRO`</b>: 프로.<br>
 * 7. <b>`PRO_PL`</b>: 프로(PL).<br>
 * 8. <b>`STAFF`</b>: 사원.<br>
 * 9. <b>`SENIOR_RESEARCHER`</b>: 수석연구원.<br>
 * 10. <b>`PRINCIPAL_RESEARCHER`</b>: 책임연구원.<br>
 * 11. <b>`RESEARCHER_LEAD`</b>: 선임연구원.<br>
 * 12. <b>`RESEARCHER`</b>: 연구원.<br><br>
 *
 * <b>메서드:</b><br>
 * - <b>`getKoreanName()`</b>:<br>
 *   - 각 직급의 한글 이름을 반환.<br><br>
 *
 * <b>사용 예:</b><br>
 * - 직급에 따라 UI를 표시하거나 특정 로직을 처리할 때 사용.<br>
 */
public enum Rank {
	CEO("대표이사"),
	EXECUTIVE_DIRECTOR("전무"),
	DIRECTOR("이사"),
	GENERAL_MANAGER("실장"),
	TEAM_LEADER("팀장"),
	PRO("프로"),
	PRO_PL("프로(PL)"),
	STAFF("사원"),
	SENIOR_RESEARCHER("수석연구원"),
	PRINCIPAL_RESEARCHER("책임연구원"),
	RESEARCHER_LEAD("선임연구원"),
	RESEARCHER("연구원");

	private final String koreanName;

	Rank(String koreanName) {
		this.koreanName = koreanName;
	}

	@JsonValue
	public String getKoreanName() {
		return koreanName;
	}
}
