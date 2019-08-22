package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcUserRespository extends JpaRepository<XcUser,String> {
    XcUser findXcUserByUsername(String username);
}
