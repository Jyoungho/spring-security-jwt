package app.project.jwt.user.dto;

import app.project.jwt.core.config.CipherService;
import app.project.jwt.user.entity.User;
import app.project.jwt.user.enums.UserRoleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class UserDTO {

    @ApiModelProperty(value = "id", example = "1")
    private Long id;

    @ApiModelProperty(value = "계정", example = "test")
    private String userId;

    @ApiModelProperty(value = "비밀번호", example = "null")
    private String password;

    @ApiModelProperty(value = "사용자 이름", example = "홍길동")
    private String name;

    @ApiModelProperty(value = "사용자 주민번호", example = "null")
    private String regNo;

    @ApiModelProperty(value = "계정권한", example = "USER")
    private UserRoleType userRoleType;

    @ApiModelProperty(value = "생성날짜", example = "2022-04-07T00:55:00.181448")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "수정날짜", example = "2022-04-07T00:55:00.181448")
    private LocalDateTime updatedDate;

    public static UserDTO fromWithOutPassword(User user, CipherService cipherService) {
        return UserDTO.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .userRoleType(user.getUserRoleType())
                .regNo(cipherService.decrypt(user.getRegNo()))
                .createdDate(user.getCreatedDate())
                .updatedDate(user.getUpdatedDate())
                .build();
    }

    public static User toEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .userId(userDTO.getUserId())
                .password(userDTO.getPassword())
                .name(userDTO.getName())
                .regNo(userDTO.getRegNo())
                .userRoleType(userDTO.getUserRoleType())
                .build();
    }
}
