package com.example.demo.src.oAuthLogin;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.oAuthLogin.*;
import com.example.demo.src.oAuthLogin.model.KakaoLoginReq;
import com.example.demo.src.oAuthLogin.model.KakaoUserNameReq;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ohouse/kakao")
public class oAuthLoginController {


        final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        private final oAuthLoginProvider oAuthLoginProvider;
        @Autowired
        private final oAuthLoginService oAuthLoginService;
        @Autowired
        private final JwtService jwtService;


        public oAuthLoginController(oAuthLoginService oAuthLoginService, oAuthLoginProvider oAuthLoginProvider, JwtService jwtService) {
            this.oAuthLoginProvider = oAuthLoginProvider;
            this.oAuthLoginService = oAuthLoginService;
            this.jwtService = jwtService;
        }


    // 6. KAKAO 로그인
    @ResponseBody
    @PostMapping("/kakao")
    public BaseResponse<PostLoginRes> kakaoLogin(@RequestBody KakaoLoginReq kakaoLoginReq){
        try{
            String accessToken = kakaoLoginReq.getAccessToken();
            PostLoginRes postLoginRes = oAuthLoginProvider.kakaoLogin(accessToken);
            return new BaseResponse<>(postLoginRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 6-1. KAKAO 회원가입
    @ResponseBody
    @PostMapping("/kakao/signup")
    public BaseResponse<PostLoginRes> kakaoSignup(@RequestBody KakaoUserNameReq kakaoUserNameReq){
        try{
            PostLoginRes postLoginRes = oAuthLoginService.createKakaoUser(kakaoUserNameReq);
            return new BaseResponse<>(postLoginRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
