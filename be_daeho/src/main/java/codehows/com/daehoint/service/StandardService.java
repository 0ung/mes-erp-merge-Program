package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.sync.MaterialIssueListDTO;
import codehows.com.daehoint.entity.mes.MaterialIssueList;
import codehows.com.daehoint.mapper.DataSyncMapper;
import codehows.com.daehoint.dto.sync.VPDPartsListDTO;
import codehows.com.daehoint.entity.erp.VPDPartsList;
import codehows.com.daehoint.mapper.erp.ERPMapper;
import codehows.com.daehoint.repository.MaterialIssueListRepo;
import codehows.com.daehoint.repository.VPDPartsListRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * StandardService 클래스
 *
 * <p>이 클래스는 기준 정보를 관리하는 서비스로, ERP 시스템의 데이터를 동기화하고
 * 부품 리스트를 업데이트하는 기능을 제공합니다. ERP 시스템에서 데이터를 조회한 후,
 * 기존 데이터의 상태를 갱신하거나 새로운 데이터를 추가합니다.</p>
 * <p>
 * 주요 기능:
 * <ul>
 *   <li>기존 부품 리스트 상태 초기화: {@code changePartList()}</li>
 *   <li>ERP 데이터를 활용한 부품 리스트 업데이트: {@code updatePartList()}</li>
 * </ul>
 *
 * <p>기능 설명:</p>
 * <ul>
 *   <li>{@code changePartList()}: 기존 데이터의 상태를 초기화하며, 모든 데이터의 상태를 'false'로 변경합니다.</li>
 *   <li>{@code updatePartList()}: ERP 시스템에서 새로운 부품 리스트 데이터를 조회하고, 이를 최신 상태로 저장하며, 'true' 상태로 설정합니다.</li>
 * </ul>
 *
 * <p>특징:</p>
 * <ul>
 *   <li>ERP 시스템과의 데이터 동기화를 위한 매퍼를 사용합니다.</li>
 *   <li>JPA를 활용하여 데이터베이스에 접근하고 데이터를 갱신합니다.</li>
 *   <li>트랜잭션 관리를 통해 데이터 일관성을 보장합니다.</li>
 * </ul>
 *
 * <p>기술 스택 및 의존성:</p>
 * <ul>
 *   <li>{@code ERPMapper}: ERP 시스템과의 데이터 매핑을 처리</li>
 *   <li>{@code DataSyncMapper}: DTO와 엔티티 간 변환을 처리</li>
 *   <li>{@code VPDPartsListRepo}: 부품 리스트 데이터베이스 관리</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class StandardService {

    private final ERPMapper erpMapper;
    private final DataSyncMapper dataSyncMapper = DataSyncMapper.INSTANCE;
    private final VPDPartsListRepo vpdPartsListRepo;
    private final MaterialIssueListRepo materialIssueListRepo;

    @Transactional
    public void changePartList() {
        List<VPDPartsList> vpdPartsLists = vpdPartsListRepo.findAll();
        //기존 데이터는 전부 false 처리
        vpdPartsLists.forEach(vpdPart -> {
            vpdPart.updateRecent(false);
        });
    }

    @Transactional
    public void updatePartList() {
        List<VPDPartsListDTO> vpdPartsList = erpMapper.getVpdPartsLists();

        //신규 데이터는 true로 계산에 반영
        vpdPartsList.forEach(vpdPartsListDTO -> {
            VPDPartsList data = dataSyncMapper.toEntity(vpdPartsListDTO);
            data.setRecent(true);
            vpdPartsListRepo.save(data);
        });
    }

    @Transactional
    public void changeMaterialIssueList() {
        List<MaterialIssueList> issueLists = materialIssueListRepo.findAll();
        issueLists.forEach(MaterialIssueList::updateSnapshot);
    }

}
