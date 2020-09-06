package com.example.wasai;

import com.example.wasai.mapper.UserMapper;
import com.example.wasai.pojo.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
class WasaiApplicationTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    UserMapper userMapper;


    @Test
    void contextLoads() throws SQLException {
        User userList = userMapper.getUserById(1);
        System.out.println(userList);
    }

}
