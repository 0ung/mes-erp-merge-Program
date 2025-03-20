package codehows.com.daehoint.dto.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MaterialIssueListDTO {
	private String itemCd;
	private String itemName;
	private String dateOfOccurrence;
	private String reason;
	private String applyContents;
	private String reponsibilityClassification;
	private Double stockQTY;
	private Double beforeOccurrenceUsingQTY;
	private Double afterOccurrenceUsingQTY;
	private Double longTermInventory;
	private Double insolvencyStock;
	private Double resale;
	private Double disuse;
	private boolean isValid;
}
