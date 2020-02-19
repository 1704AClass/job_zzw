package com.ningmeng.framework.domain.course;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class TeachplanNode extends  Teachplan {
    List<TeachplanNode> children;
}
