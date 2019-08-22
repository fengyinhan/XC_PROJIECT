package com.xuecheng.search;


import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {

    @Autowired
    RestClient client;

    @Autowired
    RestHighLevelClient restHighLevelClient;


    @Test
    public void testDel() throws IOException {

        //删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        //删除索引
        DeleteIndexResponse deleteIndexResponse = restHighLevelClient.indices().delete(deleteIndexRequest);
        //删除索引响应结果
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);

    }


    @Test
    public void testCreate() throws IOException {
        //创建索引请求对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
//设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards",1)
                .put("number_of_replicas",0));
        //创建索引操作客户端
        IndicesClient indices = restHighLevelClient.indices();
//创建响应对象
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
//得到响应结果
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
}
