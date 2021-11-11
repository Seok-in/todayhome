package com.example.demo.src.order.model;

import com.example.demo.src.store.model.Coupon;
import com.example.demo.src.store.model.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateOrderRes {
    private String userName;
    private String userEmail;
    private String userCall;
    private List<OrderProduct> orderProduct;
    private int userPoint;
    private List<Coupon> coupons;
}
