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
public class ReviewOther {
    private float rate;
    private List<String> reviewImage;
    private String productName;
    private String updatedAt;
    private String reviewText;
    private String reviewFlag;
}
