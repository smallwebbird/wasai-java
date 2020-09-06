package com.example.wasai.service.ServiceImpl;
import com.example.wasai.config.redisConfig.RedisService;
import com.example.wasai.config.securityConfig.JwtTokenUtil;
import com.example.wasai.mapper.UserMapper;
import com.example.wasai.pojo.user.User;
import com.example.wasai.pojo.user.UserRole;
import com.example.wasai.pojo.user.UserRoleAndUser;
import com.example.wasai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    RedisService redisService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Value("${jwt.expiration}")
    private Long expiration;
    @Value("${jwt.validTime}")
    private Long validTime;


    @Override
    public Map login(String name, String pwd) {
        String token = null;
        Map<String, Object> loginReturnMap = new HashMap<>();
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(name);
            if (!passwordEncoder.matches(pwd, userDetails.getPassword())) {
                log.error("密码不正确");
                return loginReturnMap;
            }
            token = jwtTokenUtil.generateToken(userDetails);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtId = jwtTokenUtil.getJWTIDFromToken(token);
            //System.out.println(userDetails.getAuthorities());
            // 如果是第一次登陆，那么就直接存入redis
            redisService.setTokenRefresh(userDetails.getUsername(), token, expiration, validTime, jwtId);
            loginReturnMap.put("permission", userDetails.getAuthorities());
            loginReturnMap.put("token", token);
            loginReturnMap.put("tokenHead", tokenHead);
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return loginReturnMap;
    }

    @Override
    public User register(User registerUser) {
        User user = new User();
        BeanUtils.copyProperties(registerUser, user);
        user.setCreateTime(new Date());
        List<User> userList = userMapper.getUserByName(user.username);
        if (userList.size() > 0) {
            return null;
        }
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getUserByName(String name) {
        List<User> users = userMapper.getUserByName(name);
        if (users != null && users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public UserRoleAndUser getUserRole(User user) {
        UserRole userRole = userMapper.getUserRole(user.id);
        String[] permissionList = new String[10];
        UserRoleAndUser userRoleAndUser;
        // 将权限字符串转为数组
        if (userRole != null) {
            String permissionStr = userRole.userAuthority.authorities;
            if (permissionStr != null) {
                permissionList = permissionStr.split(",");
            }
            userRoleAndUser = new UserRoleAndUser(user, user.username, Arrays.asList(permissionList));
            return userRoleAndUser;
        }
        return null;
    }

    @Override
    public UserRoleAndUser getUserInfo(int userId) {
        User user = userMapper.getUserById(userId);
        return getUserRole(user);
    }
}
