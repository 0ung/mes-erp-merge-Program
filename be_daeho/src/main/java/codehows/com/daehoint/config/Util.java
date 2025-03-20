package codehows.com.daehoint.config;

import codehows.com.daehoint.mapper.DTOMapper;
import codehows.com.daehoint.entity.MainProductionReport;
import codehows.com.daehoint.entity.ProcessProductionReport;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Function;

public class Util {

    /**
     * 클래스의 특정 데이터를 안전하게 추출하며, null이거나 예외 발생 시 기본값을 반환하는 메서드.
     *
     * @param <T>          입력 객체의 타입. 예: 클래스(ProductionDailyList)나 기타 데이터 타입.
     * @param <R>          반환 데이터의 타입. 예: Double, String, Integer 등.
     * @param object       데이터를 추출할 객체. null 체크를 수행합니다.
     * @param getter       데이터를 추출하기 위한 람다식 또는 메서드 참조. 예: `pd -> pd.getQty()`.
     * @param defaultValue null이거나 예외 발생 시 반환할 기본값.
     * @return getter를 통해 추출한 데이터. 객체가 null이거나 예외가 발생하면 기본값을 반환.
     * <p>
     * 예제:
     * Double qty = checkNullAndSetDefault(productionDailyList, pd -> Double.parseDouble(pd.getQty()), 0.0);
     */
    public static <T, R> R checkNullAndSetDefault(T object, Function<T, R> getter, R defaultValue) {
        return Optional.ofNullable(object)
                .map(getter)
                .orElse(defaultValue);
    }

    /**
     * 현재 시간을 "시(Hour)" 단위로 반환합니다.
     *
     * @return 현재 시간의 "시(Hour)" 단위 {@link LocalDateTime}
     */
    public static LocalDateTime getTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
    }

    /**
     * "년-월" 형식("yyyy-MM")의 날짜 포맷터를 반환합니다.
     *
     * @return "yyyy-MM" 형식의 {@link SimpleDateFormat}
     */
    public static SimpleDateFormat getMonth() {
        return new SimpleDateFormat("yyyy-MM");
    }


    /**
     * 공정 생산일보 데이터를 병합합니다.
     *
     * @param manage 공수 투입 관리 데이터
     * @param cost   생산 비용 데이터
     * @return 병합된 공정 생산일보
     */
    public static ProcessProductionReport mergeProcessProductionReport(ProcessProductionReport manage, ProcessProductionReport cost) {
        ProcessProductionReport.ProcessProductionReportBuilder builder = ProcessProductionReport.builder();

        try {
            for (Field field : ProcessProductionReport.class.getDeclaredFields()) {
                field.setAccessible(true); // private 필드 접근 허용
                Object value = field.get(manage) != null ? field.get(manage) : field.get(cost);

                Field builderField = ProcessProductionReport.ProcessProductionReportBuilder.class.getDeclaredField(field.getName());
                builderField.setAccessible(true);
                builderField.set(builder, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error merging ProcessProductionReport", e);
        }

        return builder.build();
    }

    /**
     * 메인 생산일보 데이터를 병합합니다.
     *
     * @param first  공수 투입 관리 데이터
     * @param second 생산 비용 데이터
     * @return 병합된 공정 생산일보
     */
    public static MainProductionReport mergeMainProductionReport(MainProductionReport first, MainProductionReport second) {
        MainProductionReport.MainProductionReportBuilder builder = MainProductionReport.builder();

        try {
            for (Field field : MainProductionReport.class.getDeclaredFields()) {
                field.setAccessible(true); // private 필드 접근 허용
                Object value = field.get(first) != null ? field.get(first) : field.get(second);

                Field builderField = MainProductionReport.MainProductionReportBuilder.class.getDeclaredField(field.getName());
                builderField.setAccessible(true);
                builderField.set(builder, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error merging ProcessProductionReport", e);
        }

        return builder.build();
    }

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static DTOMapper mapper = DTOMapper.INSTANCE;
}
