package com.atguigu.gmall.service;



import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;

import java.util.List;

public interface UserManageService {
    List<UserInfo> getUserInfoListAll();

    UserInfo getUserInfoById(String id);

    void addUser(UserInfo userInfo);

    void updateUser(UserInfo userInfo);

    void updateUserByName(String name, UserInfo userInfo);

    void delUser(UserInfo userInfo);
    //判断登录用户信息
    UserInfo login(UserInfo userInfo);
    //根据userid进行用户认证
    UserInfo verify(String userId);
    //根据userid获取用户地址
    List<UserAddress> getUserAddressList(String userId);
}
