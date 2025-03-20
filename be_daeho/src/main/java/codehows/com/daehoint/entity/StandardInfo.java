package codehows.com.daehoint.entity;

import codehows.com.daehoint.dto.StandardInfoResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "standard_info")
public class StandardInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "standard_info_id")
    private Long id;
    private double power;
    private String mainWritingDepartment;
    private String mainWriter;
    private String SMWritingDepartment;
    private String SMWriter;
    private String IMWritingDepartment;
    private String IMWriter;
    private String DIPWritingDepartment;
    private String DIPWriter;
    private String MANUALWritingDepartment;
    private String MANUALWriter;
    private String PCBWritingDepartment;
    private String PCBWriter;
    private String CASEWritingDepartment;
    private String CASEWriter;
    private String PACKINGWritingDepartment;
    private String PACKINGWriter;
    private String LossWriter;
    private String lossDepartment;
    private String ACCYWriter;
    private String ACCYWritingDepartment;
    private String PurchaseWriter;
    private String PurchaseWritingDepartment;

    private String ProcessStockWritingDepartment;
    private String ProcessStockWriter;

    private String LtWritingDepartment;
    private String LtWriter;

    public StandardInfo updateStandardInfo(StandardInfoResponse standardInfoResponse) {
        this.power = standardInfoResponse.getPower();
        this.mainWritingDepartment = standardInfoResponse.getMainWritingDepartment();
        this.mainWriter = standardInfoResponse.getMainWriter();
        this.SMWritingDepartment = standardInfoResponse.getSMWritingDepartment();
        this.SMWriter = standardInfoResponse.getSMWriter();
        this.IMWritingDepartment = standardInfoResponse.getIMWritingDepartment();
        this.IMWriter = standardInfoResponse.getIMWriter();
        this.DIPWritingDepartment = standardInfoResponse.getDIPWritingDepartment();
        this.DIPWriter = standardInfoResponse.getDIPWriter();
        this.MANUALWritingDepartment = standardInfoResponse.getMANUALWritingDepartment();
        this.MANUALWriter = standardInfoResponse.getMANUALWriter();
        this.PCBWritingDepartment = standardInfoResponse.getPCBWritingDepartment();
        this.PCBWriter = standardInfoResponse.getPCBWriter();
        this.CASEWritingDepartment = standardInfoResponse.getCASEWritingDepartment();
        this.CASEWriter = standardInfoResponse.getCASEWriter();
        this.PACKINGWritingDepartment = standardInfoResponse.getPACKINGWritingDepartment();
        this.PACKINGWriter = standardInfoResponse.getPACKINGWriter();
        this.ACCYWriter = standardInfoResponse.getACCYWriter();
        this.ACCYWritingDepartment = standardInfoResponse.getACCYDepartment();
        this.PurchaseWriter = standardInfoResponse.getPurchaseWriter();
        this.PurchaseWritingDepartment= standardInfoResponse.getPurchaseWritingDepartment();
        this.ProcessStockWriter= standardInfoResponse.getPurchaseWritingDepartment();
        this.ProcessStockWritingDepartment= standardInfoResponse.getPurchaseWritingDepartment();
        this.LtWritingDepartment = standardInfoResponse.getLtWritingDepartment();
        this.LtWriter = standardInfoResponse.getLtWriter();
        return this;
    }
}
