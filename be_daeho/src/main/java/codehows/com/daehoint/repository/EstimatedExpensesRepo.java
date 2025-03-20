package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.mes.EstimatedExpenses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface EstimatedExpensesRepo extends JpaRepository<EstimatedExpenses, Long> {
	EstimatedExpenses findByCreateDateTimeAfterAndSnapshot(LocalDateTime createDate, boolean snapshot);
}
