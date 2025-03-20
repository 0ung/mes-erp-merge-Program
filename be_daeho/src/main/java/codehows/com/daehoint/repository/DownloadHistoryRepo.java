package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.DownloadHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DownloadHistoryRepo extends JpaRepository<DownloadHistory, Long> {
}
