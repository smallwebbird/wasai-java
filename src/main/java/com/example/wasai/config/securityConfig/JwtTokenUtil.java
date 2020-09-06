package com.example.wasai.config.securityConfig;


import cn.hutool.core.lang.UUID;
import com.example.wasai.config.redisConfig.RedisService;
import com.example.wasai.utils.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {
//    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final String CLAIM_KEY_USERNAME = "sub";
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.issuer}")
    private String issuer;


    /**
     * 根据负责生成JWT的token
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setHeader(generateJwtHeader())
                .setExpiration(generateExpirationDate())
                .setIssuedAt(new Date())
                .setIssuer(issuer)
                .setId(getUUID())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private Map generateJwtHeader() {
        Map<String, String> map = new HashMap<>();
        map.put("typ", "JWT");
        map.put("alg", "HS512");
        return map;
    }

    /**
     * 从token中获取JWT中的负载
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
//            LOGGER.info("JWT格式验证失败:{}",token);
        }
        return claims;
    }

    /**
     * 获取一个随机唯一的字符串当做jwtId
     */

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成token的过期时间
     */
    public Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 从token中获取登录用户名
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username =  claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 获取创建token中的创建时间
     *
     */
    public Date getCreatedFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getIssuedAt();
    }

    public String getIssuerFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getIssuer();
    }

    /**
     * 判断token是否已经失效
     */
    public boolean isTokenExpired(String expirationTime) {
        String currentTime = DateUtil.getTime();
        if (DateUtil.equalTime(currentTime, expirationTime)) {
            // 当前时间比过期时间大，过期
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从token中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 获取唯一的jwtId
     */
    public String getJWTIDFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getId();
    }

    /**
     * 根据用户信息生成token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        // 把最后登录时间放在jwtToken中
        return generateToken(claims);
    }


    /**
     * 刷新token
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return generateToken(claims);
    }
}
