package com.ningmeng.framework.domain.cms.request;

import com.ningmeng.framework.model.request.RequestData;
import lombok.Data;

/**
 * Created by Lenovo on 2020/2/11.
 * 页面查询，自己封装的类
 */
@Data
public class QueryPageRequest extends RequestData{
    //站点id
    private String siteId;
    //页面id
    private String pageId;
    //页面名称
    private String pageName;
    //别名
    private String pageAliase;
    //模板id
    private String templateId;

}
