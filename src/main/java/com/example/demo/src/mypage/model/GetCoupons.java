package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCoupons {
    private String couponName;
    private int discountPrice;
    private int discountPercent;
    private int enablePrice;
    private String expiredAt;
    private String received;
}