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
public class PatchHouseReviewReq {
    private List<String> reviewImages;
    private String reviewText;
    private int priceRate;
    private int designRate;
    private int deliveryRate;
    private int healthRate;
}
