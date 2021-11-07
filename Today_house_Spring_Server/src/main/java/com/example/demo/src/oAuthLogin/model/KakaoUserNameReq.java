package com.example.demo.src.oAuthLogin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserNameReq {
    private String accessToken;
    private String userName;
}
