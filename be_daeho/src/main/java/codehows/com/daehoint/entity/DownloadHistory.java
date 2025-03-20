package codehows.com.daehoint.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "download_history")
public class DownloadHistory extends BaseEntity {
    @Id
    @Column(name = "download_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accessId;
    private String accessIp;
    private String fileName;
}
