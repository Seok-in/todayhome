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
    private GetStoreProduct storeProduct;
    private List<GetAdRes> advertisement;
    private Rate rateNum;
    private List<String> reviewImages;
    private List<ReviewToday> reviewTodays;
}
