package com.example.demo.src.mypage;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.mypage.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class MypageService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MypageDao mypageDao;
    private final MypageProvider mypageProvider;
    private final JwtService jwtService;


    @Autowired
    public MypageService(MypageDao mypageDao, MypageProvider mypageProvider, JwtService jwtService) {
        this.mypageDao = mypageDao;
        this.mypageProvider = mypageProvider;
        this.jwtService = jwtService;

    }

    /**
    쿠폰 받기
     */
    public void postPcouponsReq(int myIdx, PostCouponReq postPcouponsReq) throws BaseException {
        try{
            int result = mypageDao.postPcouponsReq(myIdx, postPcouponsReq);
            if(result == 0){
                throw new BaseException(RECEIVE_FAIL_COUPON);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     쿠폰 받기 2 (쿠폰 코드)
     */
    public void postCodeReq(int myIdx, /*PostCodeReq postCodeReq*/String code) throws BaseException {
        try{
            int result = mypageDao.postCodeReq(myIdx, /*postCodeReq*/code);
            if(result == 0){
                throw new BaseException(RECEIVE_FAIL_COUPON);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}