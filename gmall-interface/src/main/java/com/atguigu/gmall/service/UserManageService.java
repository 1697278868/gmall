package com.atguigu.gmall.service;



import com.atguigu.gmall.bean.UserInfo;

import java.util.List;

public interface UserManageService {
    List<UserInfo> getUserInfoListAll();

    UserInfo getUserInfoById(String id);

    void addUser(UserInfo userInfo);

    void updateUser(UserInfo userInfo);

    void updateUserByName(String name, UserInfo userInfo);

    void delUser(UserInfo userInfo);

}
