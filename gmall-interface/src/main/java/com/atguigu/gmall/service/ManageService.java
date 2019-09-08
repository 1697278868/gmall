package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseCatalog1;
import com.atguigu.gmall.bean.BaseCatalog2;
import com.atguigu.gmall.bean.BaseCatalog3;

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
}
