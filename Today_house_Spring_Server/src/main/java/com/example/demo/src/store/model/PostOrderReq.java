package com.example.demo.src.store.model;

import com.example.demo.src.user.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostOrderReq {
    private UserInfo userInfo;
    private String receiverName;
    private String receiverCall;
    private String address;
    private String detailAddress;
    private String request;
    private List<OrderProduct> orderProducts;
    private int point;
    private int couponIdx;
    private String couponCode;
    private String payment;
    private int price;
    private int deliveryPrice;
}
