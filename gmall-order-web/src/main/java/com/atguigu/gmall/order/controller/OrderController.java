package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.bean.enums.OrderStatus;
import com.atguigu.gmall.bean.enums.ProcessStatus;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.UserManageService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    UserManageService userManageService;

    @Reference
    CartService cartService;

    @Reference
    OrderService orderService;

    @GetMapping("toTrade")
    @LoginRequire
    public String toTrade(HttpServletRequest request){
        String userId = (String)request.getAttribute("userId");
        System.out.println("订单usrid="+userId);
        //根据userid获取用户地址
        List<UserAddress>  addressList= userManageService.getUserAddressList(userId);
        request.setAttribute("addressList",addressList);

        //用户需要结账的商品清单
        List<CartInfo> checkedCartList  = cartService.getCheckedCartList(userId);

        BigDecimal totalPrice = new BigDecimal(0);
        if (checkedCartList!=null && checkedCartList.size()>0){
            for (CartInfo cartInfo : checkedCartList) {
                BigDecimal skuPrice = cartInfo.getSkuPrice();
                totalPrice = totalPrice.add(skuPrice.multiply( new BigDecimal(cartInfo.getSkuNum()) ));
            }
        }
        request.setAttribute("checkedCartList",checkedCartList);
        request.setAttribute("totalPrice",totalPrice);
        String orderToken = orderService.genToken(userId);
        request.setAttribute("tradeNo",orderToken);
        return "trade";
    }

    @PostMapping("submitOrder")
    @LoginRequire
    public String submitOrder(OrderInfo orderInfo,HttpServletRequest request){
        String userId = (String)request.getAttribute("userId");
        String orderToken = request.getParameter("tradeNo");
        boolean verify = orderService.verifyToken(userId, orderToken);
        if(!verify){
            request.setAttribute("errMsg","页面已失效，请重新结算！");
            return  "tradeFail";
        }

        orderInfo.setUserId(userId);
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.setCreateTime(new Date());
        Date expireTime = DateUtils.addMinutes(new Date(), 15);
        orderInfo.setExpireTime(expireTime);
        orderInfo.sumTotalAmount();



        // 保存
        String orderId = orderService.saveOrder(orderInfo);
        // 重定向
        return "redirect://payment.gmall.com/index?orderId="+orderId;


    }
}
