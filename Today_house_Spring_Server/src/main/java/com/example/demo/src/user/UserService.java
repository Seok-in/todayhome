package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(userProvider.checkEmail(postUserReq.getUserEmail()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        String pwd;
        try{
            //암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getUserPw());
            postUserReq.setUserPw(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createSignOut(PostSignOutReq postSignOutReq, int userIdx) throws BaseException{
        if (postSignOutReq.getAgreement() == "N"){
            throw new BaseException(POST_USERS_REQUIRED_AGREE);
        }
        try{
            userDao.signoutUser(postSignOutReq, userIdx);
            userDao.logout(userIdx);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public KakaoUserInfo getKakaoUserInfo(String accessToken){
        RestTemplate rt2 = new RestTemplate();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization","Bearer " + accessToken);
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfo =
                new HttpEntity<>(headers2);
        ResponseEntity<KakaoUserInfo> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfo,
                KakaoUserInfo.class
        );
        return response2.getBody();
    }

    public String makeTempPw(){
        char[] ch = new char[11];
        for(int i =0; i <=10; i++){
            ch[i] = (char) ((Math.random()*26) + 97);
        }
        String password = ch.toString();
        return password;
    }

    public PostLoginRes createKakaoUser(KakaoUserNameReq kakaoUserNameReq) throws BaseException{
        PostUserReq postUserReq = new PostUserReq();
        String accessToken = kakaoUserNameReq.getAccessToken();
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(accessToken);
        String email = kakaoUserInfo.getKakao_account().getEmail();
        String userName = kakaoUserNameReq.getUserName();
        postUserReq.setUserName(userName);
        postUserReq.setUserEmail(email);

        try{
                        String pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(makeTempPw());
            postUserReq.setUserPw(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(jwt);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
