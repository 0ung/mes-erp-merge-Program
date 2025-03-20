package codehows.com.daehoint.service;

import codehows.com.daehoint.dto.HolidayResponse;
import codehows.com.daehoint.dto.MemberResponse;
import codehows.com.daehoint.dto.StandardInfoResponse;
import codehows.com.daehoint.entity.Holidays;
import codehows.com.daehoint.entity.Member;
import codehows.com.daehoint.entity.StandardInfo;
import codehows.com.daehoint.excpetion.NotFoundItemException;
import codehows.com.daehoint.excpetion.NotFoundUserException;
import codehows.com.daehoint.repository.HolidayRepo;
import codehows.com.daehoint.repository.MemberRepo;
import codehows.com.daehoint.repository.StandardInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ManageService 클래스
 *
 * <p>이 클래스는 멤버 관리, 기준 정보 관리, 공휴일 관리를 위한 서비스입니다.
 * 멤버와 기준 정보를 조회, 수정, 초기화할 수 있으며, 공휴일 데이터를 생성, 조회, 수정, 삭제하는 기능을 제공합니다.</p>
 *
 * 주요 기능:
 * <ul>
 *   <li>멤버 목록 조회 및 수정</li>
 *   <li>기준 정보 조회 및 업데이트</li>
 *   <li>비밀번호 초기화</li>
 *   <li>공휴일 데이터 생성, 조회, 수정, 삭제</li>
 * </ul>
 *
 * 주요 메서드:
 * <ul>
 *   <li>{@code getMember()}: 멤버 목록을 조회</li>
 *   <li>{@code updateMember(MemberResponse response)}: 멤버 정보를 업데이트</li>
 *   <li>{@code resetPassword(String id)}: 멤버의 비밀번호를 초기화</li>
 *   <li>{@code getStandardInfo()}: 기준 정보를 조회</li>
 *   <li>{@code updateStandardInfo(StandardInfoResponse standardInfoResponse)}: 기준 정보를 업데이트</li>
 *   <li>{@code createHoliday(HolidayResponse holidayResponse)}: 새로운 공휴일 데이터를 생성</li>
 *   <li>{@code getHoliday()}: 공휴일 목록을 조회</li>
 *   <li>{@code updateHoliday(HolidayResponse holidayResponse)}: 공휴일 데이터를 업데이트</li>
 *   <li>{@code deleteHoliday(Long id)}: 공휴일 데이터를 삭제</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ManageService {

	private final MemberRepo memberRepo;
	private final StandardInfoRepo standardInfoRepo;
	private final PasswordEncoder passwordEncoder;
	private final HolidayRepo holidayRepo;

	public List<MemberResponse> getMember() {
		List<Member> members = memberRepo.findAll();
		if (members.isEmpty()) {
			throw new NotFoundItemException("멤버 목록이 존재하지 않음");
		}

		List<MemberResponse> result = new ArrayList<>();
		members.forEach(e -> {
			result.add(MemberResponse.toDTO(e));
		});
		return result;
	}

	public StandardInfoResponse getStandardInfo() {
		StandardInfo standardInfo = standardInfoRepo.findById(1L).orElse(null);
		if (standardInfo == null) {
			throw new NotFoundItemException("기준정보가 존재하지 않음");
		}
		return StandardInfoResponse.toDTO(standardInfo);
	}

	@Transactional
	public void updateStandardInfo(StandardInfoResponse standardInfoResponse) {
		StandardInfo standardInfo = standardInfoRepo.findById(1L).orElse(null);
		if (standardInfo == null) {
			throw new NotFoundItemException("기준정보가 존재하지 않음");
		}
		standardInfo.updateStandardInfo(standardInfoResponse);
	}

	@Transactional
	public void updateMember(MemberResponse response) {
		Member member = memberRepo.findById(response.getId()).orElse(null);
		if (member == null) {
			throw new NotFoundUserException();
		}

		member.updateMember(response);
	}

	@Transactional
	public void resetPassword(String id) {
		Member member = memberRepo.findById(id).orElse(null);
		if (member == null) {
			throw new NotFoundUserException();
		}
		member.resetPassword(passwordEncoder);
	}

	@Transactional
	public void createHoliday(HolidayResponse holidayResponse) {
		holidayRepo.save(HolidayResponse.to(holidayResponse));
	}

	@Transactional
	public List<HolidayResponse> getHoliday() {
		List<Holidays> holidayResponses = holidayRepo.findAll();
		List<HolidayResponse> list = new ArrayList<>();

		holidayResponses.forEach(holidays -> {
			list.add(HolidayResponse.to(holidays));
		});
		return list;
	}

	@Transactional
	public void updateHoliday(HolidayResponse holidayResponse) {
		Holidays holidays = holidayRepo.findById(holidayResponse.getId()).orElse(null);
		if (holidays == null) {
			throw new NotFoundItemException();
		}
		holidays.updateHoliday(holidayResponse);
	}

	@Transactional
	public void deleteHoliday(Long id) {
		holidayRepo.deleteById(id);
	}

}
