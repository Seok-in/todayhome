package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String serviceText;
}
