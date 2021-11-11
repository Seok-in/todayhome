package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.oAuthLogin.model.KakaoUserInfo;
import com.example.demo.src.oAuthLogin.model.KakaoUserNameReq;
import com.example.demo.src.store.model.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
        if(userProvider.checkUserName(postUserReq.getUserName())==1){
            throw new BaseException(POST_USERS_EXISTS_NAME);
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

    @Transactional(rollbackFor = {Exception.class})
    public void createSignOut(PostSignOutReq postSignOutReq, int userIdx) throws BaseException{
        if (postSignOutReq.getAgreement() == "N"){
            throw new BaseException(POST_USERS_REQUIRED_AGREE);
        }
        if(postSignOutReq.getEtc() == "N" && postSignOutReq.getLowService() == "N" && postSignOutReq.getLowUse() =="N" && postSignOutReq.getReSignup() =="N" &&
                postSignOutReq.getProtection() == "N" && postSignOutReq.getEtc() == "N"){
            throw new BaseException(POST_USERS_REQUIRED_CHECK);
        }
        try{
            userDao.signoutUser(postSignOutReq, userIdx);
            userDao.logout(userIdx);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void createQuestion(PostProductQuestReq postProductQuestReq, int userIdx, int productIdx) throws BaseException{
        try{
            userDao.createQuestion(postProductQuestReq, userIdx, productIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteQuestion(int questionIdx, int userIdx) throws BaseException{
        if(userIdx != userDao.getUserIdxByQuest(questionIdx)){
            throw new BaseException(INVALID_USER_JWT);
        }
        try{
            userDao.deleteQuestion(questionIdx, userIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteReview(int reviewIdx, int userIdx) throws BaseException{
        if(userDao.getUserIdxByReview(reviewIdx) == userIdx){
            throw new BaseException(INVALID_USER_JWT);
        }
        try{
            userDao.deleteReview(reviewIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void createReviewByOther(int userIdx, PostCreateReviewReq postCreateReviewReq) throws BaseException{
        try{
            userDao.createReviewByOther(userIdx, postCreateReviewReq);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void createReviewByToday(int userIdx, int productIdx, int orderIndex, PostCreateReviewOhouseReq postCreateReviewOhouseReq) throws BaseException{
        try{
            userDao.createReviewByCart(userIdx, productIdx, orderIndex, postCreateReviewOhouseReq);
        }
        catch(Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void modifyReviewByOther(int userIdx, int reviewIdx, PatchReviewReq patchReviewReq) throws BaseException{
        try{
            if(userIdx == userDao.getUserIdxByReview(reviewIdx)){
                //userDao.modifyReviewImages(reviewIdx, patchReviewReq.getReviewImages());
                userDao.modifyReviewData(reviewIdx, patchReviewReq);
            }
            else{
                throw new BaseException(MODIFY_ONLY_MY_REVIEW);
            }
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void modifyReviewByToday(int reviewIdx, PatchHouseReviewReq patchHouseReviewReq) throws BaseException{
        try{
            if(reviewIdx == userDao.getUserIdxByReview(reviewIdx)){
                //userDao.modifyReviewImages(reviewIdx, patchHouseReviewReq.getReviewImages());
                userDao.modifyOHouseReviewData(reviewIdx, patchHouseReviewReq);
            }
            else{
                throw new BaseException(MODIFY_ONLY_MY_REVIEW);
            }
        }
        catch(Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     Follow API
     */
    @Transactional(rollbackFor = {Exception.class})
    public void userFollow(int userId, int followerId) throws BaseException{
        try {
            int result = userDao.userFollow(userId, followerId);
            if (result == 0)
                throw new BaseException(DATABASE_ERROR);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     Unfollow API
     */
    @Transactional(rollbackFor = {Exception.class})
    public void userUnfollow(int userId, int followerId) throws BaseException{
        try {
            int result = userDao.userUnfollow(userId, followerId);
            if (result == 0)
                throw new BaseException(DATABASE_ERROR);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
