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
public class GetStoreSecondCtgRes {
    private List<GetAdRes> advertisements;
    private List<PopularProduct> popularProducts;

}
