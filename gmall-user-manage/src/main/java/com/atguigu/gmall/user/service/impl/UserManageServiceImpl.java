package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserManageService;
import com.atguigu.gmall.user.mappper.UserInfoMapper;
;

import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserManageServiceImpl implements UserManageService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public List<UserInfo> getUserInfoListAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public UserInfo getUserInfoById(String id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public void addUser(UserInfo userInfo) {
        userInfoMapper.insertSelective(userInfo);
    }

    @Override
    public void updateUser(UserInfo userInfo) {
        userInfoMapper.updateByPrimaryKeySelective(userInfo);
    }

    @Override
    public void updateUserByName(String name, UserInfo userInfo) {
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("name",name);
        userInfoMapper.updateByExampleSelective(userInfo,example);
    }

    @Override
    public void delUser(UserInfo userInfo) {
        userInfoMapper.deleteByPrimaryKey(userInfo.getId());
    }
}
