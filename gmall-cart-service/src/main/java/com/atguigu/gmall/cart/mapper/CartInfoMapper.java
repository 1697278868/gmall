package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.bean.CartInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CartInfoMapper extends Mapper<CartInfo> {

    List<CartInfo> selectCartListWithSkuPrice(@Param("userId") String userId);

    void mergeCartList(@Param("userId") String userId, @Param("userTmpId") String userTmpId);
}
