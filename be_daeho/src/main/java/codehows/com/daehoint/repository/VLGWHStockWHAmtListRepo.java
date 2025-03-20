package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.erp.VLGWHStockWHAmtList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VLGWHStockWHAmtListRepo extends JpaRepository<VLGWHStockWHAmtList, Long> {
	List<VLGWHStockWHAmtList> findByCreateDateTimeAfterAndSnapShot(LocalDateTime dateTime, boolean isSnapShot);

	List<VLGWHStockWHAmtList> findByCreateDateTimeBetweenAndSnapShot(LocalDateTime createDate, LocalDateTime endDate,
		boolean snapshot);
}
