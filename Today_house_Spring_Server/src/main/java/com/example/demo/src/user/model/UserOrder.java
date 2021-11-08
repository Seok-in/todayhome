package com.example.demo.src.user.model;

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
public class UserOrder {
    private int orderIdx;
    private String createdAt;
    private List<OrderProduct> orderProducts;
}
