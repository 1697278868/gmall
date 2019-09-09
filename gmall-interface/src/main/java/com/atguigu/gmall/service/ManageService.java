package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface ManageService {
    //获得一级所有的分类列表
    public List<BaseCatalog1> getCatalog1();
    //根据一级分类的id获取二级分类列表
    public List<BaseCatalog2> getCatalog2(String catalog1Id);
    //根据二级分类的id获取三级级分类列表
    public List<BaseCatalog3> getCatalog3(String catalog2Id);
    //根据三级分类的id获取平台属性
    List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(String catalog3Id);

    //保存平台属性
    void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo);

    //根据平台id获取平台属性和平台属性值
    BaseAttrInfo getAttrValueListByAttrId(String attrId);
    //根据三级分类id获取获取spu列表
    List<SpuInfo> getSpuList(String catalog3Id);
    //获取基础销售属性列表
    List<BaseSaleAttr> getBaseSaleAttrList();
    //保存spu
    void saveSpuInfo(SpuInfo spuInfo);
    //根据spuid获取图片
    List<SpuImage> getSupImageListBySpuId(String spuId);
    //根据supid获取销售属性
    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(String spuId);

    void saveSkuInfo(SkuInfo skuInfo);
}
