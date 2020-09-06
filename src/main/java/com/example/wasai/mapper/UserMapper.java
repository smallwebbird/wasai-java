package com.example.wasai.mapper;

import com.example.wasai.pojo.user.User;
import com.example.wasai.pojo.user.UserRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    //获取所有用户
    List<User> getUsers();

    // 插入用户
    void insert(User user);

    // 通过名字查询用户
    List<User> getUserByName(String name);

    // 获取用户的role,以及权限
    UserRole getUserRole(int id);

    // 通过id获取用户
    User getUserById(int id);
}
