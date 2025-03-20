package codehows.com.daehoint.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdditionalDataRequest {
    private String category;
    private Double availableManHours;
    private Double availablePersonnel;
    private Double outSouringCost;
    private Double overtimePersonnel;
    private Double overtimeHours;
}
