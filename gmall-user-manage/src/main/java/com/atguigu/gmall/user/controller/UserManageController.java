package com.atguigu.gmall.user.controller;


import com.atguigu.gmall.bean.UserInfo;

import com.atguigu.gmall.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserManageController {

    @Autowired
    UserManageService userService;

    @GetMapping("allUser")
    public List<UserInfo> getAllUser(){
        return  userService.getUserInfoListAll();
    }

    @GetMapping("userInfo")
    public UserInfo getUser(String id){
        return  userService.getUserInfoById(id);
    }

    @PostMapping("addUser")
    public  String  addUser(  UserInfo userInfo){
        userService.addUser(userInfo);
        return "success";
    }

    @PostMapping("delUser")
    public String deleteUser(String id){
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userService.delUser(userInfo);
        return  "success";
    }

    @PostMapping("updateUser")
    public String updateUserByName(UserInfo userInfo){
        userService.updateUser(userInfo);
        return  "success";
    }

    @PostMapping("updateUserByName")
    public String updateUser(UserInfo userInfo){
        userService.updateUserByName(userInfo.getName(),userInfo);
        return  "success";
    }

}
