package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCoupons {
    private String coupon_name;
    private double discount_price;
    private int discount_percent;
    private double enable_price;
    private String detailed_explanation;
    private String received;
}