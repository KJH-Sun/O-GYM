package com.B305.ogym.service;

import com.B305.ogym.common.util.RestResponsePage;
import com.B305.ogym.controller.dto.PTDto.AllTeacherListResponse;
import com.B305.ogym.controller.dto.PTDto.CareerDto;
import com.B305.ogym.controller.dto.PTDto.CertificateDto;
import com.B305.ogym.controller.dto.PTDto.PTTeacherDto;
import com.B305.ogym.controller.dto.PTDto.SearchDto;
import com.B305.ogym.controller.dto.PTDto.SnsDto;
import com.B305.ogym.controller.dto.PTDto.nowReservationDto;
import com.B305.ogym.controller.dto.PTDto.reservationDto;
import com.B305.ogym.controller.dto.PTDto.reservationRequest;
import com.B305.ogym.domain.mappingTable.PTStudentPTTeacher;
import com.B305.ogym.domain.mappingTable.PTStudentPTTeacherRepository;
import com.B305.ogym.domain.users.UserRepository;
import com.B305.ogym.domain.users.common.UserBase;
import com.B305.ogym.domain.users.ptStudent.PTStudent;
import com.B305.ogym.domain.users.ptStudent.PTStudentRepository;
import com.B305.ogym.domain.users.ptTeacher.Career;
import com.B305.ogym.domain.users.ptTeacher.Certificate;
import com.B305.ogym.domain.users.ptTeacher.PTTeacher;
import com.B305.ogym.domain.users.ptTeacher.PTTeacherRepository;
import com.B305.ogym.domain.users.ptTeacher.Sns;
import com.B305.ogym.exception.pt.ReservationNotFoundException;
import com.B305.ogym.exception.user.UserNotFoundException;
import com.sun.jdi.request.DuplicateRequestException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PTService {

    private final PTTeacherRepository ptTeacherRepository;
    private final PTStudentRepository ptStudentRepository;
    private final PTStudentPTTeacherRepository ptStudentPTTeacherRepository;
    private final UserRepository userRepository;

    private final String ROLE_PTTEACHER = "ROLE_PTTEACHER";
    private final String ROLE_PTSTUDENT = "ROLE_PTSTUDENT";

    // 예약 생성
    @Transactional
    public void makeReservation(String ptStudentEmail, reservationRequest request) {

        String ptTeacherEmail = request.getPtTeacherEmail();
        LocalDateTime time = request.getReservationTime();
        String description = request.getDescription();
        // 선생님 정보 찾을 수 없음
        PTTeacher ptTeacher = ptTeacherRepository.findByEmail(ptTeacherEmail)
            .orElseThrow(() -> new UserNotFoundException("TEACHER"));

        // 학생 정보 찾을 수 없음
        PTStudent ptStudent = ptStudentRepository.findByEmail(ptStudentEmail)
            .orElseThrow(() -> new UserNotFoundException("해당하는 이메일은 존재하지 않습니다."));

        // 해당 시간에 예약이 이미 존재함
        try {
            PTStudentPTTeacher ptStudentPTTeacher = ptStudent
                .makeReservation(ptTeacher, ptStudent, time, description);
            ptStudentPTTeacherRepository.save(ptStudentPTTeacher);
        } catch (RuntimeException ex) {
            throw new DuplicateRequestException("중복된 예약");
        }
    }

    // 예약 취소
    @Transactional
    public void cancelReservation(String ptStudentEmail, reservationRequest request) {

        // 로그인한 사용자 찾을 수 없음
        PTStudent ptStudent = ptStudentRepository.findByEmail(ptStudentEmail)
            .orElseThrow(() -> new UserNotFoundException("로그인한 사용자 없음"));

        // 선생님 정보 찾을 수 없음
        PTTeacher ptTeacher = ptTeacherRepository.findByEmail(request.getPtTeacherEmail())
            .orElseThrow(() -> new UserNotFoundException("이미 탈퇴한 선생님입니다."));

        // 예약정보 찾을 수 없음
        PTStudentPTTeacher ptStudentPTTeacher = ptStudentPTTeacherRepository
            .findByPtTeacherAndPtStudentAndReservationDate(ptTeacher, ptStudent,
                request.getReservationTime())
            .orElseThrow(() -> new ReservationNotFoundException("CANCEL_RESERVATION"));

        ptStudentPTTeacherRepository.delete(ptStudentPTTeacher);

    }

    // 선생님 리스트 출력
    @Cacheable(cacheNames = "teacherList", key = "#searchDto.hashCode() + #pageable.pageNumber")
    @Transactional
    public AllTeacherListResponse getTeacherList(SearchDto searchDto, Pageable pageable) {

        // 조건 검색
        RestResponsePage<PTTeacher> ptTeachers = ptTeacherRepository.searchAll(searchDto, pageable);

        // PTTeacher 에서 원하는 정보만 담아 PTTeacherDto로 변환
        List<PTTeacherDto> ptTeacherDtos = new ArrayList<>();
        for (int i = 0; i < ptTeachers.getNumberOfElements(); i++) {
            PTTeacher ptTeacher = ptTeachers.getContent().get(i);
            ptTeacherDtos.add(toPTTeacherDto(ptTeacher));
        }

        // Content와 Paging 정보를 함께 Response 객체에 담아 반환

        return AllTeacherListResponse.builder()
            .teacherList(ptTeacherDtos)
            .totalPages(ptTeachers.getTotalPages())
            .totalElements(ptTeachers.getTotalElements())
            .numberOfElements(ptTeachers.getNumberOfElements())
            .build();
    }

    // 선생님의 PT 예약정보를 조회
    public List<LocalDateTime> getTeacherReservationTime(String teacherEmail) {
        if (!userRepository.existsByEmail(teacherEmail)) {
            throw new UserNotFoundException("해당 이메일이 존재하지 않습니다.");
        }
        return ptTeacherRepository.reservationTime(teacherEmail);
    }

    // 특정 유저의 PT 예약정보를 조회
    public List<reservationDto> getReservationTime(String email) {
        UserBase user = userRepository.findByEmail(email).orElseThrow(() ->
            new UserNotFoundException("해당하는 이메일이 존재하지 않습니다."));
        List<reservationDto> result = new ArrayList<>();
        if (ROLE_PTTEACHER.equals(user.getAuthority())) {
            ptTeacherRepository.getReservationTime(email).forEach(
                o -> {
                    result.add(
                        reservationDto.builder()
                            .description(o.getDescription())
                            .reservationTime(o.getReservationDate())
                            .nickname(o.getPtStudent().getNickname())
                            .username(o.getPtStudent().getUsername())
                            .profileUrl((String) o.getPtStudent().getInfo("profilePictureURL"))
                            .email(o.getPtStudent().getEmail())
                            .build()
                    );
                }
            );
        } else {
            ptStudentRepository.getReservationTime(email).forEach(
                o -> {
                    result.add(
                        reservationDto.builder()
                            .description(o.getDescription())
                            .reservationTime(o.getReservationDate())
                            .nickname(o.getPtTeacher().getNickname())
                            .username(o.getPtTeacher().getUsername())
                            .profileUrl((String) o.getPtTeacher().getInfo("profilePictureURL"))
                            .email(o.getPtTeacher().getEmail())
                            .build()
                    );
                }
            );
        }
        return result;
    }

    // 현재 예약정보를 조회
    public nowReservationDto getNowReservation(String userEmail) {
        String teacherEmail = null;
        String studentEmail = null;
        UserBase user = userRepository.findByEmail(userEmail).orElseThrow();
        if (ROLE_PTTEACHER.equals(user.getAuthority())) {
            teacherEmail = userEmail;
        } else {
            studentEmail = userEmail;
        }
        List<String> nowReservation = ptStudentPTTeacherRepository
            .getNowReservation(teacherEmail, studentEmail, LocalDateTime.now());
        if (nowReservation == null) {
            return null;
        }
        return nowReservationDto.builder()
            .teacherNickname(nowReservation.get(0))
            .studentNickname(nowReservation.get(1))
            .build();
    }

    // 선생님 리스트 출력(원하는 정보만 담기)
    public PTTeacherDto toPTTeacherDto(PTTeacher ptTeacher) {

        List<Certificate> certificates = ptTeacher.getCertificates();
        List<CertificateDto> certificateDtos = new ArrayList<>();

        for (Certificate certificate : certificates) {
            CertificateDto certificateDto = CertificateDto.builder()
                .name(certificate.getName())
                .date(certificate.getDate())
                .publisher(certificate.getPublisher())
                .build();
            certificateDtos.add(certificateDto);
        }

        List<Career> careers = ptTeacher.getCareers();
        List<CareerDto> careerDtos = new ArrayList<>();

        for (Career career : careers) {
            CareerDto careerDto = CareerDto.builder()
                .company(career.getCompany())
                .startDate(career.getStartDate())
                .endDate(career.getEndDate())
                .role(career.getRole())
                .build();
            careerDtos.add(careerDto);
        }

        Set<PTStudentPTTeacher> ptStudentPTTeachers = ptTeacher.getPtStudentPTTeachers();
        List<PTStudentPTTeacher> ptStudentPTTeachersList = new ArrayList<>(ptStudentPTTeachers);
        List<LocalDateTime> reservations = new ArrayList<>();

        for (PTStudentPTTeacher ptStudentPTTeacher : ptStudentPTTeachersList) {
            reservations.add(ptStudentPTTeacher.getReservationDate());
        }

        Collections.sort(reservations);

        List<Sns> snsList = ptTeacher.getSnss();
        List<SnsDto> snsDtos = new ArrayList<>();

        for (Sns sns : snsList) {
            SnsDto snsDto = SnsDto.builder()
                .platform(sns.getPlatform())
                .url(sns.getUrl())
                .build();
            snsDtos.add(snsDto);
        }

        return PTTeacherDto.builder().username(ptTeacher.getUsername())
            .gender(ptTeacher.getGender())
            .nickname(ptTeacher.getNickname())
            .age(ptTeacher.getAge())
            .tel(ptTeacher.getTel())
            .email(ptTeacher.getEmail())
            .profilePicture(ptTeacher.getProfilePicture())
            .starRating(ptTeacher.getStarRating())
            .major(ptTeacher.getMajor())
            .price(ptTeacher.getPrice())
            .description(ptTeacher.getDescription())
            .certificates(certificateDtos)
            .careers(careerDtos)
            .reservations(reservations)
            .snsList(snsDtos)
            .build();

    }


}
