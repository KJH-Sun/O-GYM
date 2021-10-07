package com.B305.ogym.controller;

import static com.B305.ogym.common.util.constants.ResponseConstants.CREATED;
import static com.B305.ogym.common.util.constants.ResponseConstants.OK;

import com.B305.ogym.controller.dto.PTDto;
import com.B305.ogym.controller.dto.PTDto.AllTeacherListResponse;
import com.B305.ogym.controller.dto.PTDto.SearchDto;
import com.B305.ogym.controller.dto.PTDto.nowReservationDto;
import com.B305.ogym.controller.dto.PTDto.reservationDto;
import com.B305.ogym.controller.dto.SuccessResponseDto;
import com.B305.ogym.domain.users.common.UserBase;
import com.B305.ogym.service.PTService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pt")
@RequiredArgsConstructor
public class PTApiController {

    private final PTService ptService;

    // 선생님 리스트 출력
    @GetMapping("/teacherlist")
    @PreAuthorize("hasAnyRole('PTSTUDENT', 'PTTEACHER')")
    public ResponseEntity<AllTeacherListResponse> teacherList(
        SearchDto searchDto,
        @PageableDefault(sort = "username") final Pageable pageable) {
        return ResponseEntity.ok(ptService.getTeacherList(searchDto, pageable));
    }

    // PT 예약 생성
    @PostMapping("/reservation")
    @PreAuthorize("hasAnyRole('PTSTUDENT')")
    public ResponseEntity<Void> makeReservation(
        @AuthenticationPrincipal String userEmail,
        @RequestBody @Valid PTDto.reservationRequest request) {

        ptService.makeReservation(userEmail, request);
        return CREATED;
    }

    // PT 예약 취소
    @DeleteMapping("/reservation")
    @PreAuthorize("hasAnyRole('PTSTUDENT')")
    public ResponseEntity<Void> cancelReservation(
        @AuthenticationPrincipal String userEmail,
        @RequestBody @Valid PTDto.reservationRequest request
    ) {
        ptService.cancelReservation(userEmail, request);
        return OK;
    }


    // PT 선생님에 대한 예약된 시간 조히
    @GetMapping("/reservation/{teacherEmail}")
    @PreAuthorize("hasAnyRole('PTTEACHER','PTSTUDENT')")
    public ResponseEntity<List<LocalDateTime>> getTeacherReservationTime(
        @PathVariable String teacherEmail
    ) {
        return ResponseEntity.ok(ptService.getTeacherReservationTime(teacherEmail));
    }

    // 자신의 예약정보 조회
    @GetMapping("/reservation")
    @PreAuthorize("hasAnyRole('PTTEACHER','PTSTUDENT')")
    public ResponseEntity<List<reservationDto>> getReservationTime(
        @AuthenticationPrincipal String userEmail
    ) {
        return ResponseEntity.ok(ptService.getReservationTime(userEmail));
    }

    // 현재 예약정보 조회
    @GetMapping("/nowreservation")
    @PreAuthorize("hasAnyRole('PTTEACHER','PTSTUDENT')")
    public ResponseEntity<nowReservationDto> getNowReservation(
        @AuthenticationPrincipal String userEmail
    ) {
        return ResponseEntity.ok(ptService.getNowReservation(userEmail));
    }
}
