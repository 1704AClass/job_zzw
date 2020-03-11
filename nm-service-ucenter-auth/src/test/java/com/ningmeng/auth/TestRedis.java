package com.ningmeng.auth;

import com.netflix.discovery.converters.Auto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testTedis(){
        //redisTemplate.boundValueOps("k1").set("v1");
        //System.out.println(redisTemplate.boundValueOps("k1").get());
        stringRedisTemplate.boundValueOps("k3").set("v3",60, TimeUnit.SECONDS);
        Long k3 = stringRedisTemplate.getExpire("k3");
        System.out.println(stringRedisTemplate.boundValueOps("k3").get()+":"+k3);
    }

}
