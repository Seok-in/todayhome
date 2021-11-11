package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import javax.naming.Binding;

import static com.example.demo.config.BaseResponseStatus.SUCCESS;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {
    @JsonProperty("isSuccess")
    private Boolean isSuccess;
    private String message;
    private int code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 요청에 성공한 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS.isSuccess();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
        this.result = result;
    }

    // 요청에 실패한 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }

    //validation
    public BaseResponse(BindingResult result){
        String resultMessage = result.getFieldError().getDefaultMessage();
        switch(resultMessage){
            case "NULL PASSWORD":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.NULL_PASSWORD.getCode();
                this.message =BaseResponseStatus.NULL_PASSWORD.getMessage();
                break;

            case "SIZE PASSWORD":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.INVALID_SIZE_PW.getCode();
                this.message= BaseResponseStatus.INVALID_SIZE_PW.getMessage();
                break;

            case "NULL USERNAME":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.NULL_USERNAME.getCode();
                this.message = BaseResponseStatus.NULL_USERNAME.getMessage();
                break;

            case "SIZE USERNAME":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.INVALID_SIZE_USERNAME.getCode();
                this.message = BaseResponseStatus.INVALID_SIZE_USERNAME.getMessage();
                break;

            case "INVALID ACCESSTOKEN":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.INVALID_KAKAO_ACCESS_TOKEN.getCode();
                this.message = BaseResponseStatus.INVALID_KAKAO_ACCESS_TOKEN.getMessage();
                break;

            case "QUESTION SIZE":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.POST_QUESTIONS_SIZE.getCode();
                this.message = BaseResponseStatus.POST_QUESTIONS_SIZE.getMessage();
                break;

            case "QUESTION BLANK":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.POST_QUESTIONS_BLANK.getCode();
                this.message = BaseResponseStatus.POST_QUESTIONS_BLANK.getMessage();
                break;

            case "REVIEW BLANK":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.POST_REVIEWS_BLANK.getCode();
                this.message = BaseResponseStatus.POST_REVIEWS_BLANK.getMessage();
                break;

            case "RATE ZERO":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.POST_REVIEWS_ZERO.getCode();
                this.message = BaseResponseStatus.POST_REVIEWS_ZERO.getMessage();
                break;

            case "REVIEW TEXT":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.POST_REVIEWS_SIZE.getCode();
                this.message = BaseResponseStatus.POST_REVIEWS_SIZE.getMessage();
                break;

            case "RATE BLANK":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.POST_RATE_NULL.getCode();
                this.message = BaseResponseStatus.POST_RATE_NULL.getMessage();
                break;

            case "RESULT NULL":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.NO_RESULT_DATA.getCode();
                this.message = BaseResponseStatus.NO_RESULT_DATA.getMessage();
                break;

            case "INTEGER NUM":
                this.isSuccess = Boolean.FALSE;
                this.code = BaseResponseStatus.INVALID_PRODUCT_NUM.getCode();
                this.message =BaseResponseStatus.INVALID_PRODUCT_NUM.getMessage();
                break;
        }

    }



}

