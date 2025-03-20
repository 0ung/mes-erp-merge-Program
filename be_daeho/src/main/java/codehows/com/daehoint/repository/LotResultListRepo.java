package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.mes.LotResultList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LotResultListRepo extends JpaRepository<LotResultList, Long> {

	//일자별로 불러오기
	List<LotResultList> findByResultDateAndItemNameAndCreateDateTimeAfterAndSnapShot(String resultDate,
		String itemName, LocalDateTime createDateTime, Boolean snapShot);

	List<LotResultList> findByLotIdAndCreateDateTimeAfterAndSnapShot(String lotId, LocalDateTime createDate,
		boolean isSnapShot);

	List<LotResultList> findByCreateDateTimeAfterAndSnapShot(LocalDateTime createDate, boolean isSnapShot);

	@Query("SELECT SUM(l.outQty) FROM LotResultList l WHERE l.itemCd = :itemCd " +
		"AND l.resultDate LIKE CONCAT(:date, '%') AND l.createDateTime > :createDateTime and  l.snapShot = :snapShot")
	Long sumByItemCd(@Param("itemCd") String itemCd, @Param("date") String date,
		@Param("createDateTime") LocalDateTime localDateTime, @Param("snapShot") Boolean snapShot);

	List<LotResultList> findByProductionRequestNoAndSnapShotAndCreateDateTimeAfter(String productionRequestNo,
		boolean isSnapshot, LocalDateTime dateTime);

	List<LotResultList> findByCreateDateTimeBetweenAndSnapShot(LocalDateTime createDate, LocalDateTime endDate,
		boolean isSnapShot);

	List<LotResultList> findByCreateDateTimeBetweenAndSnapShotAndCategoryItemValue02(LocalDateTime createDate,
		LocalDateTime endDate,
		boolean isSnapShot, String cateItemValue02);

	List<LotResultList> findByCreateDateTimeBetweenAndSnapShotAndItemCd(LocalDateTime createDate, LocalDateTime endDate,
		boolean isSnapShot, String itemCd);

	List<LotResultList> findByCreateDateTimeBetweenAndSnapShotAndCategoryItemValue02AndItemCd(LocalDateTime createDate,
		LocalDateTime endDate,
		boolean isSnapShot, String CategoryItemValue02, String itemCd);

	List<LotResultList> findByProductionRequestNoAndSnapShotAndCreateDateTimeBetween(String productionRequestNo,
		boolean snapshot, LocalDateTime startDate, LocalDateTime endDate);

	List<LotResultList> findBySnapShot(boolean snapShot);
}
