package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;

@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    CmsPageService cmsPageService;

    //接收到页面id
    @GetMapping("/cms/preview/{pageId}")
    public void preview(@PathVariable("pageId") String pageId){

        String pageHtml = cmsPageService.getPageHtml(pageId);
        try {
        if(StringUtils.isNotEmpty(pageHtml)){


            ServletOutputStream outputStream = response.getOutputStream();
            response.setHeader("Content-type","text/html;charset=utf-8");
            outputStream.write(pageHtml.getBytes("utf-8"));
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
