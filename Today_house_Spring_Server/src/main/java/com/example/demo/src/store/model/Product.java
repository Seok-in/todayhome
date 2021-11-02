package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String prodImage;
    private String companyName;
    private String prodName;
    private int discountPercent;
    private int price;
    private float rate;
    private int reviewNum;
    private int scrap;
}
