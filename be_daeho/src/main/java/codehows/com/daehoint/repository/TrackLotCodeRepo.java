package codehows.com.daehoint.repository;


import codehows.com.daehoint.entity.TrackLotCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackLotCodeRepo extends JpaRepository<TrackLotCode, String> {

    TrackLotCode findByLotCode(String lotCode);
}
