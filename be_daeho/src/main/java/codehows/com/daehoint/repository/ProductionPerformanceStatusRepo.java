package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.ProductionPerformanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductionPerformanceStatusRepo extends JpaRepository<ProductionPerformanceStatus, Long> {

	List<ProductionPerformanceStatus> findByCategoryAndCreateDateTimeAfterAndSnapShot(String category,
		LocalDateTime creatTime, Boolean snapShot);

	List<ProductionPerformanceStatus> findByCreateDateTimeAfterAndSnapShot(LocalDateTime creatTime, Boolean snapShot);

	List<ProductionPerformanceStatus> findByLotNoAndCreateDateTimeAfterAndSnapShot(String lotNo, LocalDateTime dateTime,
		boolean isSnapShot);

	List<ProductionPerformanceStatus> findByCreateDateTimeBetweenAndSnapShotAndCategory(LocalDateTime createDate,
		LocalDateTime endDate, boolean snapshot, String category);

	List<ProductionPerformanceStatus> findByLotNoAndCreateDateTimeBetweenAndSnapShot(String lotNo,
		LocalDateTime createDate, LocalDateTime endDate, boolean snapshot);
}
