package app.project.jwt.user.service;

import app.project.jwt.core.config.CipherService;
import app.project.jwt.core.handler.exception.BizException;
import app.project.jwt.core.security.JwtTokenService;
import app.project.jwt.user.dto.UserDTO;
import app.project.jwt.user.dto.UserRegDTO;
import app.project.jwt.user.entity.User;
import app.project.jwt.user.enums.UserRoleType;
import app.project.jwt.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final CipherService cipherService;


    /**
     * Request Authorization Bearer JWT 토큰으로 User 정보조회
     * 예외조건 : 토큰이 없을 경우
     * 예외조건 : DB 에 해당 user 정보가 없을 경우
     */
    @Transactional
    public UserDTO getUserByToken(HttpServletRequest request) {
        String token = jwtTokenService.getToken(request);
        if (token == null) {
            throw BizException
                    .withUserMessageKey("exception.token.need")
                    .build();
        }

        String userId = jwtTokenService.getUserInfo(token);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> BizException
                        .withUserMessageKey("exception.user.not.found")
                        .build());

        return UserDTO.fromWithOutPassword(user, cipherService);
    }

    /**
     * 회원가입
     * 예외조건 : 기존 userId 가 이미 있을 경우
     */
    @Transactional
    public UserDTO signup(UserRegDTO userRegDTO) {
        checkUserRoleType(userRegDTO);
        UserRegDTO encryptUserRegDTO = encryptInfo(userRegDTO);

        userRepository.findByUserId(userRegDTO.getUserId())
                .ifPresent(user -> {
                    throw BizException
                            .withUserMessageKey("exception.user.already.exist")
                            .build();
                });

        User save = userRepository.save(UserRegDTO.toEntity(encryptUserRegDTO));
        return UserDTO.fromWithOutPassword(save, cipherService);
    }

    /** 어드민 권한 생성불가(관리자에게 문의) */
    private void checkUserRoleType(UserRegDTO userRegDTO) {
        if (userRegDTO.getUserRoleType() == null) {
            throw BizException
                    .withUserMessageKey("exception.user.role.type.null")
                    .build();
        }

        if (userRegDTO.getUserRoleType() == UserRoleType.ADMIN) {
            throw BizException
                    .withUserMessageKey("exception.user.create.admin")
                    .build();
        }
    }

    private UserRegDTO encryptInfo(UserRegDTO userRegDTO) {
        String encryptPassword = passwordEncoder.encode(userRegDTO.getPassword());
        String encryptRegNo = cipherService.encrypt(userRegDTO.getRegNo());

        userRegDTO.setPassword(encryptPassword);
        userRegDTO.setRegNo(encryptRegNo);

        return userRegDTO;
    }
}
