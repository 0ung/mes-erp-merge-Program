package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.erp.VPUORDAmtList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VPUORDAmtListDayRepo extends JpaRepository<VPUORDAmtList, Long> {

	List<VPUORDAmtList> findByCreateDateTimeAfterAndSnapShot(LocalDateTime dateTime, boolean isSnapShot);

	List<VPUORDAmtList> findByCreateDateTimeBetweenAndSnapShot(LocalDateTime startDate, LocalDateTime endDate,
                                                               boolean snapshot);
}
