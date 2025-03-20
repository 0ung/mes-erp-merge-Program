package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.ProcessProductionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProcessProductionReportRepo extends JpaRepository<ProcessProductionReport, Long> {
	ProcessProductionReport findByCategoryAndCreateDateTimeAfterAndSnapShot(String category, LocalDateTime date,
		Boolean isSnapShot);

	@Query("SELECT p FROM ProcessProductionReport p WHERE p.category = :category AND p.createDateTime BETWEEN :startDate AND :endDate AND p.snapShot = :isSnapShot")
	ProcessProductionReport findByAvailableHour(
		@Param("category") String category,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		@Param("isSnapShot") Boolean isSnapShot
	);

	List<ProcessProductionReport> findByCreateDateTimeAfterAndSnapShot(LocalDateTime date, Boolean isSnapShot);

	List<ProcessProductionReport> findByCreateDateTimeBetweenAndSnapShotAndCategory(LocalDateTime startTime,
		LocalDateTime endTime,
		boolean snapshot, String category);
}
