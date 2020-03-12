package com.ningmeng.goven.gateway;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TestZuul extends ZuulFilter {
     /*private static final Logger LOG = LoggerFactory.getLogger(Login.class);*/

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;//int值来定义过滤器的执行顺序，数值越小优先级越高
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        HttpServletRequest request = requestContext.getRequest();
        //取出头部信息Authorization
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            requestContext.setSendZuulResponse(false);// 拒绝访问
            //requestContext.setResponseStatusCode(200);// 设置响应状态码
            // ResponseResult unauthenticated = new ResponseResult(CommonCode.UNAUTHENTICATED);
            // String jsonString = JSON.toJSONString(unauthenticated);
            // requestContext.setResponseBody(jsonString);
            // requestContext.getResponse().setContentType("application/json;charset=UTF‐8");
            // return null;
        }
        return null;
    }
}
