package com.lagou.rabbitdemo.controller;

import com.lagou.rabbitdemo.dto.ResponseData;
import com.lagou.rabbitdemo.dto.ResponseDataEnums;
import com.lagou.rabbitdemo.dto.req.UserReq;
import com.lagou.rabbitdemo.entity.User;
import com.lagou.rabbitdemo.mapper.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("user")
@AllArgsConstructor
public class UserConTRoller {

    private final UserRepository userRepository;

    /**
     * 用户登入
     */
    @PostMapping("login")
    public ResponseData<?> login (@RequestBody @Validated UserReq userReq, HttpServletRequest httpServletRequest) {

        //  获取数据库中的用户信息
        User user = userRepository.findUserByUsername(userReq.getUsername()).orElseGet(User::new);

        //  账户密码验证
        String passwordMd5 = DigestUtils.md5DigestAsHex(userReq.getPassword().getBytes());
        if ( !passwordMd5.equals(user.getPassword()) ) {
            return ResponseData.FAILURE(ResponseDataEnums.RESPONSE_LOGIN_FAIL);
        }

        //  登入成功将用户信息加入到会话
        httpServletRequest.getSession().setAttribute("user",user);

        return ResponseData.SUCCESS(ResponseDataEnums.RESPONSE_LOGIN_SUCCESS);
    }

    /**
     * 用户注册
     */
    @PostMapping("register")
    public ResponseData<?> register (@RequestBody @Validated UserReq userReq){

        //  用户名唯一性判断
        Optional<User> userByUsername = userRepository.findUserByUsername(userReq.getUsername());
        if ( userByUsername.isPresent() ) {
            return ResponseData.FAILURE(ResponseDataEnums.RESPONSE_REGISTER_EXISTING_USERNAME );
        }

        //  开始注册
        User user = new User();
        user.setUsername(userReq.getUsername());
        String passwordMd5 = DigestUtils.md5DigestAsHex(userReq.getPassword().getBytes());
        user.setPassword(passwordMd5);
        User save = userRepository.save(user);

        return save.getUserId() != null
                ?  ResponseData.SUCCESS(ResponseDataEnums.RESPONSE_REGISTER_SUCCESS)
                :  ResponseData.FAILURE(ResponseDataEnums.RESPONSE_REGISTER_FAIL);

    }
}
