package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ManageController {

    @Reference
    ManageService manageService;


    //post:http://localhost:8082/getCatalog1
    @PostMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1(){
        List<BaseCatalog1> catalog1list = manageService.getCatalog1();
        return catalog1list;
    }

    //POST http://localhost:8082/getCatalog2?catalog1Id=2
    @PostMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        List<BaseCatalog2> catalog2List = manageService.getCatalog2(catalog1Id);
        return catalog2List;
    }

    // POST http://localhost:8082/getCatalog3?catalog2Id=13
    @PostMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        List<BaseCatalog3> catalog3List = manageService.getCatalog3(catalog2Id);
        return catalog3List;
    }

    //GET http://localhost:8082/attrInfoList?catalog3Id=61
    @GetMapping("attrInfoList")
    public List<BaseAttrInfo> attrInfoList(String catalog3Id){
        List<BaseAttrInfo> baseAttrInfoList =manageService.getBaseAttrInfoByCatalog3Id(catalog3Id);
        return baseAttrInfoList;
    }

    //http://localhost:8082/saveAttrInfo
    @PostMapping("saveAttrInfo")
    public String saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        System.out.println(baseAttrInfo);
        manageService.saveBaseAttrInfo(baseAttrInfo);
        return "success";
    }

    //xhr.js:178 POST http://localhost:8082/getAttrValueList?attrId=97
    @PostMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueListByAttrId(String attrId){
        BaseAttrInfo baseAttrInfo = manageService.getAttrValueListByAttrId(attrId);
        return baseAttrInfo.getAttrValueList();
    }


    //xhr.js:178 GET http://localhost:8082/spuList?catalog3Id=61
    @GetMapping("spuList")
    public List<SpuInfo> getSpuList(String catalog3Id ){
       List<SpuInfo>  spuList =  manageService.getSpuList(catalog3Id);
        return spuList;
    }

    //POST http://localhost:8082/baseSaleAttrList
    @PostMapping("baseSaleAttrList")
    public List<BaseSaleAttr> baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = manageService.getBaseSaleAttrList();
        return baseSaleAttrList;
    }


    //http://localhost:8082/saveSpuInfo
    @PostMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody SpuInfo spuInfo){
        System.out.println(spuInfo);

        manageService.saveSpuInfo(spuInfo);
        return "success";
    }
}
