package com.example.wasai.config.securityConfig;

import com.example.wasai.pojo.user.User;
import com.example.wasai.pojo.user.UserRoleAndUser;
import com.example.wasai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/",
            "/*.html",
            "/favicon.ico",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js",
            "/swagger-ui.html",
            "/v2/api-docs", // swagger api json
            "/swagger-resources/**"
    };

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(AUTH_WHITELIST);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http    .cors().and()
                .csrf().disable()
                .sessionManagement() //定制自己的session策略
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 让spring security 不生成session
                .and()
                .authorizeRequests()
                // 登录和注册不需要认证
                .antMatchers("/user/login", "/user/register").permitAll()
                // 跨域请求会先进行一次option请求
                .antMatchers(HttpMethod.OPTIONS).permitAll()
//                .antMatchers("/**").permitAll();
                .anyRequest().authenticated();
        // 添加JWT filter
        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        http    // 权限相关的异常
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 匿名用户无权访问资源的异常
                .accessDeniedHandler(new CustomAccessDeniedHandler()); // 认证用户复发访问资源的异常
        // 省略其他代码；
    }

    @Bean
    public UserDetailsService userDetailsService() {
        //获取登录用户信息
        return username -> {
            User user = userService.getUserByName(username);
            if (user != null) {
                UserRoleAndUser userRoleAndUser = userService.getUserRole(user);
                System.out.println(userRoleAndUser);
                return new CustomUserDetail(user, userRoleAndUser);
            }
            throw new UsernameNotFoundException("用户名或密码错误");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter(){
        return new JwtAuthenticationTokenFilter();
    }
}
