package codehows.com.daehoint.entity.mes;

import codehows.com.daehoint.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MaterialIssueList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_issue_list_id")
    private Long id;
    @Setter
    private boolean snapshot;
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

    public void updateSnapshot() {
        this.snapshot = false;
    }
}
