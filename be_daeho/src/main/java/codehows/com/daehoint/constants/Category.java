package codehows.com.daehoint.constants;

import lombok.Getter;

/**
 * <b>Category 열거형</b><br>
 * 프로젝트에서 관리되는 공정 카테고리를 정의하는 열거형 클래스입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - 각 카테고리에 대한 설명(`description`)을 제공.<br><br>
 *
 * <b>구성 요소:</b><br>
 * 1. <b>`SM`</b>: SM ASSY.<br>
 * 2. <b>`IM`</b>: IM ASSY.<br>
 * 3. <b>`DIP`</b>: DIP ASSY.<br>
 * 4. <b>`MANUAL`</b>: MANUAL ASSY.<br>
 * 5. <b>`PCB`</b>: PCB ASSY.<br>
 * 6. <b>`CASE`</b>: CASE ASSY.<br>
 * 7. <b>`ACCY`</b>: ACCY.<br>
 * 8. <b>`PACKING`</b>: PACKING ASSY.<br><br>
 *
 * <b>메서드:</b><br>
 * - <b>`getDescription()`</b>:<br>
 *   - 각 카테고리의 설명을 반환.<br><br>
 *
 */
@Getter
public enum Category {
    SM("SM ASSY"),
    IM("IM ASSY"),
    DIP("DIP ASSY"),
    MANUAL("MANUAL ASSY"),
    PCB("PCB ASSY"),
    CASE("CASE ASSY"),
    ACCY("ACCY"),
    PACKING("PACKING ASSY");

    private final String description;

    Category(String description) {
        this.description = description;
    }
}
