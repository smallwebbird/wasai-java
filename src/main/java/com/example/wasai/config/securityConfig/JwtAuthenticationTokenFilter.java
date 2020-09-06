package com.example.wasai.config.securityConfig;

import cn.hutool.json.JSONUtil;
import com.example.wasai.common.api.CommonResult;
import com.example.wasai.config.redisConfig.RedisService;
import com.example.wasai.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
//    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisService redisService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.validTime}")
    private Long validTime;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader(tokenHeader);
        log.debug(authHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            String authToken = authHeader.substring(tokenHead.length());
            String name = jwtTokenUtil.getUserNameFromToken(authToken);
            String redisUUID = "";
            log.error(name);
            // 判断redis是否已经保存
            if (redisService.hasKey(name)) {
                String expirationTime = (String) redisService.hget(name, "jwtExpirationTime");
                if (jwtTokenUtil.isTokenExpired(expirationTime)) {
                    // 已经过期
                    String redisValidTime = (String) redisService.hget(name, "jwtValidTime");
                    String currentTime = DateUtil.getTime();
                    // 判断是否超过有效时间
                    if (DateUtil.equalTime(currentTime, redisValidTime)) {
                        log.debug("token存在的时间已经超出有效时间");
                        return;
                    } else {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(name);
                        String jwtToken = jwtTokenUtil.generateToken(userDetails);
                        // 删除旧的
                        redisService.delete(name);
                        Long expireTime = System.currentTimeMillis() + expiration;
                        String jwtId = jwtTokenUtil.getJWTIDFromToken(jwtToken);
                        redisService.setTokenRefresh(name, jwtToken, expireTime, validTime, jwtId);
                        authToken = jwtToken;
                        httpServletResponse.setHeader(tokenHeader, tokenHead + " " + jwtToken);
                    }
                }
            }
//            LOGGER.info("checking username:{}", name);
            if (name != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(name);
                redisUUID = (String) redisService.hget(name, "jwtId");
                String currentUUID = jwtTokenUtil.getJWTIDFromToken(authToken);
                log.debug("----------------------------");
                log.debug(currentUUID);
                log.debug(redisUUID);
                log.debug("----------------------------");
                if (!currentUUID.equals(redisUUID)) {
                    // 不等于就说明，俩个用户登录同一个账号
                    httpServletResponse.getWriter().write(JSONUtil.parseObj(CommonResult.unauthorized("禁止访问")).toStringPretty());
                    return;
                }
                //这个判断只是判断token中的username是否和当前用户一致，以及token是否过期
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
