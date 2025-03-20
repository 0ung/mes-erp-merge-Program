package codehows.com.daehoint.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <b>AccessLogAnnotation</b>
 * <p></p>
 * 이 어노테이션은 특정 메소드 호출 시 접근 로그를 기록하기 위해 사용됩니다. <br>
 * 접근한 페이지 이름을 지정하여 로깅에 활용할 수 있습니다. <br>
 *
 * <p><b>사용 방법:</b></p>
 * - 메소드에 이 어노테이션을 추가하고 `accessPage` 값을 설정합니다. <br>
 * - 설정된 값은 로깅 로직에서 접근 페이지 이름으로 활용됩니다. <br>
 *
 * <p><b>구성:</b></p>
 * - `@Target(ElementType.METHOD)` : 메소드에만 적용 가능. <br>
 * - `@Retention(RetentionPolicy.RUNTIME)` : 런타임에 어노테이션 정보를 유지. <br>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLogAnnotation {

	String accessPage();
}
