package com.ningmeng.manage_course.dao;

import com.github.pagehelper.Page;
import com.ningmeng.framework.domain.course.CourseBase;
import com.ningmeng.framework.domain.course.ext.CourseInfo;
import com.ningmeng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Administrator.
 */
@Mapper
public interface CourseMapper {
   CourseBase findCourseBaseById(String id);

   Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);
}
