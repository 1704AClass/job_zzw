package com.ningmeng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.ningmeng.filesystem.dao.FilesystemRepository;
import com.ningmeng.framework.domain.filesystem.FileSystem;
import com.ningmeng.framework.domain.filesystem.response.FileSystemCode;
import com.ningmeng.framework.domain.filesystem.response.UploadFileResult;
import com.ningmeng.framework.exception.ExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FilesystemService {
    @Autowired
    private FilesystemRepository filesystemRepository;

    //yml
    @Value("${ningmeng.fastdfs.tracker_servers}")
    private String tracker_servers;
    @Value("${ningmeng.fastdfs.connect_timeout_in_seconds}")
    private String connect_timeout_in_seconds;
    @Value("${ningmeng.fastdfs.network_timeout_in_seconds}")
    private int network_timeout_in_seconds;
    @Value("${ningmeng.fastdfs.charset}")
    private String charset;

    public UploadFileResult upload(MultipartFile file,
                                   String filetag,
                                   String businesskey,
                                   String metadata){

       //上传fastdfs上，成功返回路径
       String  file_id= fdfs_upload(file);
       if (file_id == null){
           ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
       }
        //把成功的消息保存到文件系统数据库中
        FileSystem fileSystem = new FileSystem();
        //文件ID
        fileSystem.setFileId(file_id);
        //文件在文件系统中的路径
        fileSystem.setFilePath(file_id);
        //业务表示
        fileSystem.setBusinesskey(businesskey);
        //标签
        fileSystem.setFiletag(filetag);
        if(StringUtils.isNotEmpty(metadata)){
            try {
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
            e.printStackTrace();
            }
        }
        //名称
        fileSystem.setFileName(file.getOriginalFilename());
        //大小
        fileSystem.setFileSize(file.getSize());
        //文件类型
        fileSystem.setFileType(file.getContentType());

       filesystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }


    private void initFdfsConfig(){
            try {
                ClientGlobal.initByTrackers(tracker_servers);
                ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
                ClientGlobal.setG_charset(charset);
            }catch (Exception e){
                e.printStackTrace();
                //初始化文件系统出错

            }

    }

    //上传文件到fastDFS中
     public String fdfs_upload(MultipartFile file) {
        try {
            //初始化配置
            initFdfsConfig();
            //获得Tracker客户端
            TrackerClient trackerClient = new TrackerClient();
            //获得Tracker服务器地址
            TrackerServer trackerServer = trackerClient.getConnection();
            //得到storageServer的服务器
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient1 =new StorageClient1(trackerServer,storageServer);
            byte[] bytes = file.getBytes();

            String originalFilename = file.getOriginalFilename();
          String extName =  originalFilename.substring(originalFilename.lastIndexOf("."));
            //调用上传方法
           String file_id = storageClient1.upload_file1(bytes,extName,null);
            return file_id;
        }catch (Exception e){
            e.printStackTrace();
        }
        //失败返回空
        return null;
     }


}
