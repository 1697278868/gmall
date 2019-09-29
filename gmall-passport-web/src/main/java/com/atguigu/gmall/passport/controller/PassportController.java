package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserManageService;
import com.atguigu.gmall.util.JwtUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    UserManageService userManageService;

    @GetMapping("index")
    public String index(HttpServletRequest request){
        //String originUrl = (String)request.getAttribute("originUrl");
        String originUrl = request.getParameter("originUrl");
        if (originUrl!=null){
            request.setAttribute("originUrl",originUrl);
        }
        return "index";
    }

    @PostMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request){
        UserInfo userInfoExist = userManageService.login(userInfo);
        if (userInfoExist!=null){
            //用jwt生成token
            Map<String, Object> map = new HashMap<>();
            map.put("userId",userInfoExist.getId());
            map.put("nickName",userInfoExist.getNickName());

            //获取请求的ip
            String ip = request.getHeader("X-forwarded-for");
            System.out.println(ip);
            System.out.println("getRemoteAddr:"+request.getRemoteAddr());
            //System.out.println("getRequestURI"+request.getRequestURI());login
            String token = JwtUtil.encode("atguigu", map, ip);

            return token;

        }
        return "fail";
    }

    @GetMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        String token = request.getParameter("token");
        String currentIp = request.getParameter("currentIp");
        if(StringUtils.isEmpty(token)||StringUtils.isEmpty(currentIp)){
            return "fail";
        }
        Map<String, Object> map = JwtUtil.decode(token, "atguigu", currentIp);
        if(map != null){
            String userId = (String)map.get("userId");
            UserInfo userInfo = userManageService.verify(userId);
            if (userInfo!=null){
                return "success";
            }
        }

        return "fail";
    }
}
