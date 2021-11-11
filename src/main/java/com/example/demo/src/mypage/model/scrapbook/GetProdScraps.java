package com.example.demo.src.mypage.model.scrapbook;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetProdScraps {
    private String imagePath;
    private String companyName;
    private String productName;
    private int salePercent;
    private int productPrice;
    private float rate;
    private int reviewNum;
}