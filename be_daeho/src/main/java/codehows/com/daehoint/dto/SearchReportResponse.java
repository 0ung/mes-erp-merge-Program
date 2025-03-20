package codehows.com.daehoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchReportResponse {

	private Long id;
	private String name;
	private String createDate;
}
