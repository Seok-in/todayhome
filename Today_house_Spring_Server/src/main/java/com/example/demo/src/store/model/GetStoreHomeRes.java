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
public class GetStoreHomeRes {
    private List<GetAdRes> advertisements;
    private List<GetStoreCategoryRes> storeCategories;
    private List<Product> recentProducts;
    private List<PopularProduct> popularProducts;
}
