package com.ningmeng.manage_course.controller;

import com.ningmeng.api.courseapi.CourseControllerApi;
import com.ningmeng.framework.domain.course.*;
import com.ningmeng.framework.domain.course.request.CourseListRequest;
import com.ningmeng.framework.domain.course.response.CategoryNode;
import com.ningmeng.framework.model.response.QueryResponseResult;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    CourseService courseService;

    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page,
                                              @PathVariable("size") int size,
                                              CourseListRequest courseListRequest) {
        //先使用静态数据测试
        String companyId = "1";
        return courseService.findCourseList(companyId,page,size,courseListRequest);
    }

    //查询课程计划
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(String courseId) {
        return courseService.findTeachplanList(courseId);
    }

    //添加课程计划
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }

    @Override
    @GetMapping("/course/findCourseList/{page}/{size}")
    public QueryResponseResult findCourseList(int page, int size, String id) {
        /*return courseService.findCourseList(page,size,id);*/
        return null;
    }

    @Override
    @GetMapping("/findList")
    public CategoryNode findList() {
        return null;
    }

    @Override
    public CourseMarket getCourseMarketById(String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    @Override
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        return courseService.updateCourseMarket(id,courseMarket);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseView(String id) {
        return courseService.getCoruseView(id);
    }

    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(String id) {
        return courseService.preview(id);
    }

    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable("id") String id) {
        return courseService.publish(id);
    }

    @Override
    @PostMapping("/savemedia")
    public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.savemedia(teachplanMedia);
    }

}
