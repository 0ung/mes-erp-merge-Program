package codehows.com.daehoint.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>LoginLogAnnotation</b>
 * <p></p>
 * 이 어노테이션은 사용자의 로그인 이벤트를 감지하고 로그를 기록하기 위해 사용됩니다. <br/>
 *
 * <p><b>주요 기능:</b></p>
 * - 로그인 시 사용자의 IP 주소와 사용자 ID를 기록. <br/>
 * - `LoggingAspect` 클래스에서 이 어노테이션을 감지하여 동작 수행.<br/>
 *
 * <p><b>적용 대상:</b></p>
 * - 메서드에 적용되며, 로그인 이벤트와 관련된 로직에서 사용. <br/>
 *
 * <p><b>메타 정보:</b></p>
 * - `@Target(ElementType.METHOD)`: 메서드에만 적용 가능. <br/>
 * - `@Retention(RetentionPolicy.RUNTIME)`: 런타임 시점에 어노테이션 정보 유지. <br/>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginLogAnnotation {
}
