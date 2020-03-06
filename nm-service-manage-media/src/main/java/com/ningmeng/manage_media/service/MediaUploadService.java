package com.ningmeng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.ningmeng.framework.domain.media.MediaFile;
import com.ningmeng.framework.domain.media.response.CheckChunkResult;
import com.ningmeng.framework.domain.media.response.MediaCode;
import com.ningmeng.framework.exception.ExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.manage_media.config.RabbitMQConfig;
import com.ningmeng.manage_media.controller.MediaController;
import com.ningmeng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class MediaUploadService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MediaController.class);

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Value("${nm-service-manage-media.upload-location}")
    String uploadPath;

    @Value("${nm-service-manage-media.mq.routingkey-media-video}")
    String routingkey_media_video;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：MD5的第一个字符
     * 二级目录：MD5的第二个字符
     * 三级目录：MD5
     * 文件名：MD5+文件扩展名
     * @param fileMd5 文件MD5值
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    private String getFilePath(String fileMd5,String fileExt){
        String filePath = uploadPath+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+"."+fileExt;
        return filePath;
    }
    //得到文件目录相对路径 路径中去掉根目录
    private String getFileFolderRelativePath(String fileMd5,String fileExt){
        String filePath = fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/";
        return filePath;
    }
    //得到文件所在目录
    private String getFileFoloderPath(String fileMd5){
        String fileFolderPath = uploadPath+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/";
        return fileFolderPath;
    }
    //创建文件目录
    private boolean createFileFold(String fileMd5){
        //创建上传文件目录
        String fileFolderPath = getFileFoloderPath(fileMd5);
        File fileFolder = new File(fileFolderPath);
        if(!fileFolder.exists()){
            boolean mkdirs = fileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }
    //文件上传注册
    public ResponseResult register(String fileMd5,String fileName,Long fileSize,String mimetype,String fileExt){
        //检查文件是否上传
        //1.得到文件的路径
        String filePath = getFilePath(fileMd5,fileExt);
        File file = new File(filePath);
        //2.查询数据库文件是否存在
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        //文件存在直接返回
        if(file.exists() && optional.isPresent()){
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        boolean fileFold = createFileFold(fileMd5);
        if(!fileFold){
            //上传文件目录创建失败
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //得到块文件所在目录
    private String getChunkFileFolderPath(String fileMd5){
        String fileChunkFolderPath = getFileFoloderPath(fileMd5)+"/"+"chunks"+"/";
        return fileChunkFolderPath;
    }
    //检查块文件是否存在
    public CheckChunkResult checkchunk(String fileMd5,Integer chunk,Integer chunkSize){
        //得到块文件所在路径
        String chunkfileFolderPath = getChunkFileFolderPath(fileMd5);
        //块文件的文件名称以1,2,3序号命名，没有扩展名
        File chunkFile = new File(chunkfileFolderPath+chunk);
        if(chunkFile.exists()){
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK,true);
        }
        return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK,false);
    }

    //创建块文件目录
    private boolean createChunkFileFolder(String fileMd5){
        //创建上传文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        if(!chunkFileFolder.exists()){
            //创建文件夹
            boolean mkdirs = chunkFileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }

    //块文件上传
    public ResponseResult uploadchunk(MultipartFile file,String fileMd5,Integer chunk){
        if(file == null){
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        }
        //创建块文件目录
        boolean fileFold = createChunkFileFolder(fileMd5);
        //块文件
        File chunkfile = new File(getChunkFileFolderPath(fileMd5)+chunk);
        //长传的块文件
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try{
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(chunkfile);
            IOUtils.copy(inputStream,outputStream);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("upload chunk file fail:{}",e.getMessage());
            ExceptionCast.cast(MediaCode.CHUNK_FILE_EXIST_CHECK);
        }
        try{
            inputStream.close();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取所有块文件  合并分块所要调用的方法  块文件排序后的list
    private List<File> getChunkFiles(File chunkfileFolder){
        //获取路径下的所有块问价
        File[] chunkFiles = chunkfileFolder.listFiles();
        //将文件数组转成list，并排序
        List<File> chunkFileList = new ArrayList<File>();
        chunkFileList.addAll(Arrays.asList(chunkFiles));
        //排序
        Collections.sort(chunkFileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });
        return chunkFileList;
    }

    //合并文件  合并分块时候调用的其中合并文件的方法
    private File mergeFile(File mergeFile,List<File> chunkFiles){
        try{
            //创建写文件对象
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile,"rw");
            //遍历分块文件开始合并
            //读取文件缓冲区
            byte[] b = new byte[1024];
            for(File chunkFile:chunkFiles){
                RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"r");
                int len = -1;
                //读取分块文件
                while((len = raf_read.read(b)) != -1){
                    //向合并文件中写数据
                    raf_write.write(b,0,len);
                }
                raf_read.close();
            }
            raf_write.close();
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("merge file error:{}",e.getMessage());
            return null;
        }
        return mergeFile;
    }
    //检验文件的MD5值
    private boolean checkFileMd5(File mergeFile,String md5){
        if(mergeFile == null || StringUtils.isEmpty(md5)){
            return false;
        }
        //进行MD5校验
        FileInputStream mergeFileInputstream = null;
        try{
            mergeFileInputstream = new FileInputStream(mergeFile);
            //得到文件的MD5
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileInputstream);
            //比较md5
            if(md5.equalsIgnoreCase(mergeFileMd5)){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("checkFileMd5 error,file is:{},md5 is:{}",mergeFile.getAbsoluteFile(),md5);
        }finally {
            try {
                mergeFileInputstream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //合并分块  校验文件MD5是否正确  项mongodb写入文件信息
    public ResponseResult mergechunks(String fileMd5,String fileName,Long fileSize,String mimetype,String fileExt){
        //获取块文件的路径
        String chunkfileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkfileFolder = new File(chunkfileFolderPath);
        if(!chunkfileFolder.exists()){
            chunkfileFolder.mkdirs();
        }
        //合并文件路径
        File mergeFile = new File(getFilePath(fileMd5,fileExt));
        //创建合并文件
        //合并文件存在先删除再创建
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        boolean newFile = false;
        try{
            newFile = mergeFile.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("mergechunks..create mergeFile fail:{}",e.getMessage());
        }
        if(!newFile){
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //获取块文件，此列表是已经排好序的列表
        List<File> chunkFiles = this.getChunkFiles(chunkfileFolder);
        //合并文件
        mergeFile = this.mergeFile(mergeFile,chunkFiles);
        if(mergeFile == null){
            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        //校验文件
        boolean checkResult = this.checkFileMd5(mergeFile,fileMd5);
        if(!checkResult){
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //将文件信息保存到数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        //文件路径保存相对路径
        mediaFile.setFilePath(getFileFolderRelativePath(fileMd5,fileExt));
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        //状态为上传成功
        mediaFile.setFileStatus("301002");
        MediaFile save = mediaFileRepository.save(mediaFile);
        //发送处理视频编码  向mq发送视频处理消息
        boolean flag = sendProcessVideoMsg(fileMd5);
        if(!flag){
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //合并成功后调用方法 向ma发送消息  可以处理视频
    private boolean sendProcessVideoMsg(String fileMd5){
        ;try{
            Map<String,String> msgMap = new HashMap<>();
            msgMap.put("mediaId",fileMd5);
            //发送的消息转为json格式
            String msg = JSON.toJSONString(msgMap);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingkey_media_video,msg);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
