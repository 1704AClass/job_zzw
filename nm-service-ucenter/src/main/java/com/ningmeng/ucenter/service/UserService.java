package com.ningmeng.ucenter.service;

import com.ningmeng.framework.domain.ucenter.NmCompanyUser;
import com.ningmeng.framework.domain.ucenter.NmUser;
import com.ningmeng.framework.domain.ucenter.ext.NmUserExt;
import com.ningmeng.ucenter.dao.NmCompanyUserRepository;
import com.ningmeng.ucenter.dao.NmUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private NmUserRepository nmUserRepository;

    @Autowired
    private NmCompanyUserRepository nmCompanyUserRepository;

    //根据用户账号查询用户信息
    private NmUser findNmUserByUsername(String username){
        return nmUserRepository.findNmUserByUsername(username);
    }

    //根据账号查询用户的信息，返回用户扩展信息
    public NmUserExt getUserExt(String username){
        NmUser nmUser=this.findNmUserByUsername(username);
        if(nmUser==null){
            return null;
        }
        NmUserExt nmUserExt=new NmUserExt();
        BeanUtils.copyProperties(nmUser,nmUserExt);
        //用户id
        String userId=nmUserExt.getId();
        //查询用户所属公司
        NmCompanyUser nmCompanyUser=nmCompanyUserRepository.findByUserId(userId);
        if(nmCompanyUser!=null){
            String companyId= nmCompanyUser.getCompanyId();
            nmUserExt.setCompanyId(companyId);
        }
        return nmUserExt;
    }
}
