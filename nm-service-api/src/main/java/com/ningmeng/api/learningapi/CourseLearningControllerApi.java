package com.ningmeng.api.learningapi;

import com.ningmeng.framework.domain.learning.response.GetMediaResult;
import io.swagger.annotations.Api;

/**
 * Created by 1 on 2020/3/7.
 */
@Api(value = "录播课程学习管理",description = "录播课程学习管理")
public interface CourseLearningControllerApi {

    //@ApiOperation("获取课程学习地址")
    public GetMediaResult getmedia(String courseId, String teachplanId);
}
