package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseAttrValue;
import com.atguigu.gmall.bean.es.SkuLsParams;
import com.atguigu.gmall.bean.es.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
@CrossOrigin
public class ListController {

    @Reference
    ListService listService;

    @Reference
    ManageService manageService;

    @GetMapping("list.html")
    public String getList(SkuLsParams skuLsParams , Model model){
        SkuLsResult skuLsResult = listService.searchES(skuLsParams);
        //sku列表
        model.addAttribute("skuLsInfoList",skuLsResult.getSkuLsInfoList());
        //获取sku平台属性值列表
        List<String> valueIdList = skuLsResult.getAttrValueIdList();
        List<BaseAttrInfo> attrInfoList =  manageService.getBaseAttrInfoByValueIds(valueIdList);

        ArrayList<BaseAttrValue> valueArrayList = new ArrayList<>();
        //过滤掉已选中的平台属性
        for (Iterator<BaseAttrInfo> iterator = attrInfoList.iterator(); iterator.hasNext(); ) {
            BaseAttrInfo baseAttrInfo = iterator.next();
            //获取平台属性的属性值列表
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //获取参数中已选中的属性id
                if(skuLsParams.getValueId()!=null&&skuLsParams.getValueId().length>0){
                    //遍历参数中的valueIds
                    for (int i = 0; i <skuLsParams.getValueId().length ; i++) {
                        String valueid = skuLsParams.getValueId()[i];
                        //比较
                        if(valueid.equals(baseAttrValue.getId())){
                            //如果相等，移除平台属性
                            iterator.remove();

                            //添加面包屑列表
                            BaseAttrValue attrValueSelected  = new BaseAttrValue();
                            attrValueSelected.setValueName(baseAttrInfo.getAttrName()+":"+baseAttrValue.getValueName());
                            //设置面包屑的历史路径
                            attrValueSelected.setUrlParam(makeParamUrl(skuLsParams,valueid));
                            valueArrayList.add(attrValueSelected);
                        }
                    }
                }
            }
        }
        //设置面包屑列表
        model.addAttribute("valueArrayList",valueArrayList);
        model.addAttribute("attrInfoList",attrInfoList);
        //获取历史路径
        String paramUrl = makeParamUrl(skuLsParams);
        model.addAttribute("paramUrl",paramUrl);

        if (skuLsParams.getKeyword()!=null){
            model.addAttribute("keyword",skuLsParams.getKeyword());
        }
        return "list";

    }

    public String makeParamUrl(SkuLsParams skuLsParams, String... excludeValueIds){
        String paramUrl = "";
        if (skuLsParams.getKeyword() != null){
            paramUrl += "keyword="+skuLsParams.getKeyword();
        }else if (skuLsParams.getCatalog3Id() != null){
            paramUrl += "catalog3Id="+skuLsParams.getCatalog3Id();
        }

        if(skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0){

            for (int i = 0; i <skuLsParams.getValueId().length ; i++) {
                String valueId = skuLsParams.getValueId()[i];
                if (excludeValueIds!=null && excludeValueIds.length>0 ){
                    String excludeValueId = excludeValueIds[0];
                    if(excludeValueId.equals(valueId)){
                        continue;
                    }
                }

                if (paramUrl.length()>0){
                    paramUrl+="&";
                }
                paramUrl += "valueId="+valueId;
            }
        }

        return paramUrl;
    }
}
