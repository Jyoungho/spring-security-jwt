package app.project.jwt.auth.service;

import app.project.jwt.auth.dto.AuthGetNewAccessTokenDTO;
import app.project.jwt.auth.dto.AuthLoginDTO;
import app.project.jwt.auth.dto.AuthTokenDTO;
import app.project.jwt.auth.entity.AuthToken;
import app.project.jwt.auth.repository.AuthTokenRepository;
import app.project.jwt.core.handler.exception.BizException;
import app.project.jwt.core.security.JwtTokenService;
import app.project.jwt.user.entity.User;
import app.project.jwt.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인요청 시 accessToken, refreshToken 갱신 후 반환
     * 예외처리 : 아이디 비밀번호 잘못입력
     */
    public AuthTokenDTO login(AuthLoginDTO authLoginDTO) {
        User user = userRepository.findByUserId(authLoginDTO.getUserId())
                .orElseThrow(() -> BizException.
                        withUserMessageKey("exception.auth.not.correct")
                        .build());
        if (!passwordEncoder.matches(authLoginDTO.getPassword(), user.getPassword())) {
            throw BizException.
                    withUserMessageKey("exception.auth.not.correct")
                    .build();
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authLoginDTO.getUserId(),
                authLoginDTO.getPassword()));
        return createTokenReturn(authLoginDTO);
    }

    /**
     * refreshToken 정보를 기반으로 accessToken 생성
     * 예외처리 : 요청 refreshToken 이 DB refreshToken 과 값이 일치하지 않는 경우
     * 예외처리 : 요청 refreshTokenId 가 DB 에 존재하지 않을 경우
     * 예외처리 : refreshToken 이 유효하지 않을 경우
     */
    public AuthTokenDTO newAccessToken(AuthGetNewAccessTokenDTO authGetNewAccessTokenDTO, HttpServletRequest request) {
        Optional<AuthToken> byId = authTokenRepository.findById(authGetNewAccessTokenDTO.getId());

        // 요청 refreshToken
        String requestRefreshToken = authGetNewAccessTokenDTO.getRefreshToken();

        if (byId.isPresent()) {
            String refreshToken = byId.get().getRefreshToken();

            // 요청 refreshToken 과 DB refreshToken 값이 일치하지 않으면 예외처리
            if (!refreshToken.equals(requestRefreshToken)) {
                throw BizException
                        .withUserMessageKey("exception.refresh.token.not.equal")
                        .build();
            }

            // refreshToken 이 유효할 경우 accessToken 반환
            if (jwtTokenService.validateJwtToken(request, refreshToken)) {
                String userId = jwtTokenService.getUserInfo(refreshToken);
                AuthLoginDTO authLoginDTO = AuthLoginDTO.builder()
                        .userId(userId)
                        .build();
                return createTokenReturn(authLoginDTO);

            } else {
                // refreshToken 유효하지 않을 경우 예외처리
                throw BizException
                        .withUserMessageKey("exception.refresh.token.not.validate")
                        .build();
            }
        } else {
            // refreshToken 을 찾을 수 없음
            throw BizException
                    .withUserMessageKey("exception.refresh.token.not.found")
                    .build();
        }
    }

    /**
     * userId 기반으로 accessToken, refreshToken 생성
     */
    public AuthTokenDTO createTokenReturn(AuthLoginDTO authLoginDTO) {
        String accessToken = jwtTokenService.createAccessToken(authLoginDTO);
        AuthTokenDTO authTokenDTO = jwtTokenService.createRefreshToken(authLoginDTO);
        AuthToken authToken;

        Optional<AuthToken> byUserId = authTokenRepository.findByUserId(authLoginDTO.getUserId());
        if (byUserId.isPresent()) {
            authToken = byUserId.get();
            authToken.updateToken(
                    accessToken,
                    authTokenDTO.getRefreshToken(),
                    authTokenDTO.getRefreshTokenExpirationDate()
            );
        } else {
            authToken = AuthToken.builder()
                    .userId(authLoginDTO.getUserId())
                    .accessToken(accessToken)
                    .refreshToken(authTokenDTO.getRefreshToken())
                    .refreshTokenExpirationDate(authTokenDTO.getRefreshTokenExpirationDate())
                    .build();
        }

        return AuthTokenDTO.from(authTokenRepository.save(authToken));
    }
}
