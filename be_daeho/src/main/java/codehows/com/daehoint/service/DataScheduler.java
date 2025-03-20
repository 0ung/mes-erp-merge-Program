package codehows.com.daehoint.service;

import codehows.com.daehoint.constants.Category;
import codehows.com.daehoint.mapper.DataSyncMapper;
import codehows.com.daehoint.dto.sync.*;
import codehows.com.daehoint.entity.TrackLotCode;
import codehows.com.daehoint.entity.erp.*;
import codehows.com.daehoint.entity.mes.*;
import codehows.com.daehoint.mapper.erp.ERPMapper;
import codehows.com.daehoint.mapper.mes.MESMapper;
import codehows.com.daehoint.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <b>DataScheduler</b><br />
 *
 * <p><b>설명:</b></p>
 * 이 클래스는 ERP와 MES 데이터를 동기화하고, 스케줄에 따라 보고서를 생성 및 저장하는 역할을 합니다.
 * 정기적으로 데이터를 업데이트하며, 데이터 일관성과 최신성을 유지하기 위해 다양한 스냅샷 처리와 캐싱을 활용합니다.
 *
 * <p><b>주요 기능:</b></p>
 * - ERP 및 MES 데이터 동기화<br />
 * - 메일 알림 발송<br />
 * - 공휴일 업데이트<br />
 * - 데이터 동기화 및 보고서 생성<br />
 * - 데이터 스냅샷 처리<br />
 *
 * <p><b>주요 메서드:</b></p>
 * <ul>
 *   <li>{@code sendMail()}: 정기적으로 이메일 알림을 발송합니다.</li>
 *   <li>{@code updateHoliday()}: 공휴일 데이터를 갱신합니다.</li>
 *   <li>{@code syncVPDPartsList()}: 가격정보를 동기화합니다.</li>
 *   <li>{@code mainScheduler()}: 주요 데이터를 동기화하고, 일일 보고서를 생성합니다.</li>
 *   <li>{@code createDailyProcessReport()}: 공정 생산 보고서를 생성합니다.</li>
 *   <li>{@code createDailyMainReport()}: 메인 생산 보고서를 생성합니다.</li>
 *   <li>{@code finalizeDataSnapshot()}: 데이터를 스냅샷 처리하여 저장합니다.</li>
 *   <li>{@code syncXXX()}: 각 데이터 유형별로 동기화를 수행하는 메서드들입니다.</li>
 *   <li>{@code finalizeXXX()}: 각 데이터 유형별로 스냅샷 처리를 수행하는 메서드들입니다.</li>
 * </ul>
 *
 * <p><b>주요 의존성:</b></p>
 * <ul>
 *   <li>{@code erpMapper}, {@code mesMapper}: 데이터 매핑을 위한 매퍼.</li>
 *   <li>{@code ...Repo}: 데이터 저장 및 조회를 위한 레포지토리.</li>
 *   <li>{@code mailService}, {@code standardService}: 메일 전송 및 기준 정보 관리 서비스.</li>
 *   <li>{@code mainProductionReportService}, {@code dailyProcessReportService}: 보고서 생성 서비스.</li>
 * </ul>
 *
 * <p><b>스케줄러 설명:</b></p>
 * - 매일 특정 시간에 데이터 동기화 및 보고서를 생성합니다.<br />
 * - 공휴일에는 작업을 수행하지 않도록 설정되어 있습니다.<br />
 * - CRON 표현식을 사용하여 다양한 작업의 실행 주기를 정의합니다.<br />
 *
 * <p><b>참고:</b></p>
 * - {@code isHoliday(LocalDate date)} 메서드를 활용하여 공휴일 여부를 판단합니다.<br />
 * - 메서드 내 예외 처리를 통해 실패 시 로그를 기록하고, 필요 시 이메일로 알림을 보냅니다.<br />
 * - 스냅샷 데이터는 별도의 플래그를 통해 관리됩니다.<br />
 */
