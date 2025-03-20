package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.mes.ProductionDailyList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductionDailyListRepo extends JpaRepository<ProductionDailyList, Long> {


	List<ProductionDailyList> findByCreateDateTimeAfterAndSnapShot(LocalDateTime createDate, Boolean snapShot);

	List<ProductionDailyList> findByCreateDateTimeAfterAndGubunNameAndItemNameAndSnapShot(LocalDateTime createDate,String gubunName,String itemName ,Boolean snapShot);

	List<ProductionDailyList> findByCreateDateTimeAfterAndStartTimeAndGubunName(LocalDateTime createDate,
		String startTime, String gubunName);

	List<ProductionDailyList> findByCreateDateTimeBetweenAndSnapShot(LocalDateTime createDate, LocalDateTime endDate,
		boolean snapshot);

	List<ProductionDailyList> findByCreateDateTimeBetweenAndSnapShotAndCategoryItemValue03(LocalDateTime createDate,
		LocalDateTime endDate,
		boolean snapshot, String modelName);
	List<ProductionDailyList> findByCreateDateTimeBetweenAndSnapShotAndCategoryItemValue03AndItemName(LocalDateTime createDate, LocalDateTime endDate,boolean snapshot,String modelName, String processName);
	List<ProductionDailyList> findByCreateDateTimeBetweenAndSnapShotAndItemName(LocalDateTime createDate, LocalDateTime endDate,boolean snapshot, String processName);
}
