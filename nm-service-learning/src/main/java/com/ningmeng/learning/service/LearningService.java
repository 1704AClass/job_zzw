package com.ningmeng.learning.service;

import com.ningmeng.framework.domain.course.TeachplanMediaPub;
import com.ningmeng.framework.domain.learning.response.GetMediaResult;
import com.ningmeng.framework.exception.ExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.learning.client.CourseSearchClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 1 on 2020/3/7.
 */
@Service
public class LearningService {

    @Autowired
    CourseSearchClient courseSearchClient;

    //获取课程
    public GetMediaResult getmedia(String courseId,String teachplanId) {
        //校验学生的学习权限。。是否资费等
        //调用搜索服务查询
        TeachplanMediaPub teachplanMediaPub = courseSearchClient.getmedia(teachplanId);
        if (teachplanMediaPub == null || StringUtils.isEmpty(teachplanMediaPub.getMediaUrl())) {
            //获取视频播放地址出错
            ExceptionCast.cast(CommonCode.FAIL);
        }
        return new GetMediaResult(CommonCode.SUCCESS, teachplanMediaPub.getMediaUrl());
    }
}
