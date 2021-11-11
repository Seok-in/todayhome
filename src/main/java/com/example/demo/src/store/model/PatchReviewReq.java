package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchReviewReq {
    private List<String> reviewImages;
    @NotBlank(message="RATE BLANK")
    @Positive(message ="RATE ZERO")
    private int rate;
    @NotBlank(message = "REVIEW BLANK")
    @Size(min=5, max = 400, message = "REVIEW TEXT")
    private String reviewText;
}
