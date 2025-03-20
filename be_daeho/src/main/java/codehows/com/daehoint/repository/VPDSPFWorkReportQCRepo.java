package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.erp.VPDSPFWorkReportQC;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VPDSPFWorkReportQCRepo extends JpaRepository<VPDSPFWorkReportQC, Long> {

	List<VPDSPFWorkReportQC> findByOperationInstructionNumberAndCreateDateTimeAfterAndSnapShot(String lotCode,
		LocalDateTime createDate, boolean isSnapShot);

	List<VPDSPFWorkReportQC> findByOperationInstructionNumber(String lotCode);
}
