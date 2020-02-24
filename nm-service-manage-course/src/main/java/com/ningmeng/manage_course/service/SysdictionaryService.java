package com.ningmeng.manage_course.service;

import com.ningmeng.framework.domain.system.SysDictionary;
import com.ningmeng.manage_course.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysdictionaryService {

    @Autowired
    SysDictionaryRepository sysDictionaryRepository;

    //根据字典分类type查询字典信息
    public SysDictionary findDictionaryByType(String type){
        return sysDictionaryRepository.findByDType(type);
    }
}
