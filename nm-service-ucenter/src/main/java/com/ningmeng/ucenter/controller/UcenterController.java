package com.ningmeng.ucenter.controller;

import com.ningmeng.api.ucenter.UcenterControllerApi;
import com.ningmeng.framework.domain.ucenter.ext.NmUserExt;
import com.ningmeng.ucenter.service.UcenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ucenter")
public class UcenterController implements UcenterControllerApi {

    @Autowired
    private UcenterService ucenterService;

    @Override
    @GetMapping("/getuserext")
    public NmUserExt getUserExt(String username) {
        NmUserExt nmUser = ucenterService.getUserExt(username);
        return nmUser;
    }
}
