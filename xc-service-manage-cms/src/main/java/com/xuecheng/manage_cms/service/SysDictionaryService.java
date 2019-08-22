package com.xuecheng.manage_cms.service;


import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysDictionaryService {

    @Autowired
    SysDictionaryRespository sysDictionaryRespository;

    public SysDictionary getByType(String type){
        return sysDictionaryRespository.findBydType(type);
    }
}
