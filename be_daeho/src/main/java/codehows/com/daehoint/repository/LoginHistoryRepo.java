package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginHistoryRepo extends JpaRepository<LoginHistory,Long> {
}
