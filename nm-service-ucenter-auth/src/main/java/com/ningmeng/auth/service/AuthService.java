package com.ningmeng.auth.service;

import com.alibaba.fastjson.JSON;
import com.ningmeng.framework.client.NmServiceList;
import com.ningmeng.framework.domain.ucenter.ext.AuthToken;
import com.ningmeng.framework.domain.ucenter.response.AuthCode;
import com.ningmeng.framework.exception.CustomExceptionCast;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;//token存储到redis的过期时间

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public AuthToken login(String username,String password,String clientId,String clientSecret){
        //1、申请令牌
        AuthToken authToken = applyToken(username,password,clientId, clientSecret);
        if(authToken==null){
            CustomExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
        }
        //2.将令牌保存到redis中
        String content = JSON.toJSONString(authToken);
        boolean flag = this.saveToken(authToken.getAccess_token(),content,tokenValiditySeconds);
        if(!flag){
            CustomExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
        }
        return authToken;
    }
    //存储令牌到redis
    private boolean saveToken(String access_token,String content,long ttl){
        //令牌名称
        String name = "user_token:" + access_token;
        //保存到令牌到redis
        stringRedisTemplate.boundValueOps(name).set(content,ttl, TimeUnit.SECONDS);
        //获取过期时间
        Long expire = stringRedisTemplate.getExpire(name);
        return expire>0;
    }
    //认证方法
    private AuthToken applyToken(String username,String password,String clientId,String clientSecret){
        //动态从Eureka中获取的认证服务地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(NmServiceList.NM_SERVICE_UCENTER_AUTH);
        if(serviceInstance == null){
            LOGGER.error("choose an auth instance fail");
            CustomExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
        }
        //获取令牌的url
        String authUrl = serviceInstance.getUri().toString()+"/auth/oauth/token";
        //定义body
        MultiValueMap<String,String> body = new LinkedMultiValueMap();
        //授权方式
        body.add("grant_type","password");
        //账号
        body.add("username","ningmeng");
        //密码
        body.add("password","1234");
        //定义头
        //heard信息
        MultiValueMap<String,String> heards = new LinkedMultiValueMap();
        String httpbasicStr = httpbasic(clientId,clientSecret);
        heards.add("Authorization",httpbasicStr);

        HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(body,heards);
        //指定restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        //http请求spring security的申请令牌接口
        ResponseEntity<Map> responseEntity = restTemplate.exchange(authUrl, HttpMethod.POST,httpEntity, Map.class);
        Map body1 = responseEntity.getBody();
        if(body1==null ||
                body1.get("access_token") == null ||
                body1.get("refresh_token") == null ||
                body1.get("jti") == null){//jti是jwt令牌的唯一标识作为用户身份令牌

            String error_description=(String)body1.get("error_description");

            if(StringUtils.isNotEmpty(error_description)){
                if("坏的凭证".equals(error_description)){
                    CustomExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                }else if(error_description.indexOf("UserDetailsService returned null")>=0){
                    CustomExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                }
            }
            CustomExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }

        AuthToken authToken = new AuthToken();
        //访问令牌（jwt)
        String jwt_token=(String)body1.get("access_token");
        //刷新令牌(jwt)
        String refresh_token = (String) body1.get("refresh_token");
        //jti，作为用户的身份标识
        String access_token = (String) body1.get("jti");
        authToken.setJwt_token(jwt_token);
        authToken.setAccess_token(access_token);
        authToken.setRefresh_token(refresh_token);
        return authToken;
    }

    //获取httpbasic认证串
    private String httpbasic(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }
}
