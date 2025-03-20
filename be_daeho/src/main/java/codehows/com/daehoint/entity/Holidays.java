package codehows.com.daehoint.entity;

import codehows.com.daehoint.dto.HolidayResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "holidays")
public class Holidays {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "holiday_id")
	private Long id;

	private LocalDate holidayDate;

	private String holidayName;

	public Holidays(LocalDate holidayDate, String holidayName) {
		this.holidayDate = holidayDate;
		this.holidayName = holidayName;
	}

	public void updateHoliday(HolidayResponse holidayResponse) {
		this.holidayDate = holidayResponse.getHolidayDate();
		this.holidayName = holidayResponse.getHolidayName();
	}
}
