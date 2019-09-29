package com.atguigu.gmall.litem.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.service.ManageService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
public class ItemController {
    @Reference
    ManageService manageService;

    @GetMapping("{skuId}.html")
    @LoginRequire(autoRedirect=false)
    public String getSkuInfoById(@PathVariable String skuId , HttpServletRequest request){
        SkuInfo skuInfo = manageService.getSkuInfoById(skuId);
        //String s = JSON.toJSONString(skuInfo);

        List<SpuSaleAttr>  spuSaleAttrlist = manageService.spuSaleAttrListIsChecked(skuInfo.getSpuId(),skuId);

        Map skuValueIdsMapObj = manageService.getskuValueIdsMap(skuInfo.getSpuId());
        String skuValueIdsMap = JSON.toJSONString(skuValueIdsMapObj);

        request.setAttribute("skuInfo",skuInfo);
        request.setAttribute("spuSaleAttrlist",spuSaleAttrlist);
        request.setAttribute("skuValueIdsMap",skuValueIdsMap);
        return "item";
    }
}
