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
@Table(name = "access_page_history")
public class AccessPageHistory extends BaseEntity {
	@Id
	@Column(name = "access_page_history_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String accessId;
	private String accessIp;
	private String accessPage;
}
