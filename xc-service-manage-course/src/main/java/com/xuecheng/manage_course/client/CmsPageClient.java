package com.xuecheng.manage_course.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "XC-SERVICE-MANAGE-CMS")//指定远程调用的服务名
public interface CmsPageClient {

    //根据id查询页面信息,远程调用cms
    @GetMapping("/cms/page/get/{id}")  //用GetMapping表示Http的方法类型
    public CmsPage findById(@PathVariable("id") String id);

    //保存页面用于课程预览
    @PostMapping("/cms/page/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage);

    //一键发布课程
    @PostMapping("cms/page/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage);
}
