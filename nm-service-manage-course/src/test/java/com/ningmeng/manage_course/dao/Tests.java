package com.ningmeng.manage_course.dao;

import com.ningmeng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Tests {

    @Autowired
    private RestTemplate restTemplate;

    //负载均衡调用
    @Test
    public void testRibbon() {
        //服务id
        String serviceId = "NM-SERVICE-MANAGE-CMS";
        //页面id
        String id = "5a96114fb00ffc4b44f63e06";
        for(int i=0;i<5;i++){
            //通过服务id调用 http://localhost:31001/cms/findById/{id}?id=5a96114fb00ffc4b44f63e06
            ResponseEntity<CmsPage> forEntity = restTemplate.getForEntity("http://" + serviceId
                    + "/cms/findById/5a96114fb00ffc4b44f63e06", CmsPage.class);
            CmsPage cmsPage = forEntity.getBody();
            System.out.println("-----------------------:"+cmsPage);
        }
    }
}
