package LinkUpTalk.user.application;

import LinkUpTalk.auth.domain.Group;
import LinkUpTalk.auth.domain.constant.RoleType;
import LinkUpTalk.auth.domain.constant.TokenType;
import LinkUpTalk.user.presentation.dto.UserPasswordModifyReqDto;
import LinkUpTalk.common.util.JwtUtil;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.user.presentation.dto.UserCreateReqDto;
import LinkUpTalk.user.presentation.dto.UserGetResDto;
import LinkUpTalk.user.presentation.dto.UserModifyReqDto;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.auth.domain.Repository.GroupRepository;
import LinkUpTalk.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Transactional(readOnly = true)
    public UserGetResDto getUser(Long userId) {
        User user = findByUserId(userId);
        return user.toDto();
    }

    @Transactional
    public String create(UserCreateReqDto dto) {
        checkIfUserExist(dto.getEmail());
        User user = User.of(dto.getName(), dto.getEmail(), dto.getPassword());
        user.encodePassword(passwordEncoder.encode(dto.getPassword()));

        updateCustomerGroup(user);
        userRepository.save(user);
        return issueAccessToken(user);
    }

    @Transactional
    public void modify(Long userId,UserModifyReqDto dto) {
        User user = findByUserId(userId);
        user.update(dto.getName(),dto.getEmail());
    }

    @Transactional
    public void modifyPassword(Long userId,UserPasswordModifyReqDto dto) {
        User user =findByUserId(userId);
        checkPassword(dto.getPassword(), user.getPassword());
        user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    @Transactional
    public void delete(Long userId) {
        User user = findByUserId(userId);
        user.delete();
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }

    private void checkIfUserExist(String email) {
        if(userRepository.existsByEmail(email)){
            throw new BusinessException(ResponseCode.USER_ALREADY_EXISTS);
        };
    }

    private void checkPassword(String password,String encodedPassword) {
        if (!passwordEncoder.matches(password,encodedPassword)) {
            throw new BusinessException(ResponseCode.INVALID_INPUT_VALUE);
        }
    }
    private void updateCustomerGroup(User userEntity) {
        Group group = groupRepository.findByCode(RoleType.CUSTOMER.getRole())
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
        userEntity.addUserGroups(group);
    }

    private String issueAccessToken(User user) {
        List<String> roles=user.getRoles().stream().map(m -> m.getCode()).toList();
        return jwtUtil.createToken(user.getEmail(),roles, TokenType.accessToken.name());
    }




}
