package com.B305.ogym.controller;

import static com.B305.ogym.common.util.constants.ResponseConstants.OK;
import com.B305.ogym.controller.dto.AuthDto;
import com.B305.ogym.controller.dto.AuthDto.TokenDto;
import com.B305.ogym.service.AuthService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    /*
     * 로그인을 했을때 토큰(AccessToken, RefreshToken)을 주는 메서드
     */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> login(
        @RequestBody @Valid AuthDto.LoginDto loginDto) {
        TokenDto tokenDto = authService.authorize(loginDto.getEmail(), loginDto.getPassword());
        return ResponseEntity.ok(tokenDto);
    }

    /*
     * AccessToken이 만료되었을 때 토큰(AccessToken , RefreshToken)재발급해주는 메서드
     */
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(
        @RequestBody @Valid TokenDto requestTokenDto) {
        TokenDto tokenDto = authService
            .reissue(requestTokenDto.getAccessToken(), requestTokenDto.getRefreshToken());
        return ResponseEntity.ok(tokenDto);
    }

    /*
       로그아웃을 했을 때 토큰을 받아 BlackList에 저장하는 메서드
     */
    @DeleteMapping("/authenticate")
    public ResponseEntity<Void> logout(
        @RequestBody @Valid TokenDto requestTokenDto) {
        authService.logout(requestTokenDto.getAccessToken(), requestTokenDto.getRefreshToken());
        return OK;
    }
}
