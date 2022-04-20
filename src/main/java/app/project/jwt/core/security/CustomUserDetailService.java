package app.project.jwt.core.security;

import app.project.jwt.core.handler.exception.BizException;
import app.project.jwt.user.entity.User;
import app.project.jwt.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> BizException
                        .withUserMessageKey("exception.user.not.found")
                        .build());

        UserDetailsImpl userDetailsImpl = new UserDetailsImpl();
        userDetailsImpl.setUsername(user.getUserId());
        userDetailsImpl.setPassword(user.getPassword());
        userDetailsImpl.setAuthorities(user.getUserRoleType());
        return userDetailsImpl;
    }
}
