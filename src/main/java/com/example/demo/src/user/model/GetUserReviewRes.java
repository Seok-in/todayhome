package com.example.demo.src.user.model;

import com.example.demo.src.store.model.ReviewOther;
import com.example.demo.src.store.model.ReviewToday;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserReviewRes {
    private int reviewNum;
    private List<ReviewToday> reviewTodays;
    private List<ReviewOther> reviewOthers;
}
