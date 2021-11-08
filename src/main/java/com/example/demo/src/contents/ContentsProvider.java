package com.example.demo.src.contents;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.contents.model.*;
import com.example.demo.src.contents.model.house.*;
import com.example.demo.src.contents.model.knowhow.*;
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
public class ContentsProvider {

    private final ContentsDao contentsDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ContentsProvider(ContentsDao contentsDao, JwtService jwtService) {
        this.contentsDao = contentsDao;
        this.jwtService = jwtService;
    }

    /**
     * Intro 조회
     */
    public List<GetHouseIntro> getHouseIntro(int userIdx, int contentIdx) throws BaseException{
        try{
            List<GetHouseIntro> getHouseIntro = contentsDao.getHouseIntro(userIdx,contentIdx);
            return getHouseIntro;
        }
        catch (Exception exception) {
            throw new BaseException(INVALID_INTRO);
        }
    }

    /**
     * knowhow Intro 조회
     */
    public List<GetKnowhowIntro> getKnowhowIntro(int userIdx, int contentIdx) throws BaseException{
        try{
            List<GetKnowhowIntro> getKnowhowIntro = contentsDao.getKnowhowIntro(userIdx,contentIdx);
            return getKnowhowIntro;
        }
        catch (Exception exception) {
            throw new BaseException(INVALID_INTRO);
        }
    }

    /**
     * 중간 컨텐츠 조회
     */
    public List<GetHouseContents> getHouseContents(int userIdx, int contentIdx) throws BaseException{
        try{
            List<GetHouseContents> getHouseContents = contentsDao.getHouseContents(userIdx, contentIdx);
            return getHouseContents;
        }
        catch (Exception exception) {
            throw new BaseException(INVALID_CONTENTS);
        }
    }

    /**
     * knowhow 중간 컨텐츠 조회
     */
    public List<GetKnowhowContents> getKnowhowContents(int userIdx, int contentIdx) throws BaseException{
        try{
            List<GetKnowhowContents> getKnowhowContents = contentsDao.getKnowhowContents(userIdx, contentIdx);
            return getKnowhowContents;
        }
        catch (Exception exception) {
            throw new BaseException(INVALID_CONTENTS);
        }
    }

    /**
     * SocialInfo 컨텐츠 조회
     */
    public List<GetSocialInfo> getSocialInfo(int userIdx, int contentIdx) throws BaseException{
        try{
            List<GetSocialInfo> getSocialInfo = contentsDao.getSocialInfo(userIdx, contentIdx);
            return getSocialInfo;
        }
        catch (Exception exception) {
            throw new BaseException(INVALID_SOCIAL_INFO);
        }
    }

    /**
     * 노하우 SocialInfo 컨텐츠 조회
     */
    public List<GetSocialInfo> getKnowhowSocialInfo(int userIdx, int contentIdx) throws BaseException{
        try{
            List<GetSocialInfo> getKnowhowSocialInfo = contentsDao.getKnowhowSocialInfo(userIdx, contentIdx);
            return getKnowhowSocialInfo;
        }
        catch (Exception exception) {
            throw new BaseException(INVALID_SOCIAL_INFO);
        }
    }

    /**
     * Comments 조회
     */
    public List<GetComments> getComments(int userIdx, int contentIdx) throws BaseException{
        try{
            List<GetComments> getComments = contentsDao.getComments(userIdx, contentIdx);
            return getComments;
        }
        catch (Exception exception) {
            throw new BaseException(INVALID_COMMENTS);
        }
    }

    /**
     * 노하우 Comments 조회
     */
    public List<GetComments> getKnowhowComments(int userIdx, int contentIdx) throws BaseException{
        try{
            List<GetComments> getKnowhowComments = contentsDao.getKnowhowComments(userIdx, contentIdx);
            return getKnowhowComments;
        }
        catch (Exception exception) {
            throw new BaseException(INVALID_COMMENTS);
        }
    }



}