package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManagerServiceImpl implements ManageService {
    @Autowired
    BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    SpuImageMapper spuImageMapper;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public List<BaseCatalog1> getCatalog1() {
        List<BaseCatalog1> catalog11List = baseCatalog1Mapper.selectAll();
        return catalog11List;
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 catalog2 = new BaseCatalog2();
        catalog2.setCatalog1Id(catalog1Id);
        List<BaseCatalog2> catalog2List = baseCatalog2Mapper.select(catalog2);
        return catalog2List;
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 catalog3 = new BaseCatalog3();
        catalog3.setCatalog2Id(catalog2Id);
        List<BaseCatalog3> catalog3List = baseCatalog3Mapper.select(catalog3);
        return catalog3List;
    }

    @Override
    public List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(String catalog3Id) {
       /* Example example = new Example(BaseAttrInfo.class);
        example.createCriteria().andEqualTo("catalog3Id",catalog3Id);
        List<BaseAttrInfo> attrInfoList = baseAttrInfoMapper.selectByExample(example);*/

        List<BaseAttrInfo> attrInfoList = baseAttrInfoMapper.getBaseAttrInfoByCatalog3Id(catalog3Id);
        return attrInfoList;
    }

    @Override
    @Transactional
    public void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo) {
        if(StringUtils.isEmpty(baseAttrInfo.getId())){
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }else {
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);

            Example example = new Example(BaseAttrValue.class);
            example.createCriteria().andEqualTo("attrId",baseAttrInfo.getId());
            baseAttrValueMapper.deleteByExample(example);
        }

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insertSelective(baseAttrValue);
        }
    }

    @Override
    public BaseAttrInfo getAttrValueListByAttrId(String attrId) {
        //获取平台属性
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        //根据平台属性id获取平台属性值
        Example example = new Example(BaseAttrValue.class);
        example.createCriteria().andEqualTo("attrId",attrId);
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectByExample(example);

        baseAttrInfo.setAttrValueList(baseAttrValueList);
        return baseAttrInfo;
    }

    @Override
    public List<SpuInfo> getSpuList(String catalog3Id) {
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        //保存spu
        spuInfoMapper.insertSelective(spuInfo);
        String spuInfoId = spuInfo.getId();
        //保存图片
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(spuInfoId);
            spuImageMapper.insertSelective(spuImage);
        }
        //保存销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuInfoId);
            spuSaleAttrMapper.insertSelective(spuSaleAttr);

            //保存销售属性值
            List<SpuSaleAttrValue> attrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : attrValueList) {
                //spuSaleAttrValue.setSaleAttrId(spuSaleAttr.getId());
                spuSaleAttrValue.setSpuId(spuInfoId);
                spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
            }
        }

    }

    @Override
    public List<SpuImage> getSupImageListBySpuId(String spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        List<SpuImage> spuImageList = spuImageMapper.select(spuImage);
        return spuImageList;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(String spuId) {
        return  spuSaleAttrMapper.getSpuSaleAttrListBySpuId(spuId);
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {

        String skuInfoId = skuInfo.getId();
        //保存sku的基本信息
        if(StringUtils.isEmpty(skuInfoId)){
            //保存
            skuInfoMapper.insertSelective(skuInfo);
            skuInfoId = skuInfo.getId();
        }else{
            //修改
            skuInfoMapper.updateByPrimaryKeySelective(skuInfo);
            //删除sku图片
            SkuImage skuImageDele = new SkuImage();
            skuImageDele.setSkuId(skuInfoId);
            skuImageMapper.delete(skuImageDele);
            //删除平台属性值
            SkuAttrValue skuAttrValueDele = new SkuAttrValue();
            skuAttrValueDele.setSkuId(skuInfoId);
            skuAttrValueMapper.delete(skuAttrValueDele);
            //删除销售属性
            SkuSaleAttrValue skuSaleAttrValueDele = new SkuSaleAttrValue();
            skuSaleAttrValueDele.setSkuId(skuInfoId);
            skuSaleAttrValueMapper.delete(skuSaleAttrValueDele);
        }

        //保存图片
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage image : skuImageList) {
            image.setSkuId(skuInfoId);
            skuImageMapper.insertSelective(image);
        }
        //保存平台属性值
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuInfoId);
            skuAttrValueMapper.insertSelective(skuAttrValue);
        }
        //保存销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuInfoId);
            skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
        }
    }

    @Override
    public SkuInfo getSkuInfoById(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);

        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);

        skuInfo.setSkuImageList(skuImageList);
        return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> spuSaleAttrListIsChecked(String spuId, String skuId) {
        return spuSaleAttrMapper.spuSaleAttrValueListIsChecked(spuId,skuId);
    }

    @Override
    public Map getskuValueIdsMap(String spuId) {
        List<Map> saleAttrValuesBySpu = skuSaleAttrValueMapper.getSaleAttrValuesBySpu(spuId);
        HashMap skuValueIdsMap  = new HashMap<>();
        for (Map map : saleAttrValuesBySpu) {
            String valueIds = (String) map.get("value_ids");
            String skuId =(Long)map.get("sku_id")+ "";
            skuValueIdsMap.put(valueIds,skuId);
        }
        return skuValueIdsMap;
    }
}
