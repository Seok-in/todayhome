package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSignOutReq {

    private String lowUse;
    private String reSignup;
    private String lowResource;
    private String protection;
    private String lowService;
    private String etc;
    private String agreement;
    @Size(max = 500,message = "ServiceText SIZE")
    private String serviceText;
}
