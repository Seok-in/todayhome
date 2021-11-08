package com.example.demo.src.mypage;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.mypage.model.*;
import com.example.demo.src.mypage.model.scrapbook.*;
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
public class MypageProvider {

    private final MypageDao mypageDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MypageProvider(MypageDao mypageDao, JwtService jwtService) {
        this.mypageDao = mypageDao;
        this.jwtService = jwtService;
    }
    /**
     * 팔로워 조회
     */

    public List<GetFollowers> getFollowers(int logonIdx,int userIdx) throws BaseException{
        try{
            List<GetFollowers> getFollowers = mypageDao.getFollowers(logonIdx,userIdx);
            return getFollowers;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 팔로잉 조회
     */
    public List<GetFollowers> getFollowing(int logonIdx,int userIdx) throws BaseException{
        try{
            List<GetFollowers> getFollowing = mypageDao.getFollowing(logonIdx,userIdx);
            return getFollowing;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 쿠폰 조회
     */
    public List<GetCoupons> getCoupons(int myIdx) throws BaseException{
        try{
            List<GetCoupons> getCoupons = mypageDao.getCoupons(myIdx);
            return getCoupons;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 발급한 쿠폰인지 확인
     */
    public int checkReceived(int myIdx, PostPcouponsReq postPcouponsReq) throws BaseException{
        try{
            int received = mypageDao.checkReceived(myIdx,postPcouponsReq);
            return received;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 사용된 쿠폰 코드인지 확인
     */
    public String checkUsed(/*PostCodeReq postCodeReq*/String code) throws BaseException{
        try{
            String used = mypageDao.checkUsed(/*postCodeReq*/code);
            return used;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 포인트 조회
     */
    public List<Point> getPoints(int myIdx) throws BaseException{
        try{
            List<Point> pointList = mypageDao.getPoints(myIdx);
            return pointList;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 사용 가능한 전체 포인트 조회
     */
    public int getUsablePoints(int myIdx) throws BaseException{
        try{
            int usablePoints = mypageDao.getUsablePoints(myIdx);
            return usablePoints;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 스크랩북 상단 userName 스크랩북 조회
     */
    public String getUserName(int userIdx) throws BaseException{
        try{
            String getUserName = mypageDao.getUserName(userIdx);
            return getUserName;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 전체 스크랩북 조회
     */
    public List<GetAllScraps> getAllScraps(int userIdx) throws BaseException{
        try{
            List<GetAllScraps> getAllScraps = mypageDao.getAllScraps(userIdx);
            return getAllScraps;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 노하우 & 집들이 스크랩북 조회
     */
    public List<GetContentScraps> getContentScraps(int userIdx, String filter) throws BaseException{
        try{
            List<GetContentScraps> getContentScraps = mypageDao.getContentScraps(userIdx,filter);
            return getContentScraps;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 사진 스크랩북 조회
     */
    public List<GetPicScraps> getPicScraps(int userIdx) throws BaseException{
        try{
            List<GetPicScraps> getPicScraps = mypageDao.getPicScraps(userIdx);
            return getPicScraps;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상품 스크랩북 조회
     */
    public List<GetProdScraps> getProdScraps(int userIdx) throws BaseException{
        try{
            List<GetProdScraps> getProdScraps = mypageDao.getProdScraps(userIdx);
            return getProdScraps;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}