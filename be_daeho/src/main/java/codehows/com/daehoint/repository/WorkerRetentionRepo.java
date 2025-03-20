package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.mes.WorkerRetention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkerRetentionRepo extends JpaRepository<WorkerRetention,Long> {
	List<WorkerRetention> findByCreateDateTimeAfterAndSnapShot(LocalDateTime dateTime,Boolean isSnapShot);
}
