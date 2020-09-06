package com.example.wasai.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    public int id;

    public String username;

    public int uid;

    public UserAuthority userAuthority;
}

enum ROLES {
    ADMIN("admin"), USER("user");

    private String desc;
    private ROLES(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
