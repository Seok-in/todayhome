package com.example.demo.src.contents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.contents.model.*;
import com.example.demo.src.contents.model.house.*;
import com.example.demo.src.contents.model.knowhow.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Base64.Decoder;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/ohouse/contents")
public class ContentsController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ContentsProvider contentsProvider;
    @Autowired
    private final ContentsService contentsService;
    @Autowired
    private final JwtService jwtService;

    public ContentsController(ContentsProvider contentsProvider, ContentsService contentsService, JwtService jwtService){
        this.contentsProvider = contentsProvider;
        this.contentsService = contentsService;
        this.jwtService = jwtService;
    }

    /**
    집들이 게시글 조회 API
     */
    @ResponseBody
    @GetMapping("/house/{contentIdx}")
    public BaseResponse<GetAllHouseContents> getHouseContents (@PathVariable("contentIdx") int contentIdx) {
        try{
            int userIdx = jwtService.getUserIdx();

            List<GetHouseIntro> getHouseIntro = contentsProvider.getHouseIntro(userIdx,contentIdx);
            List<GetHouseContents> getHouseContents = contentsProvider.getHouseContents(userIdx,contentIdx);
            List<GetSocialInfo> getSocialInfo = contentsProvider.getSocialInfo(userIdx, contentIdx);
            List<GetComments> getComments = contentsProvider.getComments(userIdx, contentIdx);

            GetAllHouseContents getAllHouseContents = new GetAllHouseContents(getHouseIntro, getHouseContents, getSocialInfo, getComments);
            return new BaseResponse<>(getAllHouseContents);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     knowhow 게시글 조회 API
     */
    @ResponseBody
    @GetMapping("/knowhow/{contentIdx}")
    public BaseResponse<GetAllKnowhowContents> getKnowhowContents (@PathVariable("contentIdx") int contentIdx) {
        try{
            int userIdx = jwtService.getUserIdx();

            List<GetKnowhowIntro> getKnowhowIntro = contentsProvider.getKnowhowIntro(userIdx,contentIdx);
            List<GetKnowhowContents> getKnowhowContents = contentsProvider.getKnowhowContents(userIdx,contentIdx);
            List<GetSocialInfo> getSocialInfo = contentsProvider.getKnowhowSocialInfo(userIdx, contentIdx);
            List<GetComments> getComments = contentsProvider.getKnowhowComments(userIdx, contentIdx);

            GetAllKnowhowContents getAllKnowhowContents = new GetAllKnowhowContents(getKnowhowIntro, getKnowhowContents, getSocialInfo, getComments);
            return new BaseResponse<>(getAllKnowhowContents);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     게시글 좋아요 API
     */
    @ResponseBody
    @PostMapping("/{filter}/{contentIdx}/praise")
    public BaseResponse<String> likeContents (@PathVariable("filter") String filter, @PathVariable("contentIdx") int contentIdx) {
        try{
            if(!(filter.equals("knowhow")||filter.equals("house")||filter.equals("picture")))
                return new BaseResponse<>(INVALID_USER_ACCESS);
            int logonIdx = jwtService.getUserIdx();
            contentsService.likeContents(filter, logonIdx,contentIdx);
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시글 좋아요 취소
     */
    @ResponseBody
    @PatchMapping(value = "/{filter}/{contentIdx}/unpraise")
    public BaseResponse<String> unlikeContents(@PathVariable("filter") String filter, @PathVariable("contentIdx") int contentIdx ){
        try{
            if(!(filter.equals("knowhow")||filter.equals("house")||filter.equals("picture")))
                return new BaseResponse<>(INVALID_USER_ACCESS);
            int logonIdx = jwtService.getUserIdx();
            contentsService.unlikeContents(filter,logonIdx,contentIdx);
            String result = "" ;
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     게시글 스크랩 API
     */
    @ResponseBody
    @PostMapping("/{filter}/{contentIdx}/scrap")
    public BaseResponse<String> scrapContents (@PathVariable("filter") String filter, @PathVariable("contentIdx") int contentIdx) {
        try{
            if(!(filter.equals("knowhow")||filter.equals("house")||filter.equals("picture")||filter.equals("product")))
                return new BaseResponse<>(INVALID_USER_ACCESS);
            int logonIdx = jwtService.getUserIdx();
            contentsService.scrapContents(filter, logonIdx,contentIdx);
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시글 스크랩 취소
     */
    @ResponseBody
    @PatchMapping(value = "/{filter}/{contentIdx}/unscrap")
    public BaseResponse<String> unscrapContents(@PathVariable("filter") String filter, @PathVariable("contentIdx") int contentIdx ){
        try{
            if(!(filter.equals("knowhow")||filter.equals("house")||filter.equals("picture")||filter.equals("product")))
                return new BaseResponse<>(INVALID_USER_ACCESS);
            int logonIdx = jwtService.getUserIdx();
            contentsService.unscrapContents(filter,logonIdx,contentIdx);
            String result = "" ;
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     게시글 댓글 작성
     */
    @ResponseBody
    @PostMapping("/{filter}/{contentIdx}/write-comment")
    public BaseResponse<PostCommentRes> writeComment(@PathVariable("filter") String filter, @PathVariable("contentIdx") int contentIdx, @RequestBody PostComment postComment) {
        // 아무것도 작성하지 않았을 경우
        if(!(filter.equals("knowhow")||filter.equals("house")||filter.equals("picture")))
            return new BaseResponse<>(INVALID_USER_ACCESS);
        if(postComment.getComment() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_COMMENT);
        }
        try{
            int logonIdx = jwtService.getUserIdx();
            PostCommentRes postCommentRes = contentsService.writeComment(filter,logonIdx, contentIdx, postComment);
            return new BaseResponse<>(postCommentRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     게시글 댓글 삭제
     */
    @ResponseBody
    @PatchMapping("/remove-comment")
    public BaseResponse<String> removeComment(@RequestParam(value="commentIdx", required=false, defaultValue="0") int commentIdx) {
        if(commentIdx == 0){
            return new BaseResponse<>(REMOVE_USERS_EMPTY_COMMENT);
        }
        try{
            contentsService.removeComment(commentIdx);
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     답글 작성
     */
    @ResponseBody
    @PostMapping("/write-recomment")
    public BaseResponse<PostCommentRes> writeRecomment(@RequestBody PostComment postRecomment,@RequestParam(value="commentIdx", required=false, defaultValue="0") int commentIdx) {
        if(postRecomment.getComment() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_COMMENT);
        }
        try{
            int logonIdx = jwtService.getUserIdx();
            PostCommentRes postCommentRes = contentsService.writeRecomment(logonIdx, commentIdx, postRecomment);
            return new BaseResponse<>(postCommentRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     답글 삭제
     */
    @ResponseBody
    @PatchMapping("/remove-recomment")
    public BaseResponse<String> removeRecomment(@RequestParam(value="recommentIdx", required=false, defaultValue="0") int recommentIdx) {
        if(recommentIdx == 0){
            return new BaseResponse<>(REMOVE_USERS_EMPTY_COMMENT);
        }
        try{
            contentsService.removeRecomment(recommentIdx);
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }




}