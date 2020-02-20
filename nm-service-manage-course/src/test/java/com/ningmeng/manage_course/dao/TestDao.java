package com.ningmeng.manage_course.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ningmeng.framework.domain.course.CourseBase;
import com.ningmeng.framework.domain.course.ext.CourseInfo;
import com.ningmeng.framework.domain.course.request.CourseListRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDao {
    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMapper courseMapper;

    @Test
    public void testCourseBaseRepository(){
        Optional<CourseBase> optional = courseBaseRepository.findById("402885816240d276016240f7e5000002");
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }

    }

    @Test
    public void testCourseMapper(){
        CourseBase courseBase = courseMapper.findCourseBaseById("402885816240d276016240f7e5000002");
        System.out.println(courseBase);
    }

    //测试分页
    //测试分页
    @Test
    public void testPageHelper(){
        PageHelper.startPage(1, 10);//查询第一页，每页显示10条记录
        CourseListRequest courseListRequest = new CourseListRequest();
        String companyId="1";
        Page<CourseInfo> courseListPage = (Page<CourseInfo>) courseMapper.findCourseListPage(companyId);
        List<CourseInfo> result = courseListPage.getResult();
        System.out.println(courseListPage);
        System.out.println(result);
    }
}