package com.ceos19.everytime.auth.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ceos19.everytime.auth.dto.response.ReissueResponse;
import com.ceos19.everytime.jwt.JwtUtil;
import com.ceos19.everytime.jwt.RefreshToken;
import com.ceos19.everytime.jwt.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final String REFRESH = "refresh";
    private static final String ACCESS = "access";

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshRepository;

    @Transactional
    public ReissueResponse reissue(String refresh) {
        validateRefreshToken(refresh);

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // make new JWT
        String newAccess = jwtUtil.createJwt(ACCESS, username, role);
        String newRefresh = jwtUtil.createJwt(REFRESH, username, role);

        // Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefreshToken(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);

        log.info("reissue success");

        return new ReissueResponse(newAccess, newRefresh);
    }

    private void validateRefreshToken(String refresh) {
        validateRefreshTokenNull(refresh);
        jwtUtil.validateTokenExpired(refresh);
        validateRefreshTokenCategory(refresh);
        validateRefreshTokenValidity(refresh);
    }

    private void validateRefreshTokenValidity(String refresh) {
        if (!refreshRepository.existsByRefreshToken(refresh)) {
            throw new IllegalArgumentException("invalid refresh token");
        }
    }

    private void validateRefreshTokenCategory(String refresh) {
        if (!jwtUtil.isEqualToRefreshTokenCategory(refresh)) {
            throw new IllegalArgumentException("invalid refresh token");
        }
    }

    private void validateRefreshTokenNull(String refresh) {
        if (refresh == null) {
            throw new IllegalArgumentException("refresh token null");
        }
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshEntity = new RefreshToken(username, refresh, date.toString());

        refreshRepository.save(refreshEntity);
    }
}
