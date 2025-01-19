package LinkUpTalk.util;

import LinkUpTalk.auth.domain.Group;
import LinkUpTalk.auth.domain.constant.RoleType;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.auth.domain.Repository.GroupRepository;
import LinkUpTalk.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    final String USERNAME = "spring11";
    final String PASSWORD = "spring123";
    final String EMAIL = "spring11@naver.com";

    public User registerUser() {
        Group role = groupRepository.findByCode(RoleType.CUSTOMER.getRole()).get();
        User user = User.of(USERNAME, PASSWORD, EMAIL);
        user.addUserGroups(role);
        user.encodePassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(user);
        return user;
    }

}
