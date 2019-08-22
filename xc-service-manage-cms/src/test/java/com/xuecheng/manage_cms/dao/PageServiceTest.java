package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {

    @Autowired
    CmsPageService cmsPageService;

    @Test
    public void test01(){
        String pageHtml = cmsPageService.getPageHtml("5d3d45aa9545c56a700da124");
        System.out.println(pageHtml);
    }

}
