package app.project.jwt.config;

import app.project.jwt.auth.dto.AuthLoginDTO;
import app.project.jwt.auth.dto.AuthTokenDTO;
import app.project.jwt.auth.entity.AuthToken;
import app.project.jwt.auth.repository.AuthTokenRepository;
import app.project.jwt.auth.service.AuthService;
import app.project.jwt.core.config.CipherService;
import app.project.jwt.core.config.MessageComponent;
import app.project.jwt.core.security.JwtTokenService;
import app.project.jwt.user.entity.User;
import app.project.jwt.user.enums.UserRoleType;
import app.project.jwt.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@EnableMockMvc
public class BaseMvcTest extends BaseTest{

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MessageComponent messageComponent;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AuthTokenRepository authTokenRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected AuthService authService;

    @Autowired
    protected JwtTokenService jwtTokenService;

    @Autowired
    protected CipherService cipherService;

    protected final String testUserPassword = "1234";

    protected AuthTokenDTO authTokenDTO = null;

    @Value("${app.security.jwt.secretKey}")
    protected String secretKey;

    protected User getTestUser() {
        return userRepository.findByUserId("test")
                .orElseGet(this::createTestUser);
    }

    protected User createTestUser() {
        User user = User.builder()
                .userId("test")
                .password(passwordEncoder.encode(testUserPassword))
                .name("홍길동")
                .regNo(cipherService.encrypt("860824-1655068"))
                .userRoleType(UserRoleType.USER)
                .build();
        return userRepository.save(user);
    }

    protected AuthTokenDTO getToken() {
        if (authTokenDTO == null){
            User testUser = getTestUser();
            AuthLoginDTO authLoginDTO = AuthLoginDTO.builder()
                    .userId(testUser.getUserId())
                    .password(testUserPassword)
                    .build();
            authTokenDTO = authService.login(authLoginDTO);
        }
        return authTokenDTO;
    }

    protected AuthTokenDTO getExpiredRefreshToken() {
        User testUser = getTestUser();
        AuthLoginDTO authLoginDTO = AuthLoginDTO.builder()
                .userId(testUser.getUserId())
                .password(testUserPassword)
                .build();


        String accessToken = jwtTokenService.createAccessToken(authLoginDTO);
        AuthTokenDTO authTokenDTO = createRefreshTokenExcept(authLoginDTO);

        AuthToken authToken = AuthToken.builder()
                .userId(authLoginDTO.getUserId())
                .accessToken(accessToken)
                .refreshToken(authTokenDTO.getRefreshToken())
                .refreshTokenExpirationDate(authTokenDTO.getRefreshTokenExpirationDate())
                .build();

        return AuthTokenDTO.from(authTokenRepository.save(authToken));
    }

    private AuthTokenDTO createRefreshTokenExcept(AuthLoginDTO authLoginDTO) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "token");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("userId", authLoginDTO.getUserId());

        Date expiration = new Date();
        expiration.setTime(expiration.getTime() - 1);
        LocalDateTime refreshTokenExpirationDate = expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        String jwt = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setSubject("user")
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return AuthTokenDTO.builder()
                .refreshToken(jwt)
                .refreshTokenExpirationDate(refreshTokenExpirationDate)
                .build();
    }
}
