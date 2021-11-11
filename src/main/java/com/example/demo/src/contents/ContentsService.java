package com.example.demo.src.contents;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.contents.model.*;
import com.example.demo.src.contents.model.picture.*;
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
public class ContentsService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ContentsDao contentsDao;
    private final ContentsProvider contentsProvider;
    private final JwtService jwtService;


    @Autowired
    public ContentsService(ContentsDao contentsDao, ContentsProvider contentsProvider, JwtService jwtService) {
        this.contentsDao = contentsDao;
        this.contentsProvider = contentsProvider;
        this.jwtService = jwtService;

    }
    // 존재하지 않거나 status 'N'인 컨텐츠는 처음부터 좋아요, 스크랩 불가능 -> validation 처리 필요 x
    /**
     게시글 작성 API
     */
    public PostPicRes postPicContent(int userIdx, PostPicReq postPicReq) throws BaseException{
        try {
            PostPicRes contentIndices = contentsDao.postPicContent(userIdx, postPicReq);
            if(contentIndices.getContentIndices().size() != postPicReq.getPicContent().size())
                throw new BaseException(DATABASE_ERROR);
            return contentIndices;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     게시글 Like API
     */
    public void likeContents(String filter, int logonIdx, int contentIdx) throws BaseException{
        try {
            int result = contentsDao.likeContents(filter, logonIdx, contentIdx);
            if (result == 0)
                throw new BaseException(DATABASE_ERROR);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     게시글 Like 취소 API
     */
    public void unlikeContents(String filter, int logonIdx, int contentIdx) throws BaseException{
        try {
            int result = contentsDao.unlikeContents(filter, logonIdx, contentIdx);
            if (result == 0)
                throw new BaseException(DATABASE_ERROR);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     게시글 Scrap API
     */
    public void scrapContents(String filter, int logonIdx, int contentIdx) throws BaseException{
        try {
            int result = contentsDao.scrapContents(filter, logonIdx, contentIdx);
            if (result == 0)
                throw new BaseException(DATABASE_ERROR);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }



    /**
     게시글 Scrap 취소 API
     */
    public void unscrapContents(String filter, int logonIdx, int contentIdx) throws BaseException{
        try {
            int result = contentsDao.unscrapContents(filter, logonIdx, contentIdx);
            if (result == 0)
                throw new BaseException(DATABASE_ERROR);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     게시글 댓글 작성 API
     */
    public PostCommentRes writeComment(String filter, int logonIdx, int contentIdx, PostComment postComment) throws BaseException{
        try {
            int commentIdx = contentsDao.writeComment(filter, logonIdx, contentIdx, postComment);
            return new PostCommentRes(commentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     게시글 댓글 삭제 API
     */
    public void removeComment(int commentIdx) throws BaseException{
        try {
            int result = contentsDao.removeComment(commentIdx);
            if (result == 0)
                throw new BaseException(FAILED_TO_REMOVE_COMMENT);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     답글 작성 API
     */
    public PostCommentRes writeRecomment(int logonIdx, int commentIdx, PostComment postRecomment) throws BaseException{
        try {
            int recommentIdx = contentsDao.writeRecomment(logonIdx,commentIdx,postRecomment);
            return new PostCommentRes(recommentIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     답글 삭제 API
     */
    public void removeRecomment(int recommentIdx) throws BaseException{
        try {
            int result = contentsDao.removeRecomment(recommentIdx);
            if (result == 0)
                throw new BaseException(FAILED_TO_REMOVE_COMMENT);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
}