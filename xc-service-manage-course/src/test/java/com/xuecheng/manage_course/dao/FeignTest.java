package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FeignTest {


    @Autowired
    CmsPageClient cmsPageClient;

    @Test
    public void test01(){
        CmsPage cmsPage = cmsPageClient.findById("5d4aa4c98ccf401098b9e5b1");
        System.out.println(cmsPage);
    }

}
