package codehows.com.daehoint.service;

import codehows.com.daehoint.config.Util;
import codehows.com.daehoint.dto.SearchReportResponse;
import codehows.com.daehoint.dto.sync.MaterialIssueListDTO;
import codehows.com.daehoint.entity.mes.MaterialIssueList;
import codehows.com.daehoint.repository.MaterialIssueListRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * LtUnusedMaterialService 클래스
 *
 * <p>이 클래스는 불용 자재의 Issue 데이터를 관리하고 검색하는 서비스입니다.
 * MaterialIssueList 데이터베이스와 상호작용하여 불용 자재와 관련된 데이터를
 * 생성, 검색 및 변환합니다.</p>
 * <p>
 * 주요 기능:
 * <ul>
 *   <li>불용 자재 Issue 데이터를 스냅샷 여부에 따라 조회</li>
 *   <li>특정 ID에 기반한 불용 자재 Issue 데이터를 검색</li>
 *   <li>기간별 불용 자재 Issue 데이터를 검색 및 요약</li>
 * </ul>
 * <p>
 * 주요 메서드:
 * <ul>
 *   <li>{@code getIssueListDTOS}: 스냅샷 여부에 따라 불용 자재 Issue 데이터를 DTO 형식으로 반환</li>
 *   <li>{@code searchLTUnusedMaterialReportById}: 특정 ID에 기반하여 해당 Issue 데이터를 조회</li>
 *   <li>{@code searchLTUnusedMaterialReport}: 주어진 기간 내의 불용 자재 Issue 데이터를 조회하고 요약 정보 생성</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class LtUnusedMaterialService {
    private final MaterialIssueListRepo materialIssueListRepo;

    public List<MaterialIssueListDTO> getIssueListDTOS(boolean isSnapShot) {
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        List<MaterialIssueListDTO> materialIssueListDTOS = new ArrayList<>();

        List<MaterialIssueList> materialIssueLists = materialIssueListRepo.findBySnapshot(isSnapShot);

        materialIssueLists.forEach(materialIssueList -> {
            materialIssueListDTOS.add(Util.mapper.toMaterialIssueListDto(materialIssueList));
        });

        return materialIssueListDTOS;
    }

    public List<MaterialIssueListDTO> searchLTUnusedMaterialReportById(Long id) {

        List<MaterialIssueListDTO> materialIssueListDTOS = new ArrayList<>();

        MaterialIssueList materialIssueList = materialIssueListRepo.findById(id).orElse(null);

        if (materialIssueList != null) {
            // 해당 날짜의 00:00:00과 23:59:59 설정
            LocalDateTime startDateTime = materialIssueList.getCreateDateTime().toLocalDate().atStartOfDay();
            LocalDateTime endDateTime = startDateTime.withHour(23).withMinute(59).withSecond(59);

            // Between 쿼리로 해당일의 데이터를 가져옴
            List<MaterialIssueList> materialIssueLists = materialIssueListRepo.findByCreateDateTimeBetweenAndSnapshot(
                    startDateTime, endDateTime, true);

            // 가져온 데이터를 DTO로 변환
            materialIssueLists.forEach(materialIssue -> {
                materialIssueListDTOS.add(Util.mapper.toMaterialIssueListDto(materialIssue));
            });
        }
        return materialIssueListDTOS;
    }

    public List<SearchReportResponse> searchLTUnusedMaterialReport(LocalDate startDate, LocalDate endDate) {
        List<MaterialIssueList> materialIssueListDTOS = materialIssueListRepo.findByCreateDateTimeBetweenAndSnapshot(
                startDate.atStartOfDay(), endDate.atStartOfDay(), true);

        List<SearchReportResponse> searchReportResponses = new ArrayList<>();
        materialIssueListDTOS.forEach(issueList -> {
            String formattedDate = issueList.getCreateDateTime().format(Util.formatter);
            searchReportResponses.add(SearchReportResponse.builder()
                    .id(issueList.getId())
                    .name("불용 자재 ISSUE")
                    .createDate(formattedDate)
                    .build());
        });

        return searchReportResponses;
    }

}
