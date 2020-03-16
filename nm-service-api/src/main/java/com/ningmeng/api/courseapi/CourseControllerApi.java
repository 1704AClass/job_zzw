package com.ningmeng.api.courseapi;

import com.ningmeng.framework.domain.course.*;
import com.ningmeng.framework.domain.course.request.CourseListRequest;
import com.ningmeng.framework.domain.course.response.CategoryNode;
import com.ningmeng.framework.model.response.QueryResponseResult;
import com.ningmeng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 课程发布
 */
@Api(value="课程管理页面",description = "课程管理接口，提供课程管理的增、删、改、查")
public interface CourseControllerApi {

    @ApiOperation("课程查询")
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseid);

    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(Teachplan teachplan);

    @ApiOperation("查询我的课程列表")
    public QueryResponseResult findCourseList(int page, int size, String id);

    @ApiOperation("查询分类")
    public CategoryNode findList();

    @ApiOperation("获取课程营销信息")
    public CourseMarket getCourseMarketById(String courseId);

    @ApiOperation("更新课程营销信息")
    public ResponseResult updateCourseMarket(String id,CourseMarket courseMarket);

    @ApiOperation("课程视图查询")
    public CourseView courseView(String id);

    @ApiOperation("预览课程")
    public CoursePublishResult preview(String id);

    @ApiOperation("发布课程")
    public CoursePublishResult publish(@PathVariable String id);

    @ApiOperation("保存媒资信息")
    public ResponseResult savemedia(TeachplanMedia teachplanMedia);
}
