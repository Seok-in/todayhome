package com.example.demo.src.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateOrderReq {
    private int productIdx;
    private int firstOptionIdx;
    private int secondOptionIdx;
    private int thirdOptionIdx;
    @Range(min = 1, max = 100,message = "INTEGER NUM")
    private int productNum;
}
