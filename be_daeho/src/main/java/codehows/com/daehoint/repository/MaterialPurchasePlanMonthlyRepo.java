package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.mes.MaterialPurchasePlanMonthly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MaterialPurchasePlanMonthlyRepo extends JpaRepository<MaterialPurchasePlanMonthly, Long> {
	MaterialPurchasePlanMonthly findByCreateDateTimeAfterAndSnapShot(LocalDateTime dateTime, boolean isSnapShot);

	MaterialPurchasePlanMonthly findByCreateDateTimeBetweenAndSnapShot(LocalDateTime startDate,LocalDateTime endDate, boolean snapshot);
}
