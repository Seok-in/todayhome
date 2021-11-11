package com.example.demo.src.user;

import com.example.demo.src.oAuthLogin.model.KakaoLoginReq;
import com.example.demo.src.oAuthLogin.model.KakaoUserNameReq;
import com.example.demo.src.store.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/ohouse/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<BindingResult> handleValidException (MethodArgumentNotValidException ex) {
        BindingResult message = ex.getBindingResult();
        return new BaseResponse<>(message);
    }

    // 1. 회원가입 API
    // Body
    @ResponseBody
    @PostMapping("/signup")
    public BaseResponse<PostUserRes> createUser(@RequestBody @Validated PostUserReq postUserReq) {
        if(postUserReq.getUserEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getUserEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        // 닉네임 중복여부
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    // 2. 로그인 API
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> login(@RequestBody @Validated PostLoginReq postLoginReq){
        try{
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            //휴면계정
            if(userProvider.checkUserStatus(postLoginReq.getUserEmail())=="N"){
                return new BaseResponse<>(USER_STATUS_INVALID);
            }
            //탈퇴 계정
            else if(userProvider.checkUserStatus(postLoginReq.getUserEmail())=="D"){
                return new BaseResponse<>(USER_STATUS_DELETED);
            }
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 3. 로그아웃 API
    @ResponseBody
    @PostMapping("/logout")
    public BaseResponse<String> logout(){
        try{
            int userIdx = jwtService.getUserIdx();
            userProvider.logout(userIdx);
            return new BaseResponse<>("로그아웃 되었습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 4. 회원탈퇴 API
    @ResponseBody
    @PostMapping("/signout")
    public BaseResponse<String> signout(@RequestBody @Validated PostSignOutReq postSignOutReq){
        try{
            int userIdx = jwtService.getUserIdx();
            userService.createSignOut(postSignOutReq, userIdx);
            return new BaseResponse<>("회원탈퇴 되었습니다.");
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 12. 유저 최근 본 목록 조회 API
    @ResponseBody
    @GetMapping("/recency")
    public BaseResponse<GetUserRecentRes> getUserRecentRes(@RequestParam(required = false) @Validated @Size(max = 1,message = "INVALID FLAG") String flag){
        try{
            if (flag == null) {
                int userIdx = jwtService.getUserIdx();
                GetUserRecentRes getUserRecentRes = userProvider.getUserRecentRes(userIdx);
                return new BaseResponse<>(getUserRecentRes);
            }
            else{
                int userIdx = jwtService.getUserIdx();
                GetUserRecentRes getUserRecentRes = userProvider.getUserRecentRes(userIdx, flag);
                return new BaseResponse<>(getUserRecentRes);
            }
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 18. 유저 리뷰 조회 API
    @ResponseBody
    @GetMapping("/reviews")
    public  BaseResponse<GetUserReviewRes> getUserReviews(){
        try{
            int userIdx = jwtService.getUserIdx();;
            GetUserReviewRes getUserReviewRes = userProvider.getUserReviewRes(userIdx);
            return new BaseResponse<>(getUserReviewRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 19.1 유저 리뷰 작성 API(오늘의 집)
    @ResponseBody
    @PostMapping("/review/{orderIndex}/{productIdx}")
    public BaseResponse<String> createReviewByToday(@PathVariable("orderIndex") int orderIndex, @PathVariable("productIdx") int productIdx,
                                                    @RequestBody PostCreateReviewOhouseReq postCreateReviewOhouseReq){
        try{
            int userIdx = jwtService.getUserIdx();
            userService.createReviewByToday(userIdx, productIdx, orderIndex, postCreateReviewOhouseReq);
            return new BaseResponse<>("리뷰 작성 성공! By TodayHouse");
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 19.2 유저 리뷰 작성 API (다른 사이트)
    @ResponseBody
    @PostMapping("/review")
    public BaseResponse<String> createReviewByOhter(@RequestBody PostCreateReviewReq postCreateReviewReq){
        try{
            int userIdx = jwtService.getUserIdx();
            userService.createReviewByOther(userIdx, postCreateReviewReq);
            return new BaseResponse<>("리뷰 작성 성공! By Others");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 20.1 유저 리뷰 수정 API(오늘의 집)
    @ResponseBody
    @PatchMapping("/t-reviews/{reviewIdx}")
    public BaseResponse<String> modifyReviewByToday(@PathVariable("reviewIdx") int reviewIdx, @Validated PatchHouseReviewReq patchHouseReviewReq){
        try {
            int userIdx = jwtService.getUserIdx();
            userService.modifyReviewByToday(reviewIdx, patchHouseReviewReq);
            return new BaseResponse<>("리뷰 수정 성공! By Today");
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 20.2 유저 리뷰 수정 API(다른 사이트)
    @ResponseBody
    @PatchMapping("/o-reviews/{reviewIdx}")
    public BaseResponse<String> modifyReviewByOther(@PathVariable("reviewIdx") int reviewIdx, @Validated PatchReviewReq patchReviewReq){
        try{
            int userIdx = jwtService.getUserIdx();
            userService.modifyReviewByOther(userIdx, reviewIdx, patchReviewReq);
            return new BaseResponse<>("리뷰 수정 성공! By Others");
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 21 유저 리뷰 삭제 API
    @ResponseBody
    @PatchMapping("/reviews/{reviewIdx}")
    public BaseResponse<String> deleteReview(@PathVariable("reviewIdx") int reviewIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            userService.deleteReview(reviewIdx, userIdx);
            return new BaseResponse<>("리뷰 삭제 성공!");
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    // 22. 유저 질문과 답변 조회 API
    @ResponseBody
    @GetMapping("/questions")
    public BaseResponse<List<GetQuestionRes>> getQuestions(){
        try{
            int userIdx = jwtService.getUserIdx();
            List<GetQuestionRes> getQuestionRes = userProvider.getQuestionResByUser(userIdx);
            return new BaseResponse<>(getQuestionRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 23. 상품 문의 작성 API
    @ResponseBody
    @PostMapping("/question/{productIdx}")
    public BaseResponse<String> createQuestion(@PathVariable("productIdx") int productIdx, @RequestBody @Validated PostProductQuestReq postProductQuestReq){
        try{
            int userIdx = jwtService.getUserIdx();
            userService.createQuestion(postProductQuestReq, userIdx, productIdx);
            return new BaseResponse<>("문의 작성 완료");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 24. 상품 문의 삭제 API
    @ResponseBody
    @PatchMapping("/question/{questionIdx}")
    public BaseResponse<String> deleteQuestion(@PathVariable("questionIdx") int questionIdx) {
        try {
            int userIdx = jwtService.getUserIdx();
            userService.deleteQuestion(questionIdx, userIdx);
            return new BaseResponse<>("문의 삭제 완료");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 팔로우
     * [POST], [PATCH] /users/login
     */
    @ResponseBody
    @PostMapping(value = "/follow"/*, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE*/)
    public BaseResponse<String> userFollow(@RequestParam(value="userId", required=false, defaultValue="") int userId ){
        try{
            int followerId = jwtService.getUserIdx();
            userService.userFollow(userId,followerId);
            String result = "" ;
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 팔로우
     * [PATCH] /users/login
     */
    @ResponseBody
    @PatchMapping(value = "/unfollow"/*, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE*/)
    public BaseResponse<String> userUnfollow(@RequestParam(value="userId", required=false, defaultValue="") int userId ){
        try{
            int followerId = jwtService.getUserIdx();
            userService.userUnfollow(userId,followerId);
            String result = "" ;
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
