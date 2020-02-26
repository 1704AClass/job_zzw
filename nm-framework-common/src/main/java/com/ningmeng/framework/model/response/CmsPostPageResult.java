package com.ningmeng.framework.model.response;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {

    String pageUrl;

    public CmsPostPageResult(ResultCode resultCode,String pageUrl){
        super(resultCode);
        this.pageUrl=pageUrl;
    }

}
