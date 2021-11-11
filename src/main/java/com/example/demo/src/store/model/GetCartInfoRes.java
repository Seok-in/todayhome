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
public class GetCartInfoRes {
    private int sumPrice;
    private int sumDeliveryFee;
    private int sumSales;
    private int resultPrice;
    private List<OrderProduct> orderProducts;
}
