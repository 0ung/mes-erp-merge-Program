package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.mes.DailyWorkLoss;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyWorkLossRepo extends JpaRepository<DailyWorkLoss, Long> {
    DailyWorkLoss findByLotNoAndSnapShotAndCreateDateTimeAfter(String lotNo, boolean isSnapShot,
                                                               LocalDateTime createDate);

    List<DailyWorkLoss> findByCategoryAndSnapShotAndCreateDateTimeAfter(String category, boolean isSnapShot,
                                                                        LocalDateTime createDate);

    List<DailyWorkLoss> findByCreateDateTimeAfterAndSnapShot(LocalDateTime createDate, boolean isSnapShot);

    List<DailyWorkLoss> findByCreateDateTimeBetweenAndSnapShotAndCategory(LocalDateTime createDate,
                                                                          LocalDateTime endDate, boolean snapshot, String category);

    List<DailyWorkLoss> findByCreateDateTimeBetweenAndSnapShotAndLossReason(LocalDateTime createDate,
                                                                            LocalDateTime endDate, boolean snapshot, String lossReason);

    List<DailyWorkLoss> findByCreateDateTimeBetweenAndSnapShot(LocalDateTime createDate,
                                                               LocalDateTime endDate, boolean snapshot);

    List<DailyWorkLoss> findBySnapShot(boolean isSnapShot);
}
