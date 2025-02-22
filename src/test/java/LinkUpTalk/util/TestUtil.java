package LinkUpTalk.util;

import LinkUpTalk.auth.domain.Repository.RoleRepository;
import LinkUpTalk.auth.domain.Roles;
import LinkUpTalk.auth.domain.constant.RoleType;
import LinkUpTalk.auth.domain.constant.TokenType;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.constant.ChatRoomType;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.common.util.JwtUtil;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestUtil {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository groupRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public User registerUser(String username,String email,String password){
        Roles role = groupRepository.findByCode(RoleType.CUSTOMER.getRole()).get();
        User user = User.of(username, password, email);
        user.addRole(role);
        user.encodePassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public String getToken(String email){
        return jwtUtil.createToken(email,List.of(), TokenType.accessToken.name());
    }

    public ChatRoom registerChatRoom(String name, int capacity, ChatRoomType chatRoomType) {
        ChatRoom chatRoom=ChatRoom.builder()
                .name(name)
                .capacity(capacity)
                .chatRoomType(chatRoomType)
                .build();
        return chatRoomRepository.save(chatRoom);
    }
}
