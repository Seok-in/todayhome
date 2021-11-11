package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProduct {
    private String productName;
    private String companyName;
    private String firstOptionName;
    private String secondOptionName;
    private String thirdOptionName;
    private String delivery;
    private int salePrice;
    private int deliveryFee;
    private int num;
    private int price;
}
