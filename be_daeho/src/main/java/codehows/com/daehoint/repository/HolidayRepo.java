package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.Holidays;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HolidayRepo extends JpaRepository<Holidays,Long> {
	boolean existsByHolidayDate(LocalDate holiday);
}
