package com.ningmeng.framework.domain.course.response;

import com.ningmeng.framework.domain.course.Category;

import java.util.List;

public class CategoryNode extends Category {
    List<CategoryNode> children;
}
