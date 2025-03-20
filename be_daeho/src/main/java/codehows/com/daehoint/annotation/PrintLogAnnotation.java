package codehows.com.daehoint.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>PrintLogAnnotation</b>
 * <p></p>
 * 이 어노테이션은 인쇄 요청 이벤트를 감지하고 관련 로그를 기록하기 위해 사용됩니다. <br/>
 *
 * <p><b>주요 기능:</b></p>
 * - 인쇄 작업 시 사용자의 IP 주소, 사용자 ID, 인쇄 페이지 정보를 기록. <br/>
 * - `LoggingAspect` 클래스에서 이 어노테이션을 감지하여 동작 수행. <br/>
 *
 * <p><b>적용 대상:</b></p>
 * - 메서드에 적용되며, 인쇄 요청과 관련된 로직에서 사용. <br/>
 *
 * <p><b>속성:</b></p>
 * - `printPage()`: 인쇄된 페이지 정보를 지정. <br/>
 *
 * <p><b>메타 정보:</b></p>
 * - `@Target(ElementType.METHOD)`: 메서드에만 적용 가능. <br/>
 * - `@Retention(RetentionPolicy.RUNTIME)`: 런타임 시점에 어노테이션 정보 유지. <br/>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrintLogAnnotation {
	String printPage();
}
