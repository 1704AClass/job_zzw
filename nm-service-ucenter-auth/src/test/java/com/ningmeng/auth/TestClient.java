package com.ningmeng.auth;

import com.netflix.discovery.converters.Auto;
import com.ningmeng.framework.client.NmServiceList;
import com.ningmeng.framework.domain.ucenter.ext.AuthToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;

    private String httpbasic(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }

    @Test
    public void testClient(){
        //动态从Eureka中获取的认证服务地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(NmServiceList.NM_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        String authUrl = uri+"/auth/oauth/token";
        //heard信息
        MultiValueMap<String,String> heards = new LinkedMultiValueMap();
        String httpbasicStr = httpbasic(clientId,clientSecret);
        heards.add("Authorization",httpbasicStr);
        //body信息
        MultiValueMap<String,String> body = new LinkedMultiValueMap();
        body.add("grant_type","password");
        body.add("username","ningmeng");
        body.add("password","1234");

        HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(body,heards);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });

        ResponseEntity<Map> responseEntity = restTemplate.exchange(authUrl, HttpMethod.POST,httpEntity, Map.class);
        System.out.println(responseEntity.getBody());
        //access_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Im5pbmdtZW5nIiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOm51bGwsInV0eXBlIjpudWxsLCJpZCI6bnVsbCwiZXhwIjoxNTgzODkyNDcxLCJqdGkiOiIzMDgyZTFlZS1hMzhhLTRkMjYtOWM5OC1hMGQ1OTBkYTljODciLCJjbGllbnRfaWQiOiJObVdlYkFwcCJ9.fXZCVL19aYzKlb8q12rCpCXrsrMi-gJPY1Sk3mAJ_P8WZPXDvomWSb6vAmVbNDHSbpMmjtrLcOVGEEBMUQ7YGoaAgVasM_5Xdpffn3pGNRExFaM7j8fOv7Q3NE2Lj65fV2o8WSQcyLcsUzPN-Vh4UasJCJscUe_FYVNiPeze9uVJklQdhIe_12WAFEmfUehUtrvPj7ft0qyAWX2jG9JgQP6OXf7a7_JcyW3m3VBOMgofwTsFlaUYg8zCV8TikOb3JJAzAbAQWN2BTbmJ6Mr1ZszhOzSyOF5fbbyYGnxEzVXq7oWNOg9gqmi0D_vvHDVVQhfwm-OyQKaGfeQhIjOh-g
        // , token_type=bearer
        // , refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Im5pbmdtZW5nIiwic2NvcGUiOlsiYXBwIl0sImF0aSI6IjMwODJlMWVlLWEzOGEtNGQyNi05Yzk4LWEwZDU5MGRhOWM4NyIsIm5hbWUiOm51bGwsInV0eXBlIjpudWxsLCJpZCI6bnVsbCwiZXhwIjoxNTgzODkyNDcxLCJqdGkiOiI4YTc5Y2IxYi1iM2UzLTQxMjctOTU0ZC05ZTA2MzI1YWNlZTAiLCJjbGllbnRfaWQiOiJObVdlYkFwcCJ9.OYx6liNMjjLhOtrkxif_Us8d2cyjn6quoHgU86m_fiTIWz6h4sl6ZP3yGyZI7L-asj3Ap6E5e__wJkRIs26MPlY04DYkZ7NFtVNnBBW0gVToiuhDJRe0sJn-gxVTnxLmUcgAwBF_2-X07BAiArsvRn70DkSkm-giOvBsT-YPt6ys4FwPY848A80UgPyC1Fzoz_zaC01jD1FR85x-o0vGD7PSijE6iohS91jaKqlg6PPdtg2rbtQoCVw9hD85dL5bHPk5UoiyA5qYw7azo8O2jg1Knk29HBqlKLSkhHY8NL-3t8pnEbLG-tKafVWCx7EOm8fIJ6TwvbngMjusZnCOag
        // , expires_in=43199
        // , scope=app
        // , jti=3082e1ee-a38a-4d26-9c98-a0d590da9c87
    }

}
