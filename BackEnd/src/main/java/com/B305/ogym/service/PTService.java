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

    // ?????? ??????
    @Transactional
    public void makeReservation(String ptStudentEmail, reservationRequest request) {

        String ptTeacherEmail = request.getPtTeacherEmail();
        LocalDateTime time = request.getReservationTime();
        String description = request.getDescription();
        // ????????? ?????? ?????? ??? ??????
        PTTeacher ptTeacher = ptTeacherRepository.findByEmail(ptTeacherEmail)
            .orElseThrow(() -> new UserNotFoundException("TEACHER"));

        // ?????? ?????? ?????? ??? ??????
        PTStudent ptStudent = ptStudentRepository.findByEmail(ptStudentEmail)
            .orElseThrow(() -> new UserNotFoundException("???????????? ???????????? ???????????? ????????????."));

        // ?????? ????????? ????????? ?????? ?????????
        try {
            PTStudentPTTeacher ptStudentPTTeacher = ptStudent
                .makeReservation(ptTeacher, ptStudent, time, description);
            ptStudentPTTeacherRepository.save(ptStudentPTTeacher);
        } catch (RuntimeException ex) {
            throw new DuplicateRequestException("????????? ??????");
        }
    }

    // ?????? ??????
    @Transactional
    public void cancelReservation(String ptStudentEmail, reservationRequest request) {

        // ???????????? ????????? ?????? ??? ??????
        PTStudent ptStudent = ptStudentRepository.findByEmail(ptStudentEmail)
            .orElseThrow(() -> new UserNotFoundException("???????????? ????????? ??????"));

        // ????????? ?????? ?????? ??? ??????
        PTTeacher ptTeacher = ptTeacherRepository.findByEmail(request.getPtTeacherEmail())
            .orElseThrow(() -> new UserNotFoundException("?????? ????????? ??????????????????."));

        // ???????????? ?????? ??? ??????
        PTStudentPTTeacher ptStudentPTTeacher = ptStudentPTTeacherRepository
            .findByPtTeacherAndPtStudentAndReservationDate(ptTeacher, ptStudent,
                request.getReservationTime())
            .orElseThrow(() -> new ReservationNotFoundException("CANCEL_RESERVATION"));

        ptStudentPTTeacherRepository.delete(ptStudentPTTeacher);

    }

    // ????????? ????????? ??????
    @Cacheable(cacheNames = "teacherList", key = "#searchDto.hashCode() + #pageable.pageNumber")
    @Transactional
    public AllTeacherListResponse getTeacherList(SearchDto searchDto, Pageable pageable) {

        // ?????? ??????
        RestResponsePage<PTTeacher> ptTeachers = ptTeacherRepository.searchAll(searchDto, pageable);

        // PTTeacher ?????? ????????? ????????? ?????? PTTeacherDto??? ??????
        List<PTTeacherDto> ptTeacherDtos = new ArrayList<>();
        for (int i = 0; i < ptTeachers.getNumberOfElements(); i++) {
            PTTeacher ptTeacher = ptTeachers.getContent().get(i);
            ptTeacherDtos.add(toPTTeacherDto(ptTeacher));
        }

        // Content??? Paging ????????? ?????? Response ????????? ?????? ??????

        return AllTeacherListResponse.builder()
            .teacherList(ptTeacherDtos)
            .totalPages(ptTeachers.getTotalPages())
            .totalElements(ptTeachers.getTotalElements())
            .numberOfElements(ptTeachers.getNumberOfElements())
            .build();
    }

    // ???????????? PT ??????????????? ??????
    public List<LocalDateTime> getTeacherReservationTime(String teacherEmail) {
        if (!userRepository.existsByEmail(teacherEmail)) {
            throw new UserNotFoundException("?????? ???????????? ???????????? ????????????.");
        }
        return ptTeacherRepository.reservationTime(teacherEmail);
    }

    // ?????? ????????? PT ??????????????? ??????
    public List<reservationDto> getReservationTime(String email) {
        UserBase user = userRepository.findByEmail(email).orElseThrow(() ->
            new UserNotFoundException("???????????? ???????????? ???????????? ????????????."));
        List<reservationDto> result = new ArrayList<>();
        if (ROLE_PTTEACHER.equals(user.getAuthority().getAuthorityName())) {
            ptTeacherRepository.getReservationTime(email).forEach(
                o -> {
                    result.add(
                        reservationDto.builder()
                            .description(o.getDescription())
                            .reservationTime(o.getReservationDate())
                            .nickname(o.getPtStudent().getNickname())
                            .username(o.getPtStudent().getUsername())
                            .profileUrl((String)o.getPtStudent().getInfo("profilePictureURL"))
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
                            .profileUrl((String)o.getPtTeacher().getInfo("profilePictureURL"))
                            .email(o.getPtTeacher().getEmail())
                            .build()
                    );
                }
            );
        }
        return result;
    }

    // ?????? ??????????????? ??????
    public nowReservationDto getNowReservation(String userEmail) {
        String teacherEmail = null;
        String studentEmail = null;
        UserBase user = userRepository.findByEmail(userEmail).orElseThrow();
        if (ROLE_PTTEACHER.equals(user.getAuthority().getAuthorityName())) {
            teacherEmail = userEmail;
        } else {
            studentEmail = userEmail;
        }
        List<String> nowReservation = ptStudentPTTeacherRepository
            .getNowReservation(teacherEmail, studentEmail, LocalDateTime.now());
        if (nowReservation == null) {
            return null;
        }
        nowReservationDto result = nowReservationDto.builder()
            .teacherNickname(nowReservation.get(0))
            .studentNickname(nowReservation.get(1))
            .build();
        return result;
    }

    // ????????? ????????? ??????(????????? ????????? ??????)
    public PTTeacherDto toPTTeacherDto(PTTeacher ptTeacher) {

        List<Certificate> certificates = ptTeacher.getCertificates();
        List<CertificateDto> certificateDtos = new ArrayList<>();

        for (int i = 0; i < certificates.size(); i++) {
            Certificate certificate = certificates.get(i);
            CertificateDto certificateDto = CertificateDto.builder()
                .name(certificate.getName())
                .date(certificate.getDate())
                .publisher(certificate.getPublisher())
                .build();
            certificateDtos.add(certificateDto);
        }

        List<Career> careers = ptTeacher.getCareers();
        List<CareerDto> careerDtos = new ArrayList<>();

        for (int i = 0; i < careers.size(); i++) {
            Career career = careers.get(i);
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

        for (int i = 0; i < ptStudentPTTeachersList.size(); i++) {
            PTStudentPTTeacher ptStudentPTTeacher = ptStudentPTTeachersList.get(i);
            reservations.add(ptStudentPTTeacher.getReservationDate());
        }

        Collections.sort(reservations);

        List<Sns> snsList = ptTeacher.getSnss();
        List<SnsDto> snsDtos = new ArrayList<>();

        for (int i = 0; i < snsList.size(); i++) {
            Sns sns = snsList.get(i);
            SnsDto snsDto = SnsDto.builder()
                .platform(sns.getPlatform())
                .url(sns.getUrl())
                .build();
            snsDtos.add(snsDto);
        }

        PTTeacherDto ptTeacherDto = PTTeacherDto.builder().username(ptTeacher.getUsername())
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

        return ptTeacherDto;

    }


}
