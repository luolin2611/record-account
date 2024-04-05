package cn.rollin.utils;

import cn.rollin.enums.JWTSubjectEnum;
import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具类
 *
 * @author rollin
 * @since 2022-10-07 11:10:00
 */
@Slf4j
@Component
public class JWTUtil {

    /**
     * jwt 加密密钥
     */
    @Value("${security.jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${security.aes.secretKey}")
    private String aesSecretKey;

    /**
     * jwt 默认过期时间(单位：毫秒)
     */
    @Value("${security.jwt.expiration}")
    private Long defaultExpiration;

    /**
     * 令牌前缀
     */
    @Value("${security.jwt.prefix}")
    private String tokenPrefix;

    /**
     * 生成默认的jwt字符串
     *
     * @param object 载荷部分对象
     * @return jwt 字符串
     */
    public String generateDefaultJwt(Object object) {
        return generateJwt(JWTSubjectEnum.DEFAULT_SUBJECT, System.currentTimeMillis() + defaultExpiration, object);
    }

    /**
     * 生成jwt字符串
     *
     * @param subjectEnum 主题枚举
     * @param expiration  jwt过期时间
     * @param t           载荷部分对象
     * @return jwt 字符串
     */
    public <T> String generateJwt(JWTSubjectEnum subjectEnum, Long expiration, T t) {
        String token = Jwts.builder()
                .setSubject(subjectEnum.getSubject())
                .claim("object", t)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();

        return tokenPrefix + AESUtils.encrypt(token, aesSecretKey);
    }

    /**
     * 解析jwt字符串
     *
     * @param jwtStr jwt字符串
     * @param tClass 转换对象类型
     * @return jwt对象
     */
    public <T> T parseJwt(String jwtStr, Class<T> tClass) {
        try {
            jwtStr = AESUtils.decrypt(jwtStr, aesSecretKey);
            Claims body = Jwts.parser()
                    .setSigningKey(jwtSecretKey)
                    .parseClaimsJws(jwtStr.replace(tokenPrefix, ""))
                    .getBody();
            return JSON.parseObject(JSON.toJSONString(body.get("object")), tClass);
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("Jwt expired.");
        } catch (Exception e) {
            log.error("Jwt decryption failed.");
        }
        return null;
    }
}
