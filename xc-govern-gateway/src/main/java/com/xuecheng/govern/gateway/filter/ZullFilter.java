package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ZullFilter extends ZuulFilter {

    @Autowired
    AuthService authService;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() { //过滤器是否执行
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        //得到response
        HttpServletResponse response = currentContext.getResponse();
        //获取身份令牌
        String access_token = authService.getTokenFromCookie(request);
        if (StringUtils.isEmpty(access_token)) {
            access_denied(); //拒绝访问
            return null;
        }
        //从cookie中查询令牌信息
        String jwtFromHeader = authService.getJwtFromHeader(request);
        if (StringUtils.isEmpty(jwtFromHeader)) {
            access_denied();
            return null;
        }

        //查询redis中的过期时间
        long expire = authService.getExpire(access_token);
        if (expire < 0) {
            access_denied ();
            return null;
        }
           return null;
    }
    //拒绝访问
    private void access_denied() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取response
        HttpServletResponse response = currentContext.getResponse();
        currentContext.setSendZuulResponse(false);//拒绝访问
        currentContext.setResponseStatusCode(200);//设置响应状态码
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String jsonString = JSON.toJSONString(responseResult);
        currentContext.setResponseBody(jsonString);
        response.setContentType("application/json;charset=utf-8");
    }

    }
