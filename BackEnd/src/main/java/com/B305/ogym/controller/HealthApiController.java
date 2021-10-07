package com.B305.ogym.controller;

import com.B305.ogym.controller.dto.HealthDto;
import com.B305.ogym.controller.dto.HealthDto.MyHealthResponse;
import com.B305.ogym.controller.dto.HealthDto.MyStudentsHealthListResponse;
import com.B305.ogym.controller.dto.SuccessResponseDto;
import com.B305.ogym.domain.users.common.UserBase;
import com.B305.ogym.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthApiController {

    private final HealthService healthService;

    // 선생님 : 학생의 건강정보 조회
    @GetMapping("/mystudents")
    @PreAuthorize("hasAnyRole('PTTEACHER')")
    public ResponseEntity<MyStudentsHealthListResponse> getMyStudentsHealth(
        @AuthenticationPrincipal String userEmail
    ) {
        return ResponseEntity.ok(healthService.findMyStudentsHealth(userEmail));
    }

    // 학생 : 자신의 건강정보 조회
    @GetMapping("/myhealth")
    @PreAuthorize("hasAnyRole('PTSTUDENT')")
    public ResponseEntity<MyHealthResponse> getMyHealth(
        @AuthenticationPrincipal String userEmail) {
        return ResponseEntity.ok(healthService.getMyHealth(userEmail));
    }
}
