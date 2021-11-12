package com.example.demo.src.order.model;

import com.example.demo.src.store.model.OrderProduct;
import com.example.demo.src.user.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostOrderReq {
    @NotBlank(message = "BLANK USER")
    private String userRealName;
    @NotBlank(message = "BLANK USER")
    private String userCall;
    @NotBlank(message = "BLANK USER")
    @Email(message = "EMAIL")
    private String userRecentEmail;
    @NotBlank(message = "BLANK RECEIVER")
    private String receiverName;
    @NotBlank(message = "BLANK RECEIVER")
    private String receiverCall;
    @NotBlank(message = "BLANK ADDR")
    private String address;
    @NotBlank(message = "BLANK ADDR")
    private String detailAddress;
    private String request;
    private int point;
    private int couponIdx;
    private int price;
    private String couponCode;
    private String payment;
    private String agreeStatus;
}
