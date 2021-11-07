package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final UserService userService;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService, UserService userService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        User user = userDao.getPwd(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getUserPw());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(postLoginReq.getUserPw().equals(password)){
            int userIdx = userDao.getPwd(postLoginReq).getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            try{
                userDao.login(userIdx);
            }
            catch (Exception exception){
                throw new BaseException(DATABASE_ERROR);
            }
            return new PostLoginRes(jwt);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    public void logout(int userIdx) throws BaseException{
        try{
            userDao.logout(userIdx);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes kakaoLogin(String accessToken) throws BaseException{
        KakaoUserInfo kakaoUserInfo = userService.getKakaoUserInfo(accessToken);
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
