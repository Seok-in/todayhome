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
public class GetProductReviewRes {
    private Rate rate;
    private float avgRate;
    private int rateNum;
    private List<UserReview> userReviews;
}
