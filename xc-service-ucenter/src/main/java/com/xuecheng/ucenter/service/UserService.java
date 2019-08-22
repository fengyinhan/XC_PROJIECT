package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRespository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    XcUserRespository xcUserRespository;

    @Autowired
    XcCompanyUserRepository xcUserExtRespository;

    @Autowired
    XcMenuMapper xcMenuMapper;

    //根据用户账号查询用户信息
    public XcUser findXcUserByUsername(String username) {
           return xcUserRespository.findXcUserByUsername(username);
    }
    //根据账号查询用户的信息，返回用户扩展信息
    public XcUserExt getUserExt(String username){
        XcUser xcUser = this.findXcUserByUsername(username);
        if(xcUser == null){
            return null;
        }
        String userId = xcUser.getId();
        //查询用户所属公司
        XcCompanyUser xcCompanyUser = xcUserExtRespository.findByUserId(userId);
        //查询用户所拥有的权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);
        String companyId = xcCompanyUser.getCompanyId();
        XcUserExt xcUserExt = new XcUserExt();
        if(xcCompanyUser != null){

            BeanUtils.copyProperties(xcUser,xcUserExt);
            //设置公司id
            xcUserExt.setCompanyId(companyId);
            //设置权限
            xcUserExt.setPermissions(xcMenus);
        }
        return xcUserExt;
    }
}

