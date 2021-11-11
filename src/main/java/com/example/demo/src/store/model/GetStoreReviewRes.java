package com.example.demo.src.store.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStoreReviewRes {
    private int reviewNum;
    private int rate;
    private int fiveRateNum;
    private int fourRateNum;
    private int threeRateNum;
    private int twoRateNum;
    private int oneRateNum;

}
