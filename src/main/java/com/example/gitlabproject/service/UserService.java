package com.example.gitlabproject.service;

import com.example.gitlabproject.domain.User;
import com.example.gitlabproject.exception.AppException;
import com.example.gitlabproject.exception.ErrorCode;
import com.example.gitlabproject.repository.UserRepository;
import com.example.gitlabproject.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String key;
    private Long expireTimeMs = 1000 * 60 * 60l;

    public String join(String userName, String password) {


        // userName 중복 check
        userRepository.findByUserName(userName).
                ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "는 이미 있습니다.");
                });

        // 저장
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .build();
        userRepository.save(user);

        return "SUCCESS";
    }

    public String login(String userName, String password) {
        // userName 없음
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        // password 틀림
        if (!encoder.matches(password, selectedUser.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘못 입력 했습니다.");
        }

        String token = JwtTokenUtil.createToken(selectedUser.getUserName(),key, expireTimeMs); //로그인, 토큰, 만료기간

        //앞에서 Exception 안났으면 토큰 발행
        return token;
    }

    //public Object login(Object any, Object any1) {
    // }
}
