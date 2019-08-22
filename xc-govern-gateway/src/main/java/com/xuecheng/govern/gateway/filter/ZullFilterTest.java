package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ZullFilterTest extends ZuulFilter {
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
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = currentContext.getRequest();
        //获取response
        HttpServletResponse response = currentContext.getResponse();
        //取出头部信息Authorization
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            currentContext.setSendZuulResponse(false);//拒绝访问
            currentContext.setResponseStatusCode(200);//设置响应状态码
            ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
            String jsonString = JSON.toJSONString(responseResult);
            currentContext.setResponseBody(jsonString);
            response.setContentType("application/json;charset=utf-8");
            return null;
        }
        return null;
    }
}
