package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.ManageService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Reference
    ManageService manageService;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public CartInfo addCart(String userId, String skuId, String num) {

        SkuInfo skuInfo = manageService.getSkuInfoById(skuId);

        //先查询数据库
        CartInfo cartInfoQuery = new CartInfo();
        cartInfoQuery.setUserId(userId);
        cartInfoQuery.setSkuId(skuId);
        CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfoQuery);

        if (cartInfoExist!=null){
            //数据库存在，则更新num
            cartInfoExist.setSkuName(skuInfo.getSkuName());
            cartInfoExist.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfoExist.setCartPrice(skuInfo.getPrice());
            int skuNum = Integer.parseInt(num);
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum()+skuNum);
            cartInfoExist.setSkuPrice(skuInfo.getPrice());
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
        }else {
            cartInfoExist = new CartInfo();
            cartInfoExist.setUserId(userId);
            cartInfoExist.setSkuId(skuId);
            cartInfoExist.setSkuNum(Integer.parseInt(num));
            cartInfoExist.setSkuName(skuInfo.getSkuName());
            cartInfoExist.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfoExist.setCartPrice(skuInfo.getPrice());
            cartInfoExist.setSkuPrice(skuInfo.getPrice());
            cartInfoMapper.insertSelective(cartInfoExist);
        }
        //修改缓存
        loadCartCache(userId);
        return cartInfoExist;
    }

    @Override
    public void mergeCartList(String userId, String userTmpId) {
        cartInfoMapper.mergeCartList(userId,userTmpId);
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userTmpId);
        cartInfoMapper.delete(cartInfo);
        loadCartCache(userId);
    }

    @Override
    public List<CartInfo> getCartList(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String cartKey = "cart:"+userId+":info";
        List<String> cartListJson = jedis.hvals(cartKey);
        List<CartInfo> cartInfoList= new ArrayList<>();
        if (cartListJson!=null && cartListJson.size()>0){
            for (String cartInfoJson : cartListJson) {
                CartInfo cartInfo = JSON.parseObject(cartInfoJson, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o2.getId().compareTo(o1.getId());
                }
            });
        }else {
            cartInfoList = loadCartCache(userId);
        }

        jedis.close();
        return cartInfoList;
    }

    @Override
    public void checkCart(String userId, String skuId, String isChecked) {
        Jedis jedis = redisUtil.getJedis();
        String cartKey = "cart:"+userId+":info";
        String hget = jedis.hget(cartKey, skuId);
        if(hget!=null&&hget.length()>0){
            CartInfo cartInfo = JSON.parseObject(hget, CartInfo.class);
            cartInfo.setIsChecked(isChecked);
            String cartInfoJson = JSON.toJSONString(cartInfo);
            jedis.hset(cartKey,skuId,cartInfoJson);

            //修改选中的cart缓存
            String cartCheckedKey = "cart:" + userId + ":checked";
            if(isChecked.equals("1")){
                //添加
                jedis.hset(cartCheckedKey,skuId,cartInfoJson);
                jedis.expire(cartCheckedKey,60*60);
            }else {
                //删除
                jedis.hdel(cartCheckedKey,skuId);
            }

        }

        jedis.close();
    }


    private List<CartInfo> loadCartCache(String userId) {

        Jedis jedis = redisUtil.getJedis();
        String cartKey = "cart:"+userId+":info";
        jedis.del(cartKey);
        List<CartInfo> cartInfoList =  cartInfoMapper.selectCartListWithSkuPrice(userId);
        if (cartInfoList == null || cartInfoList.size()==0){
            return cartInfoList;
        }
        Map<String, String> map = new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            map.put(cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
        }
        jedis.hmset(cartKey,map);
        jedis.expire(cartKey,60*60*24);
        jedis.close();

        return cartInfoList;
    }


    @Override
    public List<CartInfo> getCheckedCartList(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String cartCheckedKey = "cart:" + userId + ":checked";
        List<String> stringList = jedis.hvals(cartCheckedKey);
        if(stringList!=null && stringList.size()>0){
            List<CartInfo> cartInfoList = new ArrayList<>();
            for (String cartIntoJson : stringList) {
                CartInfo cartInfo = JSON.parseObject(cartIntoJson, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            return cartInfoList;
        }
        jedis.close();
        return null;
    }

}
