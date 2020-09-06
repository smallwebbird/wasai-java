package com.example.wasai.service;

import com.example.wasai.pojo.user.User;
import com.example.wasai.pojo.user.UserRoleAndUser;

import java.util.Map;

public interface UserService {
    /**
     * desc: 用户登录
     * @return String
     */
    Map login(String name, String pwd);

    /**
     * desc: 用户注册
     * @param registerUser
     * @return
     */
    User register(User registerUser);

    /**
     * desc: 根据用户名字搜索用户
     */
    User getUserByName(String name);

    UserRoleAndUser getUserRole(User user);

    UserRoleAndUser getUserInfo(int userId);
}
