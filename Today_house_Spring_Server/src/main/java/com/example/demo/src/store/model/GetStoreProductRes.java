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
public class GetStoreProductRes {
    private List<String> productImages;
    private String companyName;
    private String productName;
    private int discountPercent;
    private int originPrice;
    private int salePrice;
    private GetAdRes advertisement;
    private String productInfo;
    private int reviewNum;
    private float rate;
    private int fiveRate;
    private int fourRate;
    private int threeRate;
    private int twoRate;
    private int oneRate;
}
