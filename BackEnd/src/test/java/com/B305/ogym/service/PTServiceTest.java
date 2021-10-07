package com.B305.ogym.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.B305.ogym.common.util.RestResponsePage;
import com.B305.ogym.controller.dto.PTDto.SearchDto;
import com.B305.ogym.controller.dto.PTDto.reservationRequest;
import com.B305.ogym.controller.dto.UserDto.SaveUserRequest;
import com.B305.ogym.domain.mappingTable.PTStudentPTTeacher;
import com.B305.ogym.domain.mappingTable.PTStudentPTTeacherRepository;
import com.B305.ogym.domain.users.UserRepository;
import com.B305.ogym.domain.users.ptStudent.PTStudent;
import com.B305.ogym.domain.users.ptStudent.PTStudentRepository;
import com.B305.ogym.domain.users.ptTeacher.PTTeacher;
import com.B305.ogym.domain.users.ptTeacher.PTTeacherRepository;
import com.B305.ogym.exception.user.UserNotFoundException;
import com.sun.jdi.request.DuplicateRequestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PTServiceTest {

    @Mock
    PTTeacherRepository ptTeacherRepository;
    @Mock
    PTStudentRepository ptStudentRepository;
    @Mock
    PTStudentPTTeacherRepository ptStudentPTTeacherRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    PTService ptService;

    private SaveUserRequest createTeacherRequest() {
        return SaveUserRequest.builder()
            .email("hello@naver.com")
            .password("asdasd")
            .username("juhu")
            .nickname("주현")
            .gender(0)
            .tel("010-0000-0000")
            .zipCode("12345")
            .street("road 17")
            .detailedAddress("juhu")
            .role("ROLE_PTTEACHER")
            .major("재활")
            .certificates(new ArrayList<>())
            .careers(new ArrayList<>())
            .price(1000)
            .description("트레이너")
            .build();
    }

    private SaveUserRequest createStudentRequest() {
        return SaveUserRequest.builder()
            .email("hello@naver.com")
            .password("비밀번호")
            .username("한솥")
            .nickname("닉네임")
            .gender(0)
            .tel("010-0000-0000")
            .zipCode("12345")
            .street("도로명")
            .detailedAddress("상세주소")
            .role("ROLE_PTSTUDENT")
            .monthlyWeights(new ArrayList<Integer>(Arrays
                .asList(180, 200, 210, 180, 200, 210, 180, 200, 210, 180, 200, 210)))
            .monthlyHeights(new ArrayList<Integer>(Arrays
                .asList(180, 200, 210, 180, 200, 210, 180, 200, 210, 180, 200, 210)))
            .build();
    }

    private PTStudent createStudent() {
        return createStudentRequest().toPTStudentEntity();
    }

    private PTTeacher createTeacher() {
        return createTeacherRequest().toPTTeacherEntity();
    }

    private reservationRequest createReservation() {
        return reservationRequest.builder()
            .description("하체운동")
            .ptTeacherEmail("teacher@naver.com")
            .build();
    }

    @DisplayName("PT 예약 - 모든 제약 조건을 충족시키고 성공")
    @Test
    public void makeReservation_success() throws Exception {
        //given
        String ptStudentEmail = "student@naver.com";
        PTTeacher teacher = createTeacher();
        PTStudent student = createStudent();
        reservationRequest request = createReservation();
        given(ptTeacherRepository.findByEmail(request.getPtTeacherEmail())).willReturn(
            Optional.ofNullable(teacher));
        given(ptStudentRepository.findByEmail(ptStudentEmail)).willReturn(
            Optional.ofNullable(student));
        //when
        ptService.makeReservation(ptStudentEmail, request);
        //then
        verify(ptTeacherRepository, atLeastOnce()).findByEmail(request.getPtTeacherEmail());
        verify(ptStudentRepository, atLeastOnce()).findByEmail(ptStudentEmail);
    }

    @DisplayName("PT 예약 - 예약할 유저가 이미 탈퇴하여 실패")
    @Test
    public void makeReservation_studentNotFound() throws Exception {
        //given
        String ptStudentEmail = "student@naver.com";
        PTTeacher teacher = createTeacher();
        PTStudent student = createStudent();
        reservationRequest request = createReservation();
        given(ptTeacherRepository.findByEmail(request.getPtTeacherEmail())).willReturn(
            Optional.ofNullable(teacher));
        given(ptStudentRepository.findByEmail(ptStudentEmail))
            .willThrow(new UserNotFoundException("이미 탈퇴한 유저입니다."));
        //when
        assertThrows(UserNotFoundException.class,
            () -> ptService.makeReservation(ptStudentEmail, request));
        //then
        verify(ptTeacherRepository, atLeastOnce()).findByEmail(request.getPtTeacherEmail());
        verify(ptStudentRepository, atLeastOnce()).findByEmail(ptStudentEmail);
    }

    @DisplayName("PT 예약 - 예약할 선생님이 이미 탈퇴하여 실패")
    @Test
    public void makeReservation_teacherNotFound() throws Exception {
        //given
        String ptStudentEmail = "student@naver.com";
        PTStudent student = createStudent();
        reservationRequest request = createReservation();
        given(ptTeacherRepository.findByEmail(request.getPtTeacherEmail()))
            .willThrow(new UserNotFoundException("이미 탈퇴한 트레이너입니다.."));

        //when
        assertThrows(UserNotFoundException.class,
            () -> ptService.makeReservation(ptStudentEmail, request));
        //then
        verify(ptTeacherRepository, atLeastOnce()).findByEmail(request.getPtTeacherEmail());
    }

    @DisplayName("PT 예약 - 중복 예약으로 인한 실패")
    @Test
    public void makeReservation_duplicateReservation() throws Exception {
        //given
        String ptStudentEmail = "student@naver.com";
        PTTeacher teacher = createTeacher();
        PTStudent student = createStudent();
        PTStudentPTTeacher ptStudentPTTeacher = PTStudentPTTeacher.builder().build();
        reservationRequest request = createReservation();
        given(ptTeacherRepository.findByEmail(request.getPtTeacherEmail())).willReturn(
            Optional.ofNullable(teacher));
        given(ptStudentRepository.findByEmail(ptStudentEmail)).willReturn(
            Optional.ofNullable(student));
        given(ptStudentPTTeacherRepository.save(ptStudentPTTeacher))
            .willThrow(new RuntimeException());

        //when
        assertThrows(DuplicateRequestException.class,
            () -> ptService.makeReservation(ptStudentEmail, request));
        //then
        verify(ptTeacherRepository, atLeastOnce()).findByEmail(request.getPtTeacherEmail());
        verify(ptStudentRepository, atLeastOnce()).findByEmail(ptStudentEmail);
    }

    @DisplayName("PT 예약 취소 - 성공")
    @Test
    public void cancelReservation_success() throws Exception {
        //given
        String ptStudentEmail = "student@naver.com";
        PTTeacher teacher = createTeacher();
        PTStudent student = createStudent();
        PTStudentPTTeacher ptStudentPTTeacher = PTStudentPTTeacher.builder().build();
        reservationRequest request = createReservation();
        given(ptTeacherRepository.findByEmail(request.getPtTeacherEmail())).willReturn(
            Optional.ofNullable(teacher));
        given(ptStudentRepository.findByEmail(ptStudentEmail)).willReturn(
            Optional.ofNullable(student));
        given(ptStudentPTTeacherRepository
            .findByPtTeacherAndPtStudentAndReservationDate(teacher, student,
                request.getReservationTime())).willReturn(
            Optional.ofNullable(ptStudentPTTeacher));
        //when
        ptService.cancelReservation(ptStudentEmail, request);
        //then
        verify(ptTeacherRepository, atLeastOnce()).findByEmail(request.getPtTeacherEmail());
        verify(ptStudentRepository, atLeastOnce()).findByEmail(ptStudentEmail);
        verify(ptStudentPTTeacherRepository, atLeastOnce())
            .findByPtTeacherAndPtStudentAndReservationDate(teacher, student,
                request.getReservationTime());
    }

    @DisplayName("PT 예약 취소 - 예약 학생이 이미 탈퇴하여 실패")
    @Test
    public void cancelReservation_studentNotFound() throws Exception {
        //given
        String ptStudentEmail = "student@naver.com";
        reservationRequest request = createReservation();
        given(ptStudentRepository.findByEmail(ptStudentEmail))
            .willThrow(new UserNotFoundException("이미 탈퇴한 학생입니다."));
        //when
        assertThrows(UserNotFoundException.class,
            () -> ptService.cancelReservation(ptStudentEmail, request));
        //then
        verify(ptStudentRepository, atLeastOnce()).findByEmail(ptStudentEmail);

    }

    @DisplayName("PT 예약 취소 - 예약 선생님이 이미 탈퇴하여 실패")
    @Test
    public void cancelReservation_teacherNotFound() throws Exception {
        //given
        String ptStudentEmail = "student@naver.com";
        PTStudent student = createStudent();
        reservationRequest request = createReservation();
        given(ptStudentRepository.findByEmail(ptStudentEmail)).willReturn(
            Optional.ofNullable(student));
        given(ptTeacherRepository.findByEmail(request.getPtTeacherEmail()))
            .willThrow(new UserNotFoundException("이미 탈퇴한 트레이너입니다."));
        //when
        assertThrows(UserNotFoundException.class,
            () -> ptService.cancelReservation(ptStudentEmail, request));
        //then
        verify(ptTeacherRepository, atLeastOnce()).findByEmail(request.getPtTeacherEmail());
        verify(ptStudentRepository, atLeastOnce()).findByEmail(ptStudentEmail);

    }

    @DisplayName("PT 예약 취소 - 예약 정보를 찾을 수 없어 실패")
    @Test
    public void cancelReservation_reservationNotFound() throws Exception {
        //given
        String ptStudentEmail = "student@naver.com";
        PTTeacher teacher = createTeacher();
        PTStudent student = createStudent();
        reservationRequest request = createReservation();
        given(ptTeacherRepository.findByEmail(request.getPtTeacherEmail())).willReturn(
            Optional.ofNullable(teacher));
        given(ptStudentRepository.findByEmail(ptStudentEmail)).willReturn(
            Optional.ofNullable(student));
        given(ptStudentPTTeacherRepository
            .findByPtTeacherAndPtStudentAndReservationDate(teacher, student,
                request.getReservationTime()))
            .willThrow(new UserNotFoundException("CANCEL_RESERVATION"));
        //when
        assertThrows(UserNotFoundException.class,
            () -> ptService.cancelReservation(ptStudentEmail, request));
        //then
        verify(ptTeacherRepository, atLeastOnce()).findByEmail(request.getPtTeacherEmail());
        verify(ptStudentRepository, atLeastOnce()).findByEmail(ptStudentEmail);
        verify(ptStudentPTTeacherRepository, atLeastOnce())
            .findByPtTeacherAndPtStudentAndReservationDate(teacher, student,
                request.getReservationTime());
    }

    @DisplayName("선생님 리스트 출력 - 성공")
    @Test
    public void getTeacherList_success() throws Exception {
        //given
        SearchDto searchDto = SearchDto.builder().build();
        RestResponsePage<PTTeacher> ptTeachers = new RestResponsePage<>();
        given(ptTeacherRepository.searchAll(searchDto, Pageable.ofSize(10))).willReturn(ptTeachers);
        //when
        ptService.getTeacherList(searchDto, Pageable.ofSize(10));
        //then
        verify(ptTeacherRepository, atLeastOnce()).searchAll(searchDto, Pageable.ofSize(10));
    }

    @DisplayName("선생님 예약된 시간 출력 - 성공")
    @Test
    public void getTeacherReservationTime_success() throws Exception {
        //given
        String teacherEmail = "teacher@naver.com";
        given(userRepository.existsByEmail(teacherEmail)).willReturn(true);
        //when
        ptService.getTeacherReservationTime(teacherEmail);
        //then
        verify(userRepository, atLeastOnce()).existsByEmail(teacherEmail);
    }

    @DisplayName("선생님 예약된 시간 출력 - 이미 탈퇴한 선생님 회원으로 인한 실패")
    @Test
    public void getTeacherReservationTime_teacherNotFound() throws Exception {
        //given
        String teacherEmail = "teacher@naver.com";
        given(userRepository.existsByEmail(teacherEmail))
            .willThrow(new UserNotFoundException("이미 탈퇴한 트레이너입니다."));
        //when
        assertThrows(UserNotFoundException.class,
            () -> ptService.getTeacherReservationTime(teacherEmail));
        //then
        verify(userRepository, atLeastOnce()).existsByEmail(teacherEmail);
    }

    @DisplayName("유저 예약된 시간 출력 - 성공")
    @Test
    public void getReservationTime_success() throws Exception {
        //given
        var user = createStudent();
        user.setRole("ROLE_PTSTUDENT");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        //when
        ptService.getReservationTime(user.getEmail());
        //then
        verify(userRepository, atLeastOnce()).findByEmail(user.getEmail());
    }

    @DisplayName("유저 예약된 시간 출력 - 이미 탈퇴한 회원의 요청으로 인한 실패")
    @Test
    public void getReservationTime_userNotFound() throws Exception {
        //given
        var user = createStudent();
        user.setRole("ROLE_PTSTUDENT");
        given(userRepository.findByEmail(user.getEmail()))
            .willThrow(new UserNotFoundException("이미 탈퇴한 유저입니다."));
        //when
        assertThrows(UserNotFoundException.class,
            () -> ptService.getReservationTime(user.getEmail()));
        //then
        verify(userRepository, atLeastOnce()).findByEmail(user.getEmail());
    }
}