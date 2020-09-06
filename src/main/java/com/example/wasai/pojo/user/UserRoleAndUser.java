package com.example.wasai.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleAndUser implements Serializable {
    private User userInfo;
    private String uRole;
    private List<String> permissionList;
}
