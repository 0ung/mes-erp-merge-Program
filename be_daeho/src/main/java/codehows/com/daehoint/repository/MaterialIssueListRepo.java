package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.mes.MaterialIssueList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MaterialIssueListRepo extends JpaRepository<MaterialIssueList, Long> {
	List<MaterialIssueList> findBySnapshot(
			boolean isSnapShot);

	List<MaterialIssueList> findByCreateDateTimeBetweenAndSnapshot(LocalDateTime startDate, LocalDateTime endDate,boolean snapshot);

}
