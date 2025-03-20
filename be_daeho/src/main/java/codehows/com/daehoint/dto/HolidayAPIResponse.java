package codehows.com.daehoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor  // 기본 생성자 추가
public class HolidayAPIResponse {
	private Response response;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor  // 기본 생성자 추가
	public static class Response {
		private Header header;
		private Body body;
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor  // 기본 생성자 추가
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor  // 기본 생성자 추가
	public static class Body {
		private Items items;
		private int numOfRows;
		private int pageNo;
		private int totalCount;
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor  // 기본 생성자 추가
	public static class Items {
		private List<Item> item;
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor  // 기본 생성자 추가
	public static class Item {
		private String dateKind;
		private String dateName;
		private String isHoliday;
		private long locdate;
		private int seq;
	}
}
