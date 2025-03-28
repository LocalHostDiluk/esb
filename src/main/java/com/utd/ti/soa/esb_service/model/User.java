package com.utd.ti.soa.esb_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String username;
    private String phone;
    private String password;

    /*
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }*/
    
}
