package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRespositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void test01(){
        List<CmsPage> cmsPages = cmsPageRepository.findAll();
        System.out.println(cmsPages);
    }

    //分页查询
    @Test
    public void test02(){
        int page = 0;//从0开始
        int size = 10;//每页显示10页
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> cmsPages = cmsPageRepository.findAll(pageable);
        System.out.println(cmsPages);
    }


    //实习自定义条件查询
    @Test
    public void  testFindAll(){

        int page = 0;//从0开始
        int size = 10;//每页显示10页
        Pageable pageable = PageRequest.of(page, size);
        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //页面别名模糊查询,需要自定义字符串的匹配器实现模糊查询
        //条件值
        CmsPage cmsPage = new CmsPage();
        //站点id   如果不设置条件匹配器  就是默认精确查询
         //只需要在对象中设置即可
        //模板id

        cmsPage.setPageAliase("轮播");

        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        Page<CmsPage> all  = cmsPageRepository.findAll(example,pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);
        //
    }
    //根据页面名查询
    @Test
    public void test03(){
        CmsPage cmsPage = cmsPageRepository.findByPageName("index2.html");
        System.out.println(cmsPage);
    }



}
