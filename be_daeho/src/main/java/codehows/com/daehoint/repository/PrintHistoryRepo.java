package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.PrintHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrintHistoryRepo extends JpaRepository<PrintHistory,Long> {
}
