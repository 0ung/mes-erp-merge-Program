package codehows.com.daehoint.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
@Getter
@AllArgsConstructor
@Table(name = "track_lotcode")
public class TrackLotCode extends BaseEntity {

    @Id
    @Column(name = "track_lotCode_id")
    private String lotCode;
    private String category;

}
