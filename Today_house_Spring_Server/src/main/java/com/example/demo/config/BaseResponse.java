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
    private  Boolean isSuccess;
    private  String message;
    private  int code;
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
    public BaseResponse(String resultMessage){
        this.isSuccess = Boolean.FALSE;
        this.message = resultMessage;
        switch(resultMessage){
            case "NULL PASSWORD":
                this.code = BaseResponseStatus.NULL_PASSWORD.getCode();
                this.message =BaseResponseStatus.NULL_PASSWORD.getMessage();
                break;

            case "SIZE PASSWORD":
                this.code = BaseResponseStatus.INVALID_SIZE_PW.getCode();
                this.message= BaseResponseStatus.INVALID_SIZE_PW.getMessage();
                break;

            case "NULL USERNAME":
                this.code = BaseResponseStatus.NULL_USERNAME.getCode();
                this.message = BaseResponseStatus.NULL_USERNAME.getMessage();
                break;

            case "SIZE USERNAME":
                this.code = BaseResponseStatus.INVALID_SIZE_USERNAME.getCode();
                this.message = BaseResponseStatus.INVALID_SIZE_USERNAME.getMessage();
        }

    }



}

