package com.example.demo.src.user;

import com.example.demo.src.oAuthLogin.model.KakaoLoginReq;
import com.example.demo.src.oAuthLogin.model.KakaoUserNameReq;
import com.example.demo.src.store.model.GetQuestionRes;
import com.example.demo.src.store.model.PostProductQuestReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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

    // 1. 회원가입 API
    // Body
    @ResponseBody
    @PostMapping("/signup")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
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
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq){
        try{
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
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
    public BaseResponse<String> signout(@RequestBody PostSignOutReq postSignOutReq){
        try{
            int userIdx = jwtService.getUserIdx();
            userService.createSignOut(postSignOutReq, userIdx);
            return new BaseResponse<>("회원탈퇴 되었습니다.");
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @PostMapping("/{productIdx}/question")
    public BaseResponse<String> createQuestion(@PathVariable("productIdx") int productIdx, @RequestBody PostProductQuestReq postProductQuestReq){
        try{
            int userIdx = jwtService.getUserIdx();
            userService.createQuestion(postProductQuestReq, userIdx, productIdx);
            return new BaseResponse<>("문의 작성 완료");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/question/{questionIdx}")
    public BaseResponse<String> deleteQuestion(@PathVariable("questionIdx") int questionIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            userService.deleteQuestion(questionIdx, userIdx);
            return new BaseResponse<>("문의 삭제 완료");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

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

    @ResponseBody
    @GetMapping("/recency")
    public BaseResponse<GetUserRecentRes> getUserRecentRes(@RequestParam(required = false) String flag){
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
}
