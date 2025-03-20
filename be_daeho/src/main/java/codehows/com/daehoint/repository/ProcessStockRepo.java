package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.ProcessStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProcessStockRepo extends JpaRepository<ProcessStock, Long> {
	List<ProcessStock> findByCreateDateTimeAfterAndSnapShot(LocalDateTime dateTime, boolean isSnapShot);

	List<ProcessStock> findBySnapShot(boolean isSnapShot);

	List<ProcessStock> findByCreateDateTimeBetweenAndSnapShot(LocalDateTime startDate, LocalDateTime endDate,
		boolean snapshot);
}
