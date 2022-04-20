package app.project.jwt.auth.controller;

import app.project.jwt.auth.dto.AuthGetNewAccessTokenDTO;
import app.project.jwt.auth.dto.AuthLoginDTO;
import app.project.jwt.auth.dto.AuthTokenDTO;
import app.project.jwt.auth.service.AuthService;
import app.project.jwt.core.web.Path;
import app.project.jwt.core.web.response.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "Auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "로그인")
    @ApiResponses(value = {
            @ApiResponse(code = 200, responseContainer = "Map", response = RestResponse.class, message = "로그인 성공"),
            @ApiResponse(code = 400, responseContainer = "Map", response = RestResponse.class, message = "로그인 실패")
    })
    @PostMapping(Path.LOGIN)
    public RestResponse<AuthTokenDTO> login(@Validated @RequestBody AuthLoginDTO authLoginDTO) {
        AuthTokenDTO authTokenDTO = authService.login(authLoginDTO);

        return RestResponse
                .withData(authTokenDTO)
                .withUserMessageKey("success.auth.login")
                .build();
    }

    @ApiOperation(value = "access token 재발급")
    @ApiResponses(value = {
        @ApiResponse(code = 200, responseContainer = "Map", response = RestResponse.class, message = "발급성공"),
        @ApiResponse(code = 400, responseContainer = "Map", response = RestResponse.class, message = "발급실패")
    })
    @PostMapping(Path.NEW_TOKEN)
    public RestResponse<AuthTokenDTO> getNewAccessToken(
            HttpServletRequest request,
            @Validated @RequestBody AuthGetNewAccessTokenDTO authGetNewAccessTokenDTO) {
        AuthTokenDTO authTokenDTO = authService.newAccessToken(authGetNewAccessTokenDTO, request);

        return RestResponse
                .withData(authTokenDTO)
                .withUserMessageKey("success.auth.access.token.create.again")
                .build();
    }
}
