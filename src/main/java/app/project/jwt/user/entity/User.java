package app.project.jwt.user.entity;

import app.project.jwt.user.enums.UserRoleType;
import app.project.jwt.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String regNo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoleType userRoleType;
}
