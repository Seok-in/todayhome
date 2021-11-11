package com.example.demo.src.order.model;

import com.example.demo.src.store.model.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchCartStatusReq {
    private String productIdx;
    private ProductOption productOption;
}
