package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.erp.VLGWHStockAmtList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VLGWHStockAmtListRepo extends JpaRepository<VLGWHStockAmtList, Long> {
	List<VLGWHStockAmtList> findByCreateDateTimeAfterAndSnapShot(LocalDateTime dateTime, boolean isSnapShot);

	Optional<VLGWHStockAmtList> findTopByOrderByIdDesc();

	List<VLGWHStockAmtList> findByCreateDateTimeBetweenAndSnapShot(LocalDateTime startDate, LocalDateTime endDate,
		boolean snapshot);
}
