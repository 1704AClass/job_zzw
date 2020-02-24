package com.ningmeng.manage_course.controller;

import com.ningmeng.api.courseapi.SysDicthinaryControllerApi;
import com.ningmeng.framework.domain.system.SysDictionary;
import com.ningmeng.manage_course.service.SysdictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/dictionary")
public class SysDictionaryController implements SysDicthinaryControllerApi {

    @Autowired
    SysdictionaryService sysdictionaryService;

    //根据字典分类id查询字典信息
    @GetMapping("/get/{type}")
    @Override
    public SysDictionary findDictionaryByType(String type) {
        return sysdictionaryService.findDictionaryByType(type);
    }
}
