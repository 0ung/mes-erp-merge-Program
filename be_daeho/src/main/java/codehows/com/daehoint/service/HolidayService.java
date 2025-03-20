package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.HolidayAPIResponse;
import codehows.com.daehoint.entity.Holidays;
import codehows.com.daehoint.repository.HolidayRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * 공휴일 관리 서비스 클래스
 *
 * <p>이 클래스는 공휴일 데이터를 관리하기 위한 서비스입니다.
 * 공공 API를 사용하여 공휴일 데이터를 동기화하고, 주말 데이터를 계산하여 데이터베이스에 저장합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>공공 API를 통한 공휴일 데이터 동기화</li>
 *   <li>현재 연도 및 월의 주말 데이터 계산 및 저장</li>
 * </ul>
 *
 * <p>의존성:</p>
 * <ul>
 *   <li>{@code HolidayRepo}: 공휴일 데이터를 관리하는 레포지토리</li>
 * </ul>
 *
 * <p>참고:</p>
 * <ul>
 *   <li>공공 API 호출 시 서비스 키와 URL은 애플리케이션 설정 파일에서 관리됩니다.</li>
 *   <li>주말 데이터를 계산하여 자동으로 데이터베이스에 저장합니다.</li>
 *   <li>API에서 반환된 날짜는 변환 작업을 통해 LocalDate로 처리됩니다.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class HolidayService {

	private final HolidayRepo holidayRepo;

	@Value("${holiday.url}")
	private String url;

	@Value("${holiday.serviceKey}")
	private String serviceKey;

	@Transactional
	public void createHolidayDataWithApi() {
		int year = LocalDate.now().getYear();
		String month = String.format("%02d", LocalDate.now().getMonthValue());  // 월을 2자리로 형식화
		System.out.println(serviceKey);
		URI requestUrl = URI.create(UriComponentsBuilder.fromHttpUrl(url)
			.queryParam("serviceKey", serviceKey)
			.queryParam("solYear", year)
			.queryParam("solMonth", month)
			.queryParam("_type", "json")
			.build()
			.toUriString());
		System.out.println(requestUrl);

		WebClient webClient = WebClient.create();

		webClient.get()
			.uri(requestUrl)
			.retrieve()
			.bodyToMono(HolidayAPIResponse.class)  // JSON 데이터를 문자열로 받음
			.subscribe(data -> {
				List<HolidayAPIResponse.Item> items = data.getResponse().getBody().getItems().getItem();

				for (HolidayAPIResponse.Item item : items) {
					// holidayRepo에 저장
					holidayRepo.save(Holidays.builder()
						.holidayName(item.getDateName())
						.holidayDate(convertToDate(item.getLocdate()))  // 날짜를 적절히 변환해야 할 수도 있음
						.build());
				}
			}, error -> {
				System.err.println("Error: " + error.getMessage());
			});
	}

	public void createHolidays() {
		// 현재 날짜 기준으로 년, 월 가져오기
		LocalDate currentDate = LocalDate.now();
		int year = currentDate.getYear();
		int month = currentDate.getMonthValue();

		// 해당 연도와 월의 주말 데이터 계산 및 저장
		YearMonth yearMonth = YearMonth.of(year, month);
		LocalDate firstDay = yearMonth.atDay(1);
		LocalDate lastDay = yearMonth.atEndOfMonth();

		// 첫째 날부터 마지막 날까지 순회
		for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
			DayOfWeek dayOfWeek = date.getDayOfWeek();

			// 토요일과 일요일을 확인
			if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
				String holidayName = (dayOfWeek == DayOfWeek.SATURDAY) ? "토요일" : "일요일";

				// 주말 데이터를 holidayRepo에 저장
				holidayRepo.save(Holidays.builder()
					.holidayName(holidayName)
					.holidayDate(date)  // LocalDate 저장
					.build());
			}
		}
	}

	private LocalDate convertToDate(long locdate) {
		String dateStr = String.valueOf(locdate);  // long 값을 문자열로 변환
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		// 문자열을 LocalDate로 변환
		return LocalDate.parse(dateStr, formatter);
	}

}
