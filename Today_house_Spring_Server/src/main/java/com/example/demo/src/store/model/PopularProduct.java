package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PopularProduct {

        private String prodImage;
        private String companyName;
        private String productName;
        private int discountPercent;
        private int price;
        private float rate;
        private int reviewNum;
        private String scrap;
        private int visitNum;

}
