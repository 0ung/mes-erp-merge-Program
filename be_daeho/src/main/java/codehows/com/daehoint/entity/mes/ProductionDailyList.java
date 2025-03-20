package codehows.com.daehoint.entity.mes;

import codehows.com.daehoint.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "production_dailylist")
public class ProductionDailyList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductionDailyList_id")
    private Long id;

    // 생산시작일
    private String startTime;

    // 작업지시번호
    private String lotId;
    @Setter
    // MODEL명
    private String categoryItemValue03;

    //생산팀
    private String department;

    //대호코드
    private String itemCd;

    //공정명
    private String itemName;
    @Setter
    //Category
    private String category;

    // 계획수량
    private String qty;

    // 투입수량
    private String inQty;

    // 완료수량
    private String outQty;

    // 불량수량
    private String defectQty;

    // 생산수량구분
    private String gubunName;

    // 08:00~09:00
    private String time01;

    // 09:00~10:00
    private String time02;

    // 10:10~11:10
    private String time03;

    // 11:10~12:20
    private String time04;

    // 13:10~14:10
    private String time05;

    // 14:10~15:00
    private String time06;

    // 15:10~16:10
    private String time07;

    // 16:10~17:10
    private String time08;

    // 17:10~18:00
    private String time09;

    // 18:20~19:20
    private String time10;

    // 19:20~20:20
    private String time11;

    // 진행상태
    private String lotState;

    //설비 가동시간
    private Double equipmentUseTime;
    //00시 마무리 데이터
    private boolean snapShot;
}
