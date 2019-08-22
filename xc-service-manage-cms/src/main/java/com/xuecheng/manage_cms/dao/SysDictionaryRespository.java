package com.xuecheng.manage_cms.dao;


import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;
//数据词典
public interface SysDictionaryRespository extends MongoRepository<SysDictionary,String> {
  //根据type类型进行查询
    public SysDictionary findBydType(String dType);
}
