package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.util.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
public class CartController {

    @Reference
    CartService cartService;

    @PostMapping("addToCart")
    @LoginRequire(autoRedirect =false)
    public String addToCart(@RequestParam("skuId") String skuId , @RequestParam("num") String num,
                            HttpServletRequest request,
                            HttpServletResponse response){
        System.out.println("skuId:"+skuId+"-----  num:"+num);

        String userId = (String)request.getAttribute("userId");
        if (userId == null){
            //用户没有登录
            //从cookie中获取临时uerid
            userId = CookieUtil.getCookieValue(request, "user_tmp_id", false);
            if (userId == null){
                userId = UUID.randomUUID().toString();
                CookieUtil.setCookie(request,response,"user_tmp_id",userId,60*60*24*7,false);
            }
        }

        CartInfo cartInfo = cartService.addCart(userId,skuId,num);
        request.setAttribute("cartInfo",cartInfo);
        request.setAttribute("num",num);
        return "success";
    }

    @GetMapping("cartList")
    @LoginRequire(autoRedirect =false)
    public String cartList(HttpServletRequest request){
        String userId = (String)request.getAttribute("userId");
        String userTmpId = CookieUtil.getCookieValue(request, "user_tmp_id", false);
        if (userId != null && userTmpId!=null ){
            List<CartInfo> cartList = cartService.getCartList(userTmpId);
            if(cartList!=null && cartList.size()>0){
                cartService.mergeCartList(userId,userTmpId);
            }
        }
        if (userId == null && userTmpId!=null ){
            userId = userTmpId;
        }
        if (userId!=null){
            List<CartInfo> cartInfoList =cartService.getCartList(userId);
            request.setAttribute("cartInfoList",cartInfoList);
            if (cartInfoList!=null && cartInfoList.size()>0){
                BigDecimal totalPrice = new BigDecimal(0);
                for (CartInfo cartInfo : cartInfoList) {
                    BigDecimal skuPrice = cartInfo.getSkuPrice();
                    totalPrice = totalPrice.add(skuPrice.multiply( new BigDecimal(cartInfo.getSkuNum()) ));
                }
                request.setAttribute("totalPrice",totalPrice);

            }

        }

        return "cartList";
    }

    @PostMapping("checkCart")
    @LoginRequire(autoRedirect =false)
    @ResponseBody
    public void checkCart(HttpServletRequest request){
        String isChecked = request.getParameter("isChecked");
        String skuId = request.getParameter("skuId");
        String userId = (String)request.getAttribute("userId");
        if (userId==null){
             userId = CookieUtil.getCookieValue(request, "user_tmp_id", false);
        }
        System.out.println("isChecked="+isChecked+"; skuId="+skuId+"; userId="+userId);
        if (userId!=null){
            cartService.checkCart(userId,skuId,isChecked);
        }
    }
}
