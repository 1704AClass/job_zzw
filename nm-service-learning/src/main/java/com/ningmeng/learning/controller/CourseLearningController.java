package com.ningmeng.learning.controller;

import com.ningmeng.api.learningapi.CourseLearningControllerApi;
import com.ningmeng.framework.domain.learning.response.GetMediaResult;
import com.ningmeng.learning.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 1 on 2020/3/7.
 */
@RestController
@RequestMapping("/learning/course")
public class CourseLearningController implements CourseLearningControllerApi{
    @Autowired
    LearningService learningService;

    @GetMapping("/getmeia/{courseId}/{teachplanId}")
    @Override
    public GetMediaResult getmedia(@PathVariable("courseId") String courseId,@PathVariable("teachplanId") String teachplanId) {
        return learningService.getmedia(courseId,teachplanId);
    }
}
