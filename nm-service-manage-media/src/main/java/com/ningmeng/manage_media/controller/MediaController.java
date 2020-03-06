package com.ningmeng.manage_media.controller;

import com.ningmeng.api.mediaapi.MediaUploadControllerApi;
import com.ningmeng.framework.domain.media.response.CheckChunkResult;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.manage_media.service.MediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media/upload")
public class MediaController implements MediaUploadControllerApi{

    @Autowired
    MediaUploadService mediaUploadService;

    @Override
    //文件上传注册
    @PostMapping("/register")
    public ResponseResult register(@RequestParam("fileMd5") String fileMd5,@RequestParam("fileName") String fileName,@RequestParam("fileSize") Long fileSize,@RequestParam("mimetype") String mimetype,@RequestParam("fileExt") String fileExt) {
        return mediaUploadService.register(fileMd5,fileName,fileSize,mimetype,fileExt);
    }

    @Override
    //分块检查
    @PostMapping("/checkchunk")
    public CheckChunkResult checkchunk(@RequestParam("fileMd5") String fileMd5,@RequestParam("chunk") Integer chunk,@RequestParam("chunkSize") Integer chunkSize) {
        return mediaUploadService.checkchunk(fileMd5,chunk,chunkSize);
    }

    @Override
    //上传分块
    @PostMapping("/uploadchunk")
    public ResponseResult uploadchunk(@RequestParam("file") MultipartFile file,@RequestParam("chunk") Integer chunk,@RequestParam("fileMd5") String fileMd5) {
        return mediaUploadService.uploadchunk(file,fileMd5,chunk);
    }

    @Override
    //合并分块
    @PostMapping("/mergechunks")
    public ResponseResult mergechunks(@RequestParam("fileMd5")String fileMd5,@RequestParam("fileName") String fileName,@RequestParam("fileSize") Long fileSize,@RequestParam("mimetype") String mimetype,@RequestParam("fileExt") String fileExt) {
        return mediaUploadService.mergechunks(fileMd5,fileName,fileSize,mimetype,fileExt);
    }
}
