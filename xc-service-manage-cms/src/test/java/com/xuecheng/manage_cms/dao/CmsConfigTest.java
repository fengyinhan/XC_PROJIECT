package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsConfigTest {

    @Autowired
    CmsConfigRepository cmsConfigRepository;

    @Test
    public void test01(){
        Optional<CmsConfig> optional = cmsConfigRepository.findById("5a791725dd573c3574ee333f");
        if(optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            System.out.println(cmsConfig);
        }
    }
}
