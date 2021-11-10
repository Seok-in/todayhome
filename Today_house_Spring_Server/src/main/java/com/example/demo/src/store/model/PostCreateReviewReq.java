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
public class PostCreateReviewReq {
    private int productIdx;
    private List<String> reviewImages;
    private int rate;
    private String reviewText;
}
