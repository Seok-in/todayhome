package com.example.demo.src.oAuthLogin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserNameReq {
    @NotBlank(message="INVALID ACCESSTOKEN")
    private String accessToken;
    @NotBlank(message = "NULL USERNAME")
    @Size(min = 2, max = 15, message = "SIZE USERNAME")
    private String userName;
}
