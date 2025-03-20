package codehows.com.daehoint.dto;

import codehows.com.daehoint.entity.Holidays;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HolidayResponse {

	private Long id;
	private LocalDate holidayDate;
	private String holidayName;

	public static Holidays to(HolidayResponse holidayResponse) {
		return new Holidays(holidayResponse.getHolidayDate(), holidayResponse.getHolidayName());
	}

	public static HolidayResponse to(Holidays holidays) {
		return new HolidayResponse(holidays.getId(), holidays.getHolidayDate(), holidays.getHolidayName());
	}

}
