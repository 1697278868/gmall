package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.OrderDetail;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.cart.mapper.OrderDetailMapper;
import com.atguigu.gmall.cart.mapper.OrderInfoMapper;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String saveOrder(OrderInfo orderInfo) {
        orderInfoMapper.insertSelective(orderInfo);
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insertSelective(orderDetail);
        }
        return orderInfo.getId();
    }

    @Override
    public String genToken(String userId) {
        String tokenValue = UUID.randomUUID().toString().replace("-", "");
        String tokenKey="user:"+userId+":trade_code";
        Jedis jedis = redisUtil.getJedis();
        jedis.setex(tokenKey,60*15,tokenValue);
        jedis.close();
        return tokenValue;
    }

    @Override
    public boolean verifyToken(String userId, String token) {
        String tokenKey="user:"+userId+":trade_code";
        Jedis jedis = redisUtil.getJedis();
        String tokenExist = jedis.get(tokenKey);
        String watch = jedis.watch(tokenKey);
        Transaction transaction = jedis.multi();
        if(tokenExist!=null && tokenExist.equals(token)){
            transaction.del(tokenKey);
        }
        List<Object> execList = transaction.exec();
        if (execList!=null && execList.size()>0 && (Long)execList.get(0)==1L){
            jedis.close();
            return true;
        }
        jedis.close();
        return false;

    }
}
