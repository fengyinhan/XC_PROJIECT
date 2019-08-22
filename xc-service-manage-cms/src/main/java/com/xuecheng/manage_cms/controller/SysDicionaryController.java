package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.SysDicthinaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys")
public class SysDicionaryController implements SysDicthinaryControllerApi {

    @Autowired
    SysDictionaryService sysDictionaryService;

    @GetMapping("/dictionary/get/{dType}")
    @Override
    public SysDictionary getByType(@PathVariable("dType") String type) {
        return sysDictionaryService.getByType(type);
    }
}
