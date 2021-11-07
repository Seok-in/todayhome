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
public class Review {
    private float rate;
    private int priceRate;
    private int designRate;
    private int deliveryRate;
    private int healthRate;
    private int helpfulNum;
    private String createdAt;
    private List<String> reviewImage;
    private String productName;
    private String userName;
    private String userImage;
    private String orderFlag;
    private String productOption;
    private String reviewText;
    private String helpfulStatus;
}
