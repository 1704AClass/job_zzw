package com.ningmeng.manage_course.dao;

import com.ningmeng.framework.domain.course.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeachplanMapper {
    public TeachplanNode selectList(String courseId);
}
