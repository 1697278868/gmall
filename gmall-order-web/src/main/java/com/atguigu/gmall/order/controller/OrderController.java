package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserManageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Reference
    UserManageService userManageService;

    @GetMapping("getUserInfoById")
    public  UserInfo getUserInfo(String id){
        UserInfo userInfo = userManageService.getUserInfoById(id);
        return userInfo;
    }
}
