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
public class UserReview {
    private List<String> reviewImages;
    private String userName;
    private String firstOptionName;
    private String secondOptionName;
    private String thirdOptionName;
    private String reviewFlag;
    private String updatedAt;
    private String helpfulFlag;
    private int helpfulNum;
    private String reviewText;
    private float rate;
    private int priceRate;
    private int designRate;
    private int deliveryRate;
    private int healthRate;
}
