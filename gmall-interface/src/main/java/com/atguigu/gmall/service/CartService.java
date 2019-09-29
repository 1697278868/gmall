package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.CartInfo;

import java.util.List;

public interface CartService {

    //加入购物车
    CartInfo addCart(String userId, String skuId, String num);
    //合并购物车
    void mergeCartList(String userId, String userTmpId);
    //获取用户的购物车列表
    List<CartInfo> getCartList(String userId);
    //保存勾选中的状态
    void checkCart(String userId, String skuId, String isChecked);
    //获取用户需要结账的商品清单
    List<CartInfo> getCheckedCartList(String userId);
}
