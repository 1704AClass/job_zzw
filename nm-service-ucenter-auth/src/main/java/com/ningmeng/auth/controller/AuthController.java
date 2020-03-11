package com.ningmeng.auth.controller;

import com.ningmeng.api.auth.AuthControllerApi;
import com.ningmeng.auth.service.AuthService;
import com.ningmeng.framework.domain.ucenter.ext.AuthToken;
import com.ningmeng.framework.domain.ucenter.request.LoginRequest;
import com.ningmeng.framework.domain.ucenter.response.AuthCode;
import com.ningmeng.framework.domain.ucenter.response.LoginResult;
import com.ningmeng.framework.exception.ExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

public class AuthController implements AuthControllerApi {

    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;

    @Autowired
    private AuthService authService;

    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {

        if(loginRequest==null){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_ERROR);
        }
        //校验账号是否输入
        if("".equals(loginRequest.getUsername())){
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        //校验密码是否输入
        if("".equals(loginRequest.getPassword())){
            ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
        }
        //如果登录成功    保存cookie
        AuthToken authToken = authService.login(loginRequest.getUsername(),loginRequest.getPassword(),clientId,clientSecret);
        String  access_token = authToken.getAccess_token();
        //将访问令牌存储到cookie
        this.saveCookie(access_token);
        return new LoginResult(CommonCode.SUCCESS,access_token);
    }

    private void saveCookie(String token){
        //添加cookie认证令牌，最后一个参数设置为false,表示允许浏览器获取
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);
    }

    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        return null;
    }
}
