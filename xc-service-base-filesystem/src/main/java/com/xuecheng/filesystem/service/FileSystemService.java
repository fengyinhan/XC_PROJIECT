package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FilesystemReposite;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;

import java.io.IOException;
import java.util.Map;

@Service
public class FileSystemService {

    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;    //tracker服务器
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds; //连接超时
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;  //网络连接超时
    @Value("${xuecheng.fastdfs.charset}")
    String charset;   //编码

    @Autowired
    FilesystemReposite filesystemReposite;

    //上传文件
    public UploadFileResult upload(MultipartFile file,
                                   String filetag,
                                   String businesskey,
                                   String metadata){
            if(file == null){
                ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
            }
           //先上传文件到fastDFS,得到一个文件id
        String fileId = fdfs_upload(file);
            if(StringUtils.isEmpty(fileId)){
                ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
            }
            //将文件存入到mangodb
        FileSystem fileSystem = new FileSystem();
            fileSystem.setFileId(fileId);
            fileSystem.setBusinesskey(businesskey);
            fileSystem.setFileName(file.getOriginalFilename());
            fileSystem.setFilePath(fileId);
            fileSystem.setFiletag(filetag);
            fileSystem.setFileType(file.getContentType());
            if(StringUtils.isNotEmpty(metadata)){
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            }
        filesystemReposite.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);

    }



    //上传文件到fastDfs
    private String fdfs_upload(MultipartFile multipartFile){
        //初始化fastDFS的环境
        initFdfsConfig();
        TrackerClient trackerClient = new TrackerClient();

        try {
            TrackerServer trackerServer = trackerClient.getConnection();
            //得到storage服务器
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient上传文件
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storageServer);
            //上传文件
            //获取文件字节
            byte[] bytes = multipartFile.getBytes();
            //获取文件原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            //得到文件扩展名
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileId = storageClient1.upload_file1(bytes, ext, null);
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    //初始化fastDFS环境
    private void initFdfsConfig(){
    //初始化teacker服务地址
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
}
