package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;


@SpringBootTest
@RunWith(SpringRunner.class)
public class RibbonDao {

    @Autowired
    RestTemplate restTemplate;

   @Test
    public void testRibbon(){
       //服务id
       String serviceId = "XC-SERVICE-MANAGE-CMS";
       for(int i=0;i<10;i++){
//通过服务id调用
       ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://" + serviceId + "/cms/page/get/5a754adf6abb500ad05688d9", Map.class);
       Map body = forEntity.getBody();
       System.out.println(body);
       }
   }
   }

