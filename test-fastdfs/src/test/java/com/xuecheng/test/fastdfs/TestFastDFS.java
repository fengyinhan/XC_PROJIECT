package com.xuecheng.test.fastdfs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {



    @Test
    public void testupload(){


        try {
            //加载fastdfs-client.properties配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient,用于请求定义TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Stroage
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //创建stroageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storageServer);
            //向stroage服务器上传文件
            //本地文件路径
            String filePath = "d:/2.jpg";
            //上传成功拿到文件id
            String fileId = storageClient1.upload_file1(filePath, "jpg", null);
            System.out.println(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
