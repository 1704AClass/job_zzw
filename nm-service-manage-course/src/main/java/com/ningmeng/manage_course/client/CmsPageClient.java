package com.ningmeng.manage_course.client;

import com.ningmeng.framework.client.NmServiceList;
import com.ningmeng.framework.domain.cms.CmsPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = NmServiceList.NM_SERVICE_MANAGE_CMS)
public interface CmsPageClient {

    @GetMapping("/cms/findById/{id}")
    public CmsPage findById(@PathVariable("id") String id);
}
