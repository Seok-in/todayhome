package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    private int couponIdx;
    private String couponName;
    private int percent;
    private int price;
    private int enabledPrice;
    private String expiredAt;
}
