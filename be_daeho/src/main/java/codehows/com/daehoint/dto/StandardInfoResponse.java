package codehows.com.daehoint.dto;

import codehows.com.daehoint.entity.StandardInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StandardInfoResponse {
    @JsonProperty("공수")
    private double power;

    @JsonProperty("메인생산일보 작성부서")
    private String mainWritingDepartment;

    @JsonProperty("메인생산일보 작성자")
    private String mainWriter;

    @JsonProperty("SM 작성부서")
    private String SMWritingDepartment;

    @JsonProperty("SM 작성자")
    private String SMWriter;

    @JsonProperty("IM 작성부서")
    private String IMWritingDepartment;

    @JsonProperty("IM 작성자")
    private String IMWriter;

    @JsonProperty("DIP 작성부서")
    private String DIPWritingDepartment;

    @JsonProperty("DIP 작성자")
    private String DIPWriter;

    @JsonProperty("MANUAL 작성부서")
    private String MANUALWritingDepartment;

    @JsonProperty("MANUAL 작성자")
    private String MANUALWriter;

    @JsonProperty("PCB 작성부서")
    private String PCBWritingDepartment;

    @JsonProperty("PCB 작성자")
    private String PCBWriter;

    @JsonProperty("CASE 작성부서")
    private String CASEWritingDepartment;

    @JsonProperty("CASE 작성자")
    private String CASEWriter;

    @JsonProperty("PACKING 작성부서")
    private String PACKINGWritingDepartment;

    @JsonProperty("PACKING 작성자")
    private String PACKINGWriter;

    @JsonProperty("LOSS 작성부서")
    private String LOSSDepartment;

    @JsonProperty("LOSS 작성자")
    private String LOSSWriter;

    @JsonProperty("ACCY 작성부서")
    private String ACCYDepartment;

    @JsonProperty("ACCY 작성자")
    private String ACCYWriter;

    @JsonProperty("구매자재 작성자")
    private String PurchaseWriter;

    @JsonProperty("구매자재 작성부서")
    private String PurchaseWritingDepartment;

    @JsonProperty("공정재고 작성자")
    private String ProcessStockWriter;

    @JsonProperty("공정재고 작성부서")
    private String ProcessStockWritingDepartment;

    @JsonProperty("장기불용자재 작성자")
    private String LtWriter;

    @JsonProperty("장기불용자재 작성부서")
    private String LtWritingDepartment;

    public static StandardInfoResponse toDTO(StandardInfo standardInfo) {
        return StandardInfoResponse.builder()
                .power(standardInfo.getPower())
                .mainWritingDepartment(standardInfo.getMainWritingDepartment())
                .mainWriter(standardInfo.getMainWriter())
                .SMWritingDepartment(standardInfo.getSMWritingDepartment())
                .SMWriter(standardInfo.getSMWriter())
                .IMWritingDepartment(standardInfo.getIMWritingDepartment())
                .IMWriter(standardInfo.getIMWriter())
                .DIPWritingDepartment(standardInfo.getDIPWritingDepartment())
                .DIPWriter(standardInfo.getDIPWriter())
                .MANUALWritingDepartment(standardInfo.getMANUALWritingDepartment())
                .MANUALWriter(standardInfo.getMANUALWriter())
                .PCBWritingDepartment(standardInfo.getPCBWritingDepartment())
                .PCBWriter(standardInfo.getPCBWriter())
                .CASEWritingDepartment(standardInfo.getCASEWritingDepartment())
                .CASEWriter(standardInfo.getCASEWriter())
                .PACKINGWritingDepartment(standardInfo.getPACKINGWritingDepartment())
                .PACKINGWriter(standardInfo.getPACKINGWriter())
                .LOSSDepartment(standardInfo.getLossDepartment())
                .LOSSWriter(standardInfo.getLossWriter())
                .ACCYDepartment(standardInfo.getACCYWritingDepartment())
                .ACCYWriter(standardInfo.getACCYWriter())
                .PurchaseWriter(standardInfo.getPurchaseWriter())
                .PurchaseWritingDepartment(standardInfo.getPurchaseWritingDepartment())
                .ProcessStockWriter(standardInfo.getProcessStockWriter())
                .ProcessStockWritingDepartment(standardInfo.getProcessStockWritingDepartment())
                .LtWritingDepartment(standardInfo.getLtWritingDepartment())
                .LtWriter(standardInfo.getLtWriter())
                .build();
    }
}

