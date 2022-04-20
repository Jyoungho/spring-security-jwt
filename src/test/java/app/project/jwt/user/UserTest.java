package app.project.jwt.user;

import app.project.jwt.auth.dto.AuthTokenDTO;
import app.project.jwt.config.BaseMvcTest;
import app.project.jwt.core.handler.exception.ErrorCode;
import app.project.jwt.core.web.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("사용자")
public class UserTest extends BaseMvcTest {

    @Test
    @Transactional
    @Order(1)
    @DisplayName("회원가입")
    public void createUser() throws Exception {
        // given
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", "signUpTest");
        requestData.put("password", testUserPassword);
        requestData.put("name", "회원가입 테스트 계정");
        requestData.put("regNo", "920910-1234567");

        String requestBody = objectMapper.writeValueAsString(requestData);

        // when
        mockMvc.perform(post(Path.SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.user.create")));
    }

    @Test
    @Transactional
    @Order(2)
    @DisplayName("회원가입(예외) - Validate 문자길이")
    public void createUserValidateCheckSize() throws Exception {
        // given case
        // userId, password length size 3 ~ 30
        Map<String, Object> testCase = new HashMap<>();
        testCase.put("userId", "12");
        testCase.put("password", "1234567890123456789012345678901");
        testCase.put("name", "회원가입 테스트 계정");
        testCase.put("regNo", "920910-1234567");

        String requestBody1 = objectMapper.writeValueAsString(testCase);

        // when
        mockMvc.perform(post(Path.SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody1)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("exception.validate.need")));
    }

    @Test
    @Transactional
    @Order(3)
    @DisplayName("내정보보기")
    public void getUserInfo() throws Exception {
        // given
        StringBuilder sb = new StringBuilder();
        AuthTokenDTO token = getToken();

        String bearAccessToken = sb.append("Bearer").append(" ").append(token.getAccessToken()).toString();

        // when
        mockMvc.perform(get(Path.ME)
                .header(HttpHeaders.AUTHORIZATION, bearAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("meta.userMessage")
                        .value(messageComponent.getMessage("success.user.get")));
    }

    @Test
    @Transactional
    @Order(4)
    @DisplayName("내정보보기(예외) - 잘못된 토큰")
    public void getUserInfoError() throws Exception {
        // given
        String bearAccessTokenError = "Bearer sss111";

        // when
        mockMvc.perform(get(Path.ME)
                .header(HttpHeaders.AUTHORIZATION, bearAccessTokenError)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("meta.userMessage")
                        .value(ErrorCode.UNAUTHORIZED_EXCEPTION.getDescription()));
    }
}
