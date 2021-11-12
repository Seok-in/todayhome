package com.example.demo.src.oAuthLogin;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.oAuthLogin.model.*;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.example.demo.src.user.*;

import java.security.SecureRandom;
import java.util.Random;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class oAuthLoginService {
    private final oAuthLoginDao oAuthLoginDao;
    private final JwtService jwtService;
    private final UserDao userDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public oAuthLoginService(oAuthLoginDao oAuthLoginDao, JwtService jwtService, UserDao userDao) {
        this.oAuthLoginDao = oAuthLoginDao;
        this.jwtService = jwtService;
        this.userDao = userDao;

    }

    // 카카오 액세스토큰 받기위해 구현
    @Transactional(rollbackFor = {Exception.class})
    public String getKakaoToken(String code){
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "7f5bb71f045567209d1beb4e84d91b0c");
        params.add("redirect_uri", "https://prod.seokin-test.shop/ohouse/kakao/auth/kakao/callback");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = new OAuthToken();
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException exception) {
            exception.printStackTrace();
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }
        return oauthToken.getAccess_token();
    }

    @Transactional(rollbackFor = {Exception.class})
    public KakaoPayReq kakaoPayReq(int orderIndex, int userIdx) throws BaseException {
        try {
            if (oAuthLoginDao.checkUserIdxByOrder(orderIndex) != userIdx) {
                throw new BaseException(INVALID_USER_JWT);
            }
            KakaoPayReq kakaoPayReq = oAuthLoginDao.getPayment(orderIndex);
            return kakaoPayReq;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = {Exception.class})
    public KakaoPayRes payReady(int orderIndex, int userIdx) throws BaseException{
        try
        {
            if(oAuthLoginDao.checkUserIdxByOrder(orderIndex)!= userIdx){
                throw new BaseException(INVALID_USER_JWT);
            }
            KakaoPayReq kakaoPayReq = oAuthLoginDao.getPayment(orderIndex);

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","KakaoAK 3c4dd8f4914bbcd64a8a47865ccf307b");
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", kakaoPayReq.getCid());
        params.add("partner_order_id", kakaoPayReq.getPartner_order_id());
        params.add("partner_user_id", kakaoPayReq.getPartner_user_id());
        params.add("item_name",kakaoPayReq.getItem_name());
        params.add("quantity",Integer.toString(kakaoPayReq.getQuantity()));
        params.add("total_amount",Integer.toString(kakaoPayReq.getTotal_amount()));
        params.add("tax_free_amount",Integer.toString(kakaoPayReq.getTax_free_amount()));
        params.add("approval_url",kakaoPayReq.getApproval_url());
        params.add("cancel_url", kakaoPayReq.getCancel_url());
        params.add("fail_url",kakaoPayReq.getFail_url());

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/payment/ready",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );


            KakaoPayRes kakaoPayRes = new KakaoPayRes();
            ObjectMapper objectMapper = new ObjectMapper();
        try{
            kakaoPayRes = objectMapper.readValue(response.getBody(), KakaoPayRes.class);
        } catch (JsonMappingException exception) {
            exception.printStackTrace();
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }
        return kakaoPayRes;
        }
        catch(BaseException e){
            throw new BaseException(e.getStatus());
        }
        catch(Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public KakaoPayApproveRes kakaoApprove(KakaoPayApproveReq kakaoPayApproveReq, int orderIndex, int userIdx, String pg_token) throws BaseException{
        try {
            if (oAuthLoginDao.checkUserIdxByOrder(orderIndex) != userIdx) {
                throw new BaseException(INVALID_USER_JWT);
            }
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK 3c4dd8f4914bbcd64a8a47865ccf307b");
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("cid", "TC0ONETIME");
            params.add("tid", kakaoPayApproveReq.getTid());
            params.add("partner_order_id", Integer.toString(orderIndex));
            params.add("partner_user_id", Integer.toString(userIdx));
            params.add("pg_token", pg_token);

            HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                    new HttpEntity<>(params, headers);
            ResponseEntity<String> response = rt.exchange(
                    "https://kapi.kakao.com/v1/payment/approve",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );

            KakaoPayApproveRes kakaoPayApproveRes = new KakaoPayApproveRes();
            ObjectMapper objectMapper = new ObjectMapper();
            try{
                kakaoPayApproveRes = objectMapper.readValue(response.getBody(), KakaoPayApproveRes.class);
            } catch (JsonMappingException exception) {
                exception.printStackTrace();
            } catch (JsonProcessingException exception) {
                exception.printStackTrace();
            }
            oAuthLoginDao.completePayment(orderIndex);
            return kakaoPayApproveRes;
        }
        catch(BaseException e){
            throw new BaseException(e.getStatus());
        }
        catch(Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public KakaoUserInfo getKakaoUserInfo(String accessToken){
        RestTemplate rt2 = new RestTemplate();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization","Bearer " + accessToken);
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
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

    @Transactional(rollbackFor = {Exception.class})
    public String makeTempPw(){

        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++)
        {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }

    public PostLoginRes createKakaoUser(KakaoUserNameReq kakaoUserNameReq) throws BaseException {
        PostUserReq postUserReq = new PostUserReq();
        String accessToken = kakaoUserNameReq.getAccessToken();
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(accessToken);
        String email = kakaoUserInfo.getKakao_account().getEmail();
        String userName = kakaoUserNameReq.getUserName();
        postUserReq.setUserName(userName);
        postUserReq.setUserEmail(email);
        if(userDao.checkUserName(postUserReq.getUserName())==1){
            throw new BaseException(POST_USERS_EXISTS_NAME);
        }
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
