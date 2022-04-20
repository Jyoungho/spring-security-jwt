package app.project.jwt.user.repository;

import app.project.jwt.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);
    
    Optional<User> findByUserIdAndPassword(String userId, String password);
}