@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class DataScheduler {
    private final ERPMapper erpMapper;
    private final MESMapper mesMapper;

    //기존 데이터 불러오는 Repo
    private final VLGWHStockAmtListRepo vlgwhStockAmtListRepo;
    private final VLGWHStockWHAmtListRepo vlgwhStockWHAmtListRepo;
    private final VPDPartsListRepo vpdPartsListRepo;
    private final VPDSPFWorkReportQCRepo vpdspfWorkReportQCRepo;
    private final VPUORDAmtListDayRepo vpuordAmtListDayRepo;
    private final DailyWorkLossRepo dailyWorkLossRepo;
    private final LotResultListRepo lotResultListRepo;
    private final MaterialPurchasePlanMonthlyRepo materialPurchasePlanMonthlyRepo;
    private final ProductionDailyListRepo productionDailyListRepo;
    private final WorkerRetentionRepo workerRetentionRepo;
    private final TrackLotCodeRepo trackLotCodeRepo;
    private final StandardService standardService;
    private final HolidayRepo holidayRepo;
    private final HolidayService holidayService;
    private final MaterialIssueListRepo materialIssueListRepo;
    private final EstimatedExpensesRepo estimatedExpensesRepo;
    private final DataSyncMapper dataSyncMapper = DataSyncMapper.INSTANCE;
    private final MailService mailService;
    private final MainProductionReportService mainProductionReportService;
    private final DailyProcessReportService dailyProcessReportService;
    private final ProcessStockService processStockService;
    private final LossDateService lossDateService;

    @Scheduled(cron = "0 1 8 * * *")
    public void sendMail() {
        mailService.sendMail();
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void updateHoliday() {
        holidayService.createHolidayDataWithApi();
        holidayService.createHolidays();
        log.info("공휴일 갱신완료");
    }

    /**
     * VPD 부품 리스트 동기화
     * 시간별로 업데이트 X
     * 수동/자동
     * 기준정보로 관리
     * 최신 데이터 컬럼 사용
     * CRON식은 평일 00시에만 실행
     */
    @Scheduled(cron = "1 0 0 * * *", zone = "Asia/Seoul")
    public void syncVPDPartsList() {
        if (isHoliday(LocalDate.now())) {
            log.info("공휴일로 데이터 반영 X");
            return;
        }
        standardService.changePartList();
        List<VPDPartsListDTO> vpdPartsList = erpMapper.getVpdPartsLists();

        //신규 데이터는 true로 계산에 반영
        vpdPartsList.forEach(vpdPartsListDTO -> {
            VPDPartsList data = dataSyncMapper.toEntity(vpdPartsListDTO);
            data.setRecent(true);
            vpdPartsListRepo.save(data);
        });
        log.info("가격정보 반영 완료");
    }

    @Scheduled(cron = "1 0 0 * * *", zone = "Asia/Seoul")
    public void syncMaterialIssueList() {
        if (isHoliday(LocalDate.now())) {
            log.info("공휴일로 데이터 반영 X");
            return;
        }
        List<MaterialIssueListDTO> materialIssueListDTOS = mesMapper.getMaterialIssueList();
        standardService.changeMaterialIssueList();
        materialIssueListDTOS.forEach(
                materialIssueListDTO -> {
                    MaterialIssueList materialIssueList = dataSyncMapper.toEntity(materialIssueListDTO);
                    materialIssueList.setSnapshot(true);
                    materialIssueListRepo.save(materialIssueList);
                });
    }

    @Scheduled(cron = "1 0 8-22 * * *", zone = "Asia/Seoul")
    public void mainScheduler() {
        if (isHoliday(LocalDate.now())) {
            return;
        }
        // 가장 최근 추가된 데이터의 createDateTime 가져오기
        Optional<VLGWHStockAmtList> latestRecord = vlgwhStockAmtListRepo.findTopByOrderByIdDesc();
        // 최근 기록이 존재하고, 해당 시간과 현재 시간이 동일한지 비교
        if (latestRecord.isPresent() && isSameTimeSlot(latestRecord.get().getCreateDateTime(), LocalDateTime.now())) {
            return;
        }
        try {
            syncVLGWHStockAmtList();
            syncVLGWHStockWHAmtList();
            syncVPDSPFWorkReportQC();
            syncVPUORDAmtListDay();
            syncDailyWorkLoss();
            syncLotResultList();
            syncMaterialPurchasePlanMonthly();
            syncProductionDailyList();
            syncWorkerRetention();
            syncEstimatedExpenses();
            createDailyProcessReport();
            createDailyMainReport();
            log.info("생산 데이터 저장 완료");
        } catch (Exception e) {
            log.error("생산 데이터 저장 실패", e);
            mailService.sendMail(e.getMessage());
        }
    }

    public void createDailyProcessReport() {
        if (isHoliday(LocalDate.now())) {
            return;
        }
        for (Category process : Category.values()) {
            dailyProcessReportService.setCategory(process);
            dailyProcessReportService.setSnapshot(false);
            dailyProcessReportService.createProductionPerformanceReport();
            dailyProcessReportService.createProcessProductionReport();
        }
    }

    public void createDailyMainReport() {
        if (isHoliday(LocalDate.now())) {
            return;
        }
        mainProductionReportService.setSnapshot(false);
        processStockService.setSnapShot(false);
        lossDateService.createLossData(false);
        mainProductionReportService.saveMainProductionReport(mainProductionReportService.createMainProductionReport());
        processStockService.saveProcessStock(processStockService.createProcessStock());
    }

    // =============== 스냅샷 마무리 스케줄 ===============

    /**
     * 평일 00시 정각에 실행
     * 전체 데이터를 다시 불러와 snapshot 처리 후 저장
     */
    @Scheduled(cron = "0 55 23 * * *", zone = "Asia/Seoul")
    public void finalizeDataSnapshot() {
        if (isHoliday(LocalDate.now())) {
            return;
        }
        try {
            finalizeVLGWHStockAmtList();
            finalizeVLGWHStockWHAmtList();
            finalizeVPDSPFWorkReportQC();
            finalizeVPUORDAmtListDay();
            finalizeDailyWorkLoss();
            finalizeLotResultList();
            finalizeMaterialPurchasePlanMonthly();
            finalizeProductionDailyList();
            finalizeWorkerRetention();
            finalizeEstimatedExpenses();
            finalizeDailyProcessReport();
            finalizeDailyMainReport();
            log.info("집계 데이터 저장 완료");
        } catch (Exception e) {
            log.error("집계 데이터 저장 실패",e);
//            mailService.sendMail(e.getMessage());
        }
    }

    public void finalizeDailyProcessReport() {
        if (isHoliday(LocalDate.now())) {
            return;
        }
        if (isHoliday(LocalDate.now())) {
            return;
        }
        for (Category process : Category.values()) {
            dailyProcessReportService.setCategory(process);
            dailyProcessReportService.setSnapshot(true);
            dailyProcessReportService.createProductionPerformanceReport();
            dailyProcessReportService.createProcessProductionReport();
        }
    }

    public void finalizeDailyMainReport() {
        if (isHoliday(LocalDate.now())) {
            return;
        }
        mainProductionReportService.setSnapshot(true);
        processStockService.setSnapShot(true);
        lossDateService.createLossData(true);
        mainProductionReportService.saveMainProductionReport(mainProductionReportService.createMainProductionReport());
        processStockService.saveProcessStock(processStockService.createProcessStock());
    }

    // 날짜 포맷 메서드
    private String getTodayDateWithHyphen() {
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return simpleFormatter.format(new Date());
    }

    private String getTodayDateWithoutHyphen() {
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyyMMdd");
        return simpleFormatter.format(new Date());
    }

    // 시간대를 비교하는 메서드 (예: 시간만 비교하거나 필요한 범위를 조정)
    private boolean isSameTimeSlot(LocalDateTime time1, LocalDateTime time2) {
        return time1.getHour() == time2.getHour();  // 시간만 비교
    }

    private boolean isHoliday(LocalDate date) {
        return holidayRepo.existsByHolidayDate(date);
    }

    /**
     * 재고 데이터 동기화
     * 시간별로 업데이트 진행
     * 분기 컬럼 추가 (flag)
     * 저장 데이터 구분은 createDate를 기준으로 구분
     * CRON식은 평일 09시부터 21시까지 실행
     */
    public void syncVLGWHStockAmtList() {
        List<VLGWHStockAmtListDTO> vlgwhStockAmtList = erpMapper.getVlgwhStockAmtList();
        vlgwhStockAmtList.forEach(vlgwhStockAmtListDTO -> {
            VLGWHStockAmtList data = dataSyncMapper.toEntity(vlgwhStockAmtListDTO);
            vlgwhStockAmtListRepo.save(data);
        });
    }

    /**
     * 재고 데이터 동기화
     * 시간별로 업데이트 진행
     * 분기 컬럼 추가 (flag)
     * CRON식은 평일 09시부터 21시까지 실행
     */
    public void syncVLGWHStockWHAmtList() {
        List<VLGWHStockWHAmtListDTO> vlgwhStockWHAmtList = erpMapper.getVlgwhStockWhAmtList();
        vlgwhStockWHAmtList.forEach(vlgwhStockWHAmtListDTO -> {
            VLGWHStockWHAmtList data = dataSyncMapper.toEntity(vlgwhStockWHAmtListDTO);
            vlgwhStockWHAmtListRepo.save(data);
        });
    }

    /**
     * 재고 데이터 동기화
     * 시간별로 업데이트 진행
     * 분기 컬럼 추가 (flag)
     * CRON식은 평일 09시부터 21시까지 실행
     */
    public void syncVPDSPFWorkReportQC() {
        List<VPDSPFWorkReportQCDTO> vpdspfWorkReportQCDTOS = erpMapper.getVpdspfWorkReportQcs(
                getTodayDateWithoutHyphen());
        vpdspfWorkReportQCDTOS.forEach(vpdspfWorkReportQCDTO -> {
            VPDSPFWorkReportQC reportQC = dataSyncMapper.toEntity(vpdspfWorkReportQCDTO);
            vpdspfWorkReportQCRepo.save(reportQC);
        });
    }

    /**
     * 구매 데이터 동기화
     * 시간별로 업데이트 진행
     * 분기 컬럼 추가 (flag)
     * CRON식은 평일 09시부터 21시까지 실행
     */
    public void syncVPUORDAmtListDay() {
        List<VPUORDAmtListDayDTO> vpuordAmtListDays = erpMapper.getVpuordAmtListDays();
        vpuordAmtListDays.forEach(vpuordAmtListDayDTO -> {
            VPUORDAmtList day = dataSyncMapper.toEntity(vpuordAmtListDayDTO);
            vpuordAmtListDayRepo.save(day);
        });
    }

    /**
     * 공정 데이터 동기화
     * 시간별로 업데이트 진행
     * 분기 컬럼 추가 (flag)
     * 추적할 수 있게 LOTCODE 유지
     * CRON식은 평일 09시부터 21시까지 실행
     */
    public void syncDailyWorkLoss() {
        List<DailyWorkLossResponse> dailyWorkLosses = mesMapper.getDailyWorkLosses("2024-10-01",
                getTodayDateWithHyphen());
        dailyWorkLosses.forEach(dailyWorkLossDTO -> {
            DailyWorkLoss workLoss = dataSyncMapper.toEntity(dailyWorkLossDTO);
            dailyWorkLossRepo.save(workLoss);
        });
    }

    /**
     * 공정 데이터 동기화
     * 시간별로 업데이트 진행
     * 분기 컬럼 추가 (flag)
     * 추적할 수 있게 LOTCODE 유지
     * CRON식은 평일 09시부터 21시까지 실행
     */
    public void syncLotResultList() {
        List<LotResultListDTO> lotResultLists = mesMapper.getLotResultLists(getTodayDateWithHyphen(),
                getTodayDateWithHyphen());
        lotResultLists.forEach(lotResultListDTO -> {
            LotResultList lotResultList = dataSyncMapper.toEntity(lotResultListDTO);
            lotResultListRepo.save(lotResultList);
        });
    }

    /**
     * 구매 데이터 동기화
     * 시간별로 업데이트 진행
     * 분기 컬럼 추가 (flag)
     * CRON식은 평일 09시부터 21시까지 실행
     */
    public void syncMaterialPurchasePlanMonthly() {
        List<MaterialPurchasePlanMonthlyDTO> materialPurchasePlanMonthlyDTOS = mesMapper.getMaterialPurchasePlanMonthlies();
        materialPurchasePlanMonthlyDTOS.forEach(materialPurchasePlanMonthlyDTO -> {
            MaterialPurchasePlanMonthly monthly = dataSyncMapper.toEntity(materialPurchasePlanMonthlyDTO);
            materialPurchasePlanMonthlyRepo.save(monthly);
        });
    }

    /**
     * 생산 데이터 동기화
     * 시간별로 업데이트 진행
     * 분기 컬럼 추가 (flag)
     * CRON식은 평일 09시부터 21시까지 실행
     */
    public void syncProductionDailyList() {
        List<ProductionDailyListDTO> productionDailyLists = mesMapper.getProductionDailyLists();
        java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.now());
        productionDailyLists.forEach(productionDailyListDTO -> {
            ProductionDailyList list = dataSyncMapper.toEntity(productionDailyListDTO);
            EquipmentUseTimeDTO equipments = mesMapper.getEquipmentUseTimeDto(list.getLotId(), sqlDate);
            list.setEquipmentUseTime(equipments.getEquipmentUseTime());
            productionDailyListRepo.save(list);
        });
    }

    /**
     * 인원 데이터 동기화
     * 시간별로 업데이트 진행
     * 분기 컬럼 추가 (flag)
     * CRON식은 평일 09시부터 21시까지 실행
     */
    public void syncWorkerRetention() {
        List<WorkerRetentionDTO> workerRetentions = mesMapper.getWorkerRetentions();
        workerRetentions.forEach(workerRetentionDTO -> {
            WorkerRetention retention = dataSyncMapper.toEntity(workerRetentionDTO);
            workerRetentionRepo.save(retention);
        });
    }

    public void syncEstimatedExpenses() {
        List<EstimatedExpensesDTO> expensesDTOS = mesMapper.getEstimatedExpensesDto();
        expensesDTOS.forEach(
                estimatedExpensesDTO -> estimatedExpensesRepo.save(dataSyncMapper.toEntity(estimatedExpensesDTO)));
    }


    // =============== 스냅샷 처리 메서드 구현 ===============

    /**
     * VLGWHStockAmtList 스냅샷 처리
     */
    public void finalizeVLGWHStockAmtList() {
        List<VLGWHStockAmtListDTO> vlgwhStockAmtLists = erpMapper.getVlgwhStockAmtList();
        vlgwhStockAmtLists.forEach(record -> {
            VLGWHStockAmtList vlgwhStockAmtList = dataSyncMapper.toEntity(record);
            vlgwhStockAmtList.setSnapShot(true);
            vlgwhStockAmtListRepo.save(vlgwhStockAmtList);
        });
    }

    /**
     * VLGWHStockWHAmtList 스냅샷 처리
     */
    public void finalizeVLGWHStockWHAmtList() {
        List<VLGWHStockWHAmtListDTO> vlgwhStockWHAmtLists = erpMapper.getVlgwhStockWhAmtList();
        vlgwhStockWHAmtLists.forEach(record -> {
            VLGWHStockWHAmtList vlgwhStockWHAmtList = dataSyncMapper.toEntity(record);
            vlgwhStockWHAmtList.setSnapShot(true);
            vlgwhStockWHAmtListRepo.save(vlgwhStockWHAmtList);
        });
    }

    /**
     * VPDSPFWorkReportQC 스냅샷 처리
     */
    public void finalizeVPDSPFWorkReportQC() {
        List<VPDSPFWorkReportQCDTO> vpdspfWorkReportQCs = erpMapper.getVpdspfWorkReportQcs(getTodayDateWithHyphen());
        vpdspfWorkReportQCs.forEach(record -> {
            VPDSPFWorkReportQC reportQC = dataSyncMapper.toEntity(record);
            reportQC.setSnapShot(true);
            vpdspfWorkReportQCRepo.save(reportQC);
        });
    }

    /**
     * VPUORDAmtListDay 스냅샷 처리
     */
    public void finalizeVPUORDAmtListDay() {
        List<VPUORDAmtListDayDTO> vpuordAmtListDays = erpMapper.getVpuordAmtListDays();
        vpuordAmtListDays.forEach(record -> {
            VPUORDAmtList vpuordAmtList = dataSyncMapper.toEntity(record);
            vpuordAmtList.setSnapShot(true);
            vpuordAmtListDayRepo.save(vpuordAmtList);
        });
    }

    /**
     * DailyWorkLoss 스냅샷 처리
     */
    public void finalizeDailyWorkLoss() {
        List<DailyWorkLossResponse> dailyWorkLosses = mesMapper.getDailyWorkLosses(getTodayDateWithHyphen(),
                getTodayDateWithHyphen());
        dailyWorkLosses.forEach(record -> {
            DailyWorkLoss dailyWorkLoss = dataSyncMapper.toEntity(record);
            dailyWorkLoss.setSnapShot(true);
            dailyWorkLossRepo.save(dailyWorkLoss);
            if (dailyWorkLoss.getStateProgressing().equals("진행")) {
                try {
                    trackLotCodeRepo.save(new TrackLotCode(dailyWorkLoss.getLotNo(), "loss"));
                } catch (DataIntegrityViolationException e) {
                    trackLotCodeRepo.deleteById(dailyWorkLoss.getLotNo());
                }
            }
        });
    }


    /**
     * LotResultList 스냅샷 처리
     */
    public void finalizeLotResultList() {
        List<LotResultListDTO> lotResultLists = mesMapper.getLotResultLists(getTodayDateWithHyphen(),
                getTodayDateWithHyphen());
        lotResultLists.forEach(record -> {
            LotResultList list = dataSyncMapper.toEntity(record);
            list.setSnapShot(true);
            lotResultListRepo.save(list);
            updateLotState();
            if (list.getLotState().equals("생산중")) {
                try {
                    trackLotCodeRepo.save(new TrackLotCode(list.getLotId(), "lotResult"));
                } catch (DataIntegrityViolationException e) {
                    trackLotCodeRepo.deleteById(list.getLotId());
                }
            }
        });
    }

    public void updateLotState() {
        lotResultListRepo.findBySnapShot(true).forEach(
                lotResultList -> {
                    TrackLotCode byLotCode = trackLotCodeRepo.findByLotCode(lotResultList.getLotId());
                    if (byLotCode != null) {
                        if (lotResultList.getLotState().equals("생산완료")) {
                            lotResultList.updateLotResultList("생산완료");
                        }
                    }
                }
        );
    }

    /**
     * MaterialPurchasePlanMonthly 스냅샷 처리
     */
    public void finalizeMaterialPurchasePlanMonthly() {
        List<MaterialPurchasePlanMonthlyDTO> materialPurchasePlanMonthlies = mesMapper.getMaterialPurchasePlanMonthlies();
        materialPurchasePlanMonthlies.forEach(record -> {
            MaterialPurchasePlanMonthly materialPurchasePlanMonthly = dataSyncMapper.toEntity(record);
            materialPurchasePlanMonthly.setSnapShot(true);
            materialPurchasePlanMonthlyRepo.save(materialPurchasePlanMonthly);
        });
    }

    /**
     * ProductionDailyList 스냅샷 처리
     */
    public void finalizeProductionDailyList() {
        List<ProductionDailyListDTO> productionDailyLists = mesMapper.getProductionDailyLists();
        java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.now());
        productionDailyLists.forEach(record -> {
            ProductionDailyList productionDailyList = dataSyncMapper.toEntity(record);
            EquipmentUseTimeDTO equipments = mesMapper.getEquipmentUseTimeDto(record.getLotId(), sqlDate);
            productionDailyList.setEquipmentUseTime(equipments.getEquipmentUseTime());
            productionDailyList.setSnapShot(true);
            productionDailyListRepo.save(productionDailyList);
        });
    }

    /**
     * WorkerRetention 스냅샷 처리
     */
    public void finalizeWorkerRetention() {
        List<WorkerRetentionDTO> workerRetentions = mesMapper.getWorkerRetentions();
        workerRetentions.forEach(record -> {
            WorkerRetention workerRetention = dataSyncMapper.toEntity(record);
            workerRetention.setSnapShot(true);
            workerRetentionRepo.save(workerRetention);
        });
    }

    /**
     * EstimatedExpenses 스냅샷 처리
     */
    public void finalizeEstimatedExpenses() {
        List<EstimatedExpensesDTO> expensesDTOS = mesMapper.getEstimatedExpensesDto();
        expensesDTOS.forEach(
                estimatedExpensesDTO -> {
                    EstimatedExpenses estimatedExpenses = dataSyncMapper.toEntity(estimatedExpensesDTO);
                    estimatedExpenses.setSnapshot(true);
                    estimatedExpensesRepo.save(estimatedExpenses);
                });
    }

}
