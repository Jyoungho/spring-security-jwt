package app.project.jwt.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class AuthLoginDTO {

    @NotEmpty
    @ApiModelProperty(value = "아이디", example = "test", required = true)
    private String userId;

    @NotEmpty
    @ApiModelProperty(value = "비밀번호", example = "1234", required = true)
    private String password;

}
