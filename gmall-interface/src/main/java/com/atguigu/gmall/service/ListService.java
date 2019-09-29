package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.es.SkuLsInfo;
import com.atguigu.gmall.bean.es.SkuLsParams;
import com.atguigu.gmall.bean.es.SkuLsResult;

public interface ListService {
    public void saveSkuInfo(SkuLsInfo skuLsInfo);

    public SkuLsResult searchES(SkuLsParams skuLsParams);
}
