package com.B305.ogym.controller;

import static com.B305.ogym.common.util.constants.ResponseConstants.CREATED;
import static com.B305.ogym.common.util.constants.ResponseConstants.OK;

import com.B305.ogym.controller.dto.SuccessResponseDto;
import com.B305.ogym.controller.dto.UserDto;
import com.B305.ogym.controller.dto.UserDto.ProfileDto;
import com.B305.ogym.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    /*
     * 회원탈퇴 기능을 구현한 메서드
     */
    @DeleteMapping
    @PreAuthorize("hasAnyRole('PTTEACHER','PTSTUDENT')")
    public ResponseEntity<Void> deleteMyUser(
        @AuthenticationPrincipal String userEmail, HttpServletRequest req
    ) {
        userService.deleteUserBase(userEmail, req.getHeader("Authorization").substring(7));
        return OK;
    }

    /*
     * 회원가입 기능을 구현한 메서드
     */
    @PostMapping
    public ResponseEntity<Void> signup(
        @RequestBody @Valid UserDto.SaveUserRequest userRequestDto) {
        userService.signup(userRequestDto);
        return CREATED;
    }


    /*
     * 사용자의 회원정보를 조회하기 위한 메서드
     */
    @GetMapping("/{req}")
    @PreAuthorize("hasAnyRole('PTTEACHER','PTSTUDENT')")
    public ResponseEntity<Map<String, Object>> getUserInfo(
        @AuthenticationPrincipal String userEmail,
        @PathVariable @NotEmpty List<String> req) {
        return ResponseEntity.ok(userService.getUserInfo(userEmail, req));
    }

    /*
     * 프로필 사진 변경을 위한 메서드
     */
    @PatchMapping
    @PreAuthorize("hasAnyRole('PTTEACHER', 'PTSTUDENT')")
    public ResponseEntity<Void> putProfile(
        @AuthenticationPrincipal String userEmail,
        @RequestBody ProfileDto profileDto
    ) {
        userService.putProfile(userEmail, profileDto);
        return OK;
    }
}
