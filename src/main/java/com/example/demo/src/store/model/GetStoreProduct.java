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
public class GetStoreProduct{

    private String companyName;
    private String productName;
    private int discountPercent;
    private int originPrice;
    private int salePrice;
    private String productInfo;
    private String paymentWay;
    private int deliveryFee;
    private int mountainFee;
    private int reviewNum;
    private int questionNum;
    private float rate;

}
