package app.project.jwt.auth;

import app.project.jwt.auth.dto.AuthTokenDTO;
import app.project.jwt.config.BaseMvcTest;
import app.project.jwt.core.web.Path;
import app.project.jwt.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("인증")
public class AuthTest extends BaseMvcTest {

    @Test
    @Transactional
    @Order(1)
    @DisplayName("로그인")
    public void login() throws Exception {
        // given
        User testUser = getTestUser();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", testUser.getUserId());
        requestData.put("password", testUserPassword);

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.auth.login")));
    }

    @Test
    @Transactional
    @Order(2)
    @DisplayName("토큰재발급")
    public void getNewAccessToken() throws Exception {
        // given
        AuthTokenDTO authTokenDTO = getToken();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("id", authTokenDTO.getRefreshId());
        requestData.put("refreshToken", authTokenDTO.getRefreshToken());

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.NEW_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.auth.access.token.create.again")));
    }

    @Test
    @Transactional
    @Order(3)
    @DisplayName("토큰재발급(예외) - DB 데이터와 refreshToken 다름")
    public void getNewAccessTokenExceptData() throws Exception {
        // given
        AuthTokenDTO authTokenDTO = getToken();

        Map<String, Object> requestExceptData = new HashMap<>();
        requestExceptData.put("id", authTokenDTO.getRefreshId());
        requestExceptData.put("refreshToken", "ss");

        String requestBody = objectMapper.writeValueAsString(requestExceptData);

        // when
        mockMvc.perform(post(Path.NEW_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.refresh.token.not.equal")));
    }

    @Test
    @Transactional
    @Order(4)
    @DisplayName("토큰재발급(예외) - refreshToken 만료")
    public void getNewAccessTokenExceptJwt() throws Exception {
        // given
        AuthTokenDTO authTokenDTO = getExpiredRefreshToken();

        Map<String, Object> requestExceptData = new HashMap<>();
        requestExceptData.put("id", authTokenDTO.getRefreshId());
        requestExceptData.put("refreshToken", authTokenDTO.getRefreshToken());

        String requestBody = objectMapper.writeValueAsString(requestExceptData);

        // when
        mockMvc.perform(post(Path.NEW_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.refresh.token.not.validate")));
    }

    @Test
    @Transactional
    @Order(5)
    @DisplayName("토큰재발급(예외) - DB 조회불가")
    public void getNewAccessTokenExceptNotFound() throws Exception {
        // given
        AuthTokenDTO authTokenDTO = getToken();

        Map<String, Object> requestExceptData = new HashMap<>();
        requestExceptData.put("id", Long.MAX_VALUE);
        requestExceptData.put("refreshToken", authTokenDTO.getRefreshToken());

        String requestBody = objectMapper.writeValueAsString(requestExceptData);

        // when
        mockMvc.perform(post(Path.NEW_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.refresh.token.not.found")));
    }
}
