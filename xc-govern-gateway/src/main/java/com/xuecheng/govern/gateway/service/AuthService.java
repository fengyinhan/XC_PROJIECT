package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

     //查询身份令牌
    public String getTokenFromCookie(HttpServletRequest request){
        Map<String, String> cookie = CookieUtil.readCookie(request, "uid");
        String access_token = cookie.get("uid");
        if(StringUtils.isEmpty(access_token)){
            return null;
        }
        return access_token;
    }
     //从header中获取Jwt令牌
     public String getJwtFromHeader(HttpServletRequest request){
         String authorization = request.getHeader("Authorization");
         if(StringUtils.isEmpty(authorization)){
             //拒绝访问
             return null;
         }
         if(!authorization.startsWith("Bearer ")){
             //拒绝访问
             return null;
         }
         //取到jwt令牌
         String jwt = authorization.substring(7);
         return jwt;
     }
     //从redis中查询过期时间
    public long getExpire(String access_token){
        //定义key
        //key
        String key = "user_token:"+access_token;
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire;
    }


}
