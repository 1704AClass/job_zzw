package com.ningmeng.api.filesystem;

import com.ningmeng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "文件管理接口",description = "提供文件的常规操作")
public interface FileSystemControllerApi {
    @ApiOperation("上传操作")
    public UploadFileResult upload(MultipartFile multipartFile,
                                   String filetag,
                                   String businesskey,
                                   String metadata);
}
