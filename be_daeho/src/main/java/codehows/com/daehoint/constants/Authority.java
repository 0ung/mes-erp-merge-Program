package codehows.com.daehoint.constants;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * <b>Authority 열거형</b><br>
 * 프로젝트에서 사용되는 사용자 권한을 정의하는 열거형 클래스입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - 각 권한(Role)에 해당하는 한글 이름(`koreanName`)을 제공.<br>
 * - JSON 직렬화 시 한글 이름(`koreanName`)을 반환하도록 설정.<br>
 * - `toString()` 메서드를 오버라이드하여 열거형 이름에서 "ROLE_" 접두사를 제거한 값을 반환.<br><br>
 *
 * <b>구성 요소:</b><br>
 * 1. <b>`ROLE_A`</b>: A 권한.<br>
 * 2. <b>`ROLE_B`</b>: B 권한.<br>
 * 3. <b>`ROLE_C`</b>: C 권한.<br>
 * 4. <b>`ROLE_ADMIN`</b>: 관리자 권한.<br><br>
 *
 * <b>메서드:</b><br>
 * - <b>`getKoreanName()`</b>:<br>
 *   - 권한의 한글 이름을 반환.<br>
 *   - JSON 직렬화 시 반환 값으로 사용.<br>
 * - <b>`toString()`</b>:<br>
 *   - 열거형 이름에서 "ROLE_" 접두사를 제거한 문자열을 반환.<br><br>
 *
 */
public enum Authority{
	ROLE_A("A"), ROLE_B("B"),

	ROLE_C("C"), ROLE_ADMIN("관리자");

	private final String koreanName;

	Authority(String koreanName) {
		this.koreanName = koreanName;
	}

	@JsonValue
	public String getKoreanName(){
		return koreanName;
	}
	@Override
	public String toString(){
		return this.name().substring(5);
	}
}
