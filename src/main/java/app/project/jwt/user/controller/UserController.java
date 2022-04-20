package app.project.jwt.user.controller;

import app.project.jwt.core.aop.User;
import app.project.jwt.core.web.Path;
import app.project.jwt.core.web.response.RestResponse;
import app.project.jwt.user.dto.UserDTO;
import app.project.jwt.user.dto.UserRegDTO;
import app.project.jwt.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "사용자")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(code = 200, responseContainer = "Map", response = RestResponse.class, message = "회원가입 성공"),
            @ApiResponse(code = 400, responseContainer = "Map", response = RestResponse.class, message = "회원가입 실패")
    })
    @PostMapping(Path.SIGNUP)
    public RestResponse<UserDTO> signup(@Validated @RequestBody UserRegDTO userRegDTO) {
        UserDTO userDTO = userService.signup(userRegDTO);

        return RestResponse
                .withData(userDTO)
                .withUserMessageKey("success.user.create")
                .build();
    }

    @ApiOperation(value = "내 정보 보기")
    @ApiResponses(value = {
            @ApiResponse(code = 200, responseContainer = "Map", response = RestResponse.class, message = "내 정보 보기 성공"),
            @ApiResponse(code = 400, responseContainer = "Map", response = RestResponse.class, message = "내 정보 보기 실패")
    })
    @GetMapping(Path.ME)
    public RestResponse<UserDTO> getUserInfo(@ApiIgnore @User UserDTO userDTO) {
        return RestResponse
                .withData(userDTO)
                .withUserMessageKey("success.user.get")
                .build();
    }
}
