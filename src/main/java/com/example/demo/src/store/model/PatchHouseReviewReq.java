package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchHouseReviewReq {
    private List<String> reviewImages;
    @NotBlank(message = "REVIEW BLANK")
    @Size(min=5, max = 400, message = "REVIEW TEXT")
    private String reviewText;
    @NotNull(message="RATE BLANK")
    @Positive(message ="RATE ZERO")
    private int priceRate;
    @NotNull(message="RATE BLANK")
    @Positive(message ="RATE ZERO")
    private int designRate;
    @NotNull(message="RATE BLANK")
    @Positive(message ="RATE ZERO")
    private int deliveryRate;
    @NotNull(message="RATE BLANK")
    @Positive(message ="RATE ZERO")
    private int healthRate;
}
