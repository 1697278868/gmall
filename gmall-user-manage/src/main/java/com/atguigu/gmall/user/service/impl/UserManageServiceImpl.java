package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserManageService;
import com.atguigu.gmall.user.mappper.UserAddressMapper;
import com.atguigu.gmall.user.mappper.UserInfoMapper;
;

import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
//@org.springframework.stereotype.Service
@Service
public class UserManageServiceImpl implements UserManageService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserAddressMapper userAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    public String userKey_prefix="user:";
    public String userinfoKey_suffix=":info";
    public int userKey_timeOut=60*60*24;


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

    @Override
    public UserInfo login(UserInfo userInfo) {
        String passwd = userInfo.getPasswd();
        //密码md5加密
        String digestAsHex = DigestUtils.md5DigestAsHex(passwd.getBytes());
        userInfo.setPasswd(digestAsHex);
        UserInfo selectOne = userInfoMapper.selectOne(userInfo);
        if(selectOne != null){
            //放入Redis缓存中
            Jedis jedis = redisUtil.getJedis();
            String key = userKey_prefix+selectOne.getId()+userinfoKey_suffix;
            String userInfoJson = JSON.toJSONString(selectOne);
            jedis.setex(key,userKey_timeOut,userInfoJson);
            jedis.close();

            return selectOne;
        }
        return null;
    }

    @Override
    public UserInfo verify(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String key = userKey_prefix+userId+userinfoKey_suffix;
        //查询redis是否存在
        String userInfoJson = jedis.get(key);

        UserInfo userInfo = JSON.parseObject(userInfoJson, UserInfo.class);
        if(userInfo != null){
            //认证成功后重新设置过期时间
            jedis.expire(key, userKey_timeOut);

            return userInfo;
        }
        jedis.close();
        return null;
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        List<UserAddress> addressList = userAddressMapper.select(userAddress);

        return addressList;
    }
}
