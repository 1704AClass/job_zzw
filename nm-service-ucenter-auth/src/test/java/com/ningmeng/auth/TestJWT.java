package com.ningmeng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJWT {

    //生成一个jwt令牌
    @Test
    public void testCreateJWT(){
        //证书文件
        String key_location = "nm.keystore";
        //密钥库密码
        String keystore_password ="ningmeng";
        //访问证书路径
        ClassPathResource resource = new ClassPathResource(key_location);
        //密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,
                keystore_password.toCharArray());
        //密钥的密码，此密码和别名要匹配
        String keypassword = "ningmeng";
        //密钥别名
        String alias = "nmkey";
        //密钥对（密钥和公钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypassword.toCharArray());
        //私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //定义payload信息(负载信息)
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "123");
        tokenMap.put("name", "ningmeng");
        tokenMap.put("roles", "anmin,user");
        //生成jwt令牌   根据秘钥生成JWT令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(aPrivate));
        //取出jwt令牌
        String token = jwt.getEncoded();
        System.out.println("token="+token);
        //token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6ImFubWluLHVzZXIiLCJuYW1lIjoibmluZ21lbmciLCJpZCI6IjEyMyJ9.HNjskqp0CcoZxMiYXRC8K4rcaiXxc9FmMcq0Ww3JSRE39MtaS-HdVI9FjcY0AlGdXy-aGoT_lCqdQaxW8H3l99A6pfLrL0qcwn9hvo4tUGnvxuU6pDfDP_6WQKXKUtc0LoBGPYczW5ZnQRDkX-Y6fD-TGC4p6NxhqEzBVpC1DNgh4CKvBipTJh8swnovYEJKO8FtaGZ93-wSxlJfTmcrDx03qVKBNKVfvqYbkca-RvLCmUcwFTc6EqqYUJ4Rr2vQ99VLqNXwBblnMSbFMtONNqqoOfBjMj_zBhWq_DtGIAYrzchYY14bbDHgm5lZraeLxsh6CgMMhKXQyyh2m9s8aQ
    }

    //资源服务使用公钥验证jwt的合法性，并对jwt解码
    @Test
    public void testVerify(){
        //jwt令牌
        String jwtToken="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6ImFubWluLHVzZXIiLCJuYW1lIjoibmluZ21lbmciLCJpZCI6IjEyMyJ9.HNjskqp0CcoZxMiYXRC8K4rcaiXxc9FmMcq0Ww3JSRE39MtaS-HdVI9FjcY0AlGdXy-aGoT_lCqdQaxW8H3l99A6pfLrL0qcwn9hvo4tUGnvxuU6pDfDP_6WQKXKUtc0LoBGPYczW5ZnQRDkX-Y6fD-TGC4p6NxhqEzBVpC1DNgh4CKvBipTJh8swnovYEJKO8FtaGZ93-wSxlJfTmcrDx03qVKBNKVfvqYbkca-RvLCmUcwFTc6EqqYUJ4Rr2vQ99VLqNXwBblnMSbFMtONNqqoOfBjMj_zBhWq_DtGIAYrzchYY14bbDHgm5lZraeLxsh6CgMMhKXQyyh2m9s8aQ";
        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0JWLscE2/Xz9OcQ9+H4LuP/ifrTdM7dZoga/t1xMH37GEdYOmwRLidiUYHkuTRTaWNgaTthtbyKsByVMOwTc+zpRf2nR9YAde8+ZNysk6gHjtfcEJ2qzx+Gr1SZMC27uuXKg1SktIzpvI5q+eBE+QUVtHG/nMfqEDPFtoyfasi6eSenWvw/MChc2wPEDTW/oTghzS99Jx5wfhUjf3Zf05VotyBjqOgywV6XlOpWjE/P4BV2NKj6TMs5+/gQJnoB9FmGRt7FPr7kBBHRq8YJXaOjOalvGZ9xPaL8F5uKZ571z7fgqCBLhzeHA5B+tYOdedEGx9Y47qYKyW7v+gh/+RQIDAQAB-----END PUBLIC KEY-----";
        //校验jwt
        Jwt jwt = JwtHelper.decodeAndVerify(jwtToken, new RsaVerifier(publickey));
        //获取jwt原始内容
        String claims = jwt.getClaims();
        //jwt令牌
        String encoded = jwt.getEncoded();
        //encode=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6ImFubWluLHVzZXIiLCJuYW1lIjoibmluZ21lbmciLCJpZCI6IjEyMyJ9.HNjskqp0CcoZxMiYXRC8K4rcaiXxc9FmMcq0Ww3JSRE39MtaS-HdVI9FjcY0AlGdXy-aGoT_lCqdQaxW8H3l99A6pfLrL0qcwn9hvo4tUGnvxuU6pDfDP_6WQKXKUtc0LoBGPYczW5ZnQRDkX-Y6fD-TGC4p6NxhqEzBVpC1DNgh4CKvBipTJh8swnovYEJKO8FtaGZ93-wSxlJfTmcrDx03qVKBNKVfvqYbkca-RvLCmUcwFTc6EqqYUJ4Rr2vQ99VLqNXwBblnMSbFMtONNqqoOfBjMj_zBhWq_DtGIAYrzchYY14bbDHgm5lZraeLxsh6CgMMhKXQyyh2m9s8aQ
        System.out.println("encode="+encoded);
    }
}
