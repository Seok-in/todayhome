package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProductRes {
    private List<String> productImage;
    private String companyName;
    private String firstCtgName;
    private String secondCtgName;
    private String productName;
    private Float avgRate;
    private int rateNum;
    private int price;
    private int discountPercent;
    private int salePrice;
    private String paymentWay;
    private String deliveryFee;
    private String productInfo;
    private Rate rate;
    private List<Review> review;
}
