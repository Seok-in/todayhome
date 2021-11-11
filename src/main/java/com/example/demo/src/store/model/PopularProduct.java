package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PopularProduct {

        private String prodImage;
        private String companyName;
        @NotBlank(message = "RESULT NULL")
        private String productName;
        private int discountPercent;
        private int price;
        private float rate;
        private int reviewNum;
        private String scrap;
        private int visitNum;

}
