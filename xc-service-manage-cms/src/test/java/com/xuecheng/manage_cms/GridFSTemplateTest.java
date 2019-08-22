package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFSTemplateTest {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Test
    public void testGridFS() throws FileNotFoundException {
        //要存储的文件
        File file = new File("c:/course.ftl");
        //定义输入流
        FileInputStream inputStream = new FileInputStream(file);
        //向GridFS存储文件
        ObjectId objectId = gridFsTemplate.store(inputStream,"课程详情页面");
        System.out.println(objectId);
    }


    @Test
    public void queryFile() throws IOException {
        //根据文件id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5d3c146e9545c5688c59f99a")));
        //打开一个下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建GridFsResource对象,获取流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        //从流中获取数据
        String string = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(string);
    }

}
