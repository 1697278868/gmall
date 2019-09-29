package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OrderInfo;

public interface OrderService {
    //保存订单
    String saveOrder(OrderInfo orderInfo);
    //生产结算页的token
    String genToken(String userId);
    //验证结算页的token
    boolean verifyToken(String userId, String token);
}
