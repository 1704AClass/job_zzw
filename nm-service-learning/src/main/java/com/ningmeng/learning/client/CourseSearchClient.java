package com.ningmeng.learning.client;

import com.ningmeng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by 1 on 2020/3/7.
 */
@FeignClient(value = "nm-service-search")
public interface CourseSearchClient {

    @GetMapping(value = "/search/getmedia/{teachplanId}")
    public TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId);
}
