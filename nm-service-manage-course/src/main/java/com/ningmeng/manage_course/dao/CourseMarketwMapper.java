package com.ningmeng.manage_course.dao;

import com.ningmeng.framework.domain.course.CourseMarket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CourseMarketwMapper {

    @Select("select * from course_market where id=#{courseId}")
    CourseMarket getCourseMarketById(String courseId);

    @Update("update course_market set charge=#{charge},valid=#{valid},expires=#{expires},qq=#{qq},price=#{price},price_old=#{priceOld},start_time=#{startTime},end_time=#{endTime} where id = #{id}")
    void updateCourseMarket(String id, CourseMarket courseMarket);
}
