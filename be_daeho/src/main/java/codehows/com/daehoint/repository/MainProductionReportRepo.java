package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.MainProductionReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MainProductionReportRepo extends JpaRepository<MainProductionReport, Long> {
	MainProductionReport findByCreateDateTimeAfterAndSnapShot(LocalDateTime date, Boolean isSnapShot);

	List<MainProductionReport> findByCreateDateTimeBetweenAndSnapShot(LocalDateTime createDateTime,
		LocalDateTime createDateTime2, boolean snapShot);
}
