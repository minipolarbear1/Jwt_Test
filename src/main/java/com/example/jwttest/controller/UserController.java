package com.example.jwttest.controller;

import com.example.jwttest.config.JwtTokenProvider;
import com.example.jwttest.domain.dto.ReqUser;
import com.example.jwttest.domain.entity.User;
import com.example.jwttest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/join")
    public int join(@RequestBody Map<String, String> user/*ReqUser reqUser*/){

//        User user = new User();
//        user.setEmail(reqUser.getEmail());
//        user.setPassword(passwordEncoder.encode(reqUser.getPassword()));
//        user.setRoles(Collections.singletonList("ROLE_USER"));
//
//        userRepository.save(user);

        return userRepository.save(User.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .roles(Collections.singletonList("ROLE_USER"))
                .build()).getId();
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) {
        User member = userRepository.findByEmail(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
    }

}
