package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.erp.VPDPartsList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VPDPartsListRepo extends JpaRepository<VPDPartsList,Long> {

	VPDPartsList findByDaehoCodeAndRecent(String daehoCode,Boolean recent);
}
