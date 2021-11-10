package com.example.demo.src.oAuthLogin;

import com.example.demo.config.BaseException;
import com.example.demo.src.oAuthLogin.model.KakaoUserInfo;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.NEED_TO_SIGNUP;

@Service
public class oAuthLoginProvider {
    private final oAuthLoginDao oAuthLoginDao;
    private final oAuthLoginService oAuthLoginService;
    private final JwtService jwtService;
    private final UserDao userDao;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public oAuthLoginProvider(oAuthLoginService oAuthLoginService, oAuthLoginDao oAuthLoginDao, JwtService jwtService, UserDao userDao) {
        this.oAuthLoginDao = oAuthLoginDao;
        this.jwtService = jwtService;
        this.oAuthLoginService = oAuthLoginService;
        this.userDao=userDao;
    }


    public PostLoginRes kakaoLogin(String accessToken) throws BaseException {
        KakaoUserInfo kakaoUserInfo = oAuthLoginService.getKakaoUserInfo(accessToken);
        String email = kakaoUserInfo.getKakao_account().getEmail();
        if (userDao.checkEmail(email)==1){
            int userIdx = userDao.getUserIdx(email);
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(jwt);
        }
        else{
            throw new BaseException(NEED_TO_SIGNUP);
        }
    }
}
