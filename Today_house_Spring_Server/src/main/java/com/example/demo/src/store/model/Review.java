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
    private List<String> reviewImage;
    private int productName;
    private String firstOptionName;
    private String secondOptionName;
    private String thirdOptionName;
    private String createdAt;
    private String reviewText;
    private String reviewFlag;
}
