package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

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
        Example example = new Example(BaseAttrInfo.class);
        example.createCriteria().andEqualTo("catalog3Id",catalog3Id);
        List<BaseAttrInfo> attrInfoList = baseAttrInfoMapper.selectByExample(example);
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
}
