package com.example.wasai.controller;

import com.example.wasai.common.api.CommonResult;
import com.example.wasai.pojo.user.User;
import com.example.wasai.pojo.user.UserRoleAndUser;
import com.example.wasai.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "UserController", description = "用户注册/登录/获取用户信息")
@Controller
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "/register")
    @ResponseBody
    public CommonResult<User> register(@RequestBody User registerUser, BindingResult bindingResult) {
        User user = userService.register(registerUser);
        if (user == null) {
            return CommonResult.failed();
        }
        return CommonResult.success(user);
    }
    @ApiOperation(value = "用户登录")
    @PostMapping(value = "/login")
    @ResponseBody
    // TODO: 2020/9/1 登录之后的返回格式需要调整 
    public CommonResult login(@RequestBody User loginUser, BindingResult bindingResult) {
        Map loginReturnMap = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (loginReturnMap == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        return CommonResult.success(loginReturnMap);
    }

    @ApiOperation(value = "获取用户信息")
    @PostMapping(value = "/getUser")
    @ResponseBody
    public CommonResult getUser(@RequestBody Map<String ,Object> requestParams) {
        int id = (int) requestParams.get("userId");
        UserRoleAndUser userRoleAndUser = userService.getUserInfo(id);
        if (userRoleAndUser == null) {
            return CommonResult.validateFailed("用户id为空");
        }
        return CommonResult.success(userRoleAndUser);
    }
}
