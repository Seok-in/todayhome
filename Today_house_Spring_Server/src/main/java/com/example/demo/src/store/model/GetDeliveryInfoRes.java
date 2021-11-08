package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetDeliveryInfoRes {
    private String deliveryWay;
    private int deliveryFee;
    private String paymentWay;
    private int mountainFee;
    private String disabledArea;
    private String numDeliveryFlag;
    private String etc;
    private int exchageFee;
    private int refundFee;
    private String address;
}
