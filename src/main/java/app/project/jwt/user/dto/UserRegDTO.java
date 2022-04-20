package app.project.jwt.user.dto;

import app.project.jwt.common.validation.annotation.RegNoValid;
import app.project.jwt.user.entity.User;
import app.project.jwt.user.enums.UserRoleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class UserRegDTO {

    @ApiModelProperty(value = "계정", example = "test", required = true)
    @Size(min = 3, max = 30, message = "사용자 아이디는 {min} ~ {max} 사이여야 합니다.")
    private String userId;

    @ApiModelProperty(value = "비밀번호", example = "1234" , required = true)
    @Size(min = 3, max = 30, message = "비밀번호는 {min} ~ {max} 사이여야 합니다.")
    private String password;

    @ApiModelProperty(value = "사용자 이름", example = "홍길동", required = true)
    private String name;

    @ApiModelProperty(value = "사용자 주민번호", example = "920910-1234567", required = true)
    @RegNoValid
    private String regNo;

    @ApiModelProperty(value = "사용자 권한", hidden = true)
    private UserRoleType userRoleType = UserRoleType.USER;

    public static User toEntity(UserRegDTO userRegDTO) {
        return User.builder()
                .userId(userRegDTO.getUserId())
                .password(userRegDTO.getPassword())
                .name(userRegDTO.getName())
                .regNo(userRegDTO.getRegNo())
                .userRoleType(userRegDTO.getUserRoleType())
                .build();
    }
}
