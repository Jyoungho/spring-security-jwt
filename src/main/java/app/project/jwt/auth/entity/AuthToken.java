package app.project.jwt.auth.entity;

import app.project.jwt.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
@Table(name = "auth_token")
public class AuthToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    private String accessToken;

    private String refreshToken;

    private LocalDateTime refreshTokenExpirationDate;

    public void updateToken(String accessToken, String refreshToken, LocalDateTime refreshTokenExpirationDate) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationDate = refreshTokenExpirationDate;
    }
}
