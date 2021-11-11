package com.example.demo.src.oAuthLogin;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.oAuthLogin.*;
import com.example.demo.src.oAuthLogin.model.*;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ohouse/kakao")
public class oAuthLoginController {


        final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        private final oAuthLoginProvider oAuthLoginProvider;
        @Autowired
        private final oAuthLoginService oAuthLoginService;
        @Autowired
        private final JwtService jwtService;


        public oAuthLoginController(oAuthLoginService oAuthLoginService, oAuthLoginProvider oAuthLoginProvider, JwtService jwtService) {
            this.oAuthLoginProvider = oAuthLoginProvider;
            this.oAuthLoginService = oAuthLoginService;
            this.jwtService = jwtService;
        }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<BindingResult> handleValidException (MethodArgumentNotValidException ex) {
        BindingResult message = ex.getBindingResult();
        return new BaseResponse<>(message);
    }
    // 6. KAKAO 로그인
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> kakaoLogin(@RequestBody @Validated KakaoLoginReq kakaoLoginReq){
        try{
            String accessToken = kakaoLoginReq.getAccessToken();
            PostLoginRes postLoginRes = oAuthLoginProvider.kakaoLogin(accessToken);
            return new BaseResponse<>(postLoginRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/{orderIndex}/test")
    public BaseResponse<KakaoPayReq> kakaoPayTest(@PathVariable ("orderIndex") int orderindex){
            try{
                int userIdx= jwtService.getUserIdx();
                KakaoPayReq kakaoPayReq = oAuthLoginService.kakaoPayReq(orderindex, userIdx);
                return new BaseResponse<>(kakaoPayReq);
            }
            catch(BaseException exception){
                return new BaseResponse<>((exception.getStatus()));
        }
    }
    // 6-1. KAKAO 회원가입
    @ResponseBody
    @PostMapping("/signup")
    public BaseResponse<PostLoginRes> kakaoSignup(@RequestBody @Validated KakaoUserNameReq kakaoUserNameReq){
        try{
            PostLoginRes postLoginRes = oAuthLoginService.createKakaoUser(kakaoUserNameReq);
            return new BaseResponse<>(postLoginRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // * O-AUTH 카카오 토큰 받기 (임시구현)
    @ResponseBody
    @GetMapping("/auth/kakao/callback")
    public BaseResponse<String> oAuthKaKaoToken(String code){
        String accessToken = oAuthLoginService.getKakaoToken(code);
        return new BaseResponse<>(accessToken);
    }

    // 73.1 카카오페이 결제시스템 결제준비
    @ResponseBody
    @PostMapping("/{orderIndex}/payment/ready")
    public BaseResponse<KakaoPayRes> kakaoPay(@PathVariable ("orderIndex") int orderIndex){
            try{
                int userIdx = jwtService.getUserIdx();
                KakaoPayRes kakaoPayRes = oAuthLoginService.payReady(orderIndex, userIdx);

                return new BaseResponse<>(kakaoPayRes);
            }
            catch(BaseException exception){
                return new BaseResponse<>((exception.getStatus()));
            }
    }
    // 73.2 카카오페이 결제시스템 결과 승인 정보
    @ResponseBody
    @PostMapping("/{orderIndex}/payment/approval")
    public BaseResponse<KakaoPayApproveRes> kakaoApprove(@PathVariable("orderIndex") int orderIndex,
                                                        @RequestParam(required = false) String pg_token,
                                                         @RequestBody KakaoPayApproveReq kakaoPayApproveReq) {
        try {
            if(pg_token == null){
                return new BaseResponse<>(BaseResponseStatus.FAILED_TO_PAYMENT);
            }
            int userIdx = jwtService.getUserIdx();
            KakaoPayApproveRes kakaoPayApproveRes = oAuthLoginService.kakaoApprove(kakaoPayApproveReq, orderIndex, userIdx, pg_token);
            return new BaseResponse<>(kakaoPayApproveRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
