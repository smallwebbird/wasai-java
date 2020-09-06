package com.example.wasai.config.securityConfig;

import com.example.wasai.pojo.user.User;
import com.example.wasai.pojo.user.UserRoleAndUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
public class CustomUserDetail implements UserDetails {
    private User user;
    private UserRoleAndUser userRoleAndUser;

    public CustomUserDetail(User u, UserRoleAndUser ur) {
        this.user = u;
        this.userRoleAndUser = ur;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //返回当前用户的权限
        return userRoleAndUser.getPermissionList().stream()
                .map(permission ->new SimpleGrantedAuthority(permission))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
