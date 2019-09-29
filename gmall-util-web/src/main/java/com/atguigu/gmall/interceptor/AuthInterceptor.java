package com.atguigu.gmall.interceptor;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.HttpClientUtil;
import com.atguigu.gmall.util.WebConst;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {//拦截器

        String token = request.getParameter("newToken");
        //如何刚刚登陆，将token写入cookie
        if (token != null){
            CookieUtil.setCookie(request,response,"token",token, WebConst.COOKIE_MAXAGE,false);
        }
        //从cookie中获取token
        else if (token == null){
            token = CookieUtil.getCookieValue(request, "token", false);
        }
        //如果token不为空，将昵称放入request域中
        if (token!=null){
            //读取token
            Map map = getUserMapByToken(token);
            String nickName = (String)map.get("nickName");
            request.setAttribute("nickName",nickName);
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequire loginRequire = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if (loginRequire != null){
            //如果方法上有认证注解就进行认证
            boolean autoRedirect = loginRequire.autoRedirect();

            String currentIp = request.getHeader("x-forwarded-for");
            String getResult = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&currentIp=" + currentIp);
            if ("success".equals(getResult)){
                //认证通过,将userid放入request域中
                Map userMap = getUserMapByToken(token);
                String userId = (String)userMap.get("userId");
                request.setAttribute("userId",userId);
                return true;
            }else if(autoRedirect){
                //认证失败，//如果认证注解的值等于true，,跳转到登陆页面
                String requestURL = request.getRequestURL().toString();
                String encodeURL = URLEncoder.encode(requestURL, "UTF-8");
                response.sendRedirect(WebConst.LOGIN_ADDRESS+"?originUrl="+encodeURL);
                return false;
            }

        }
        return true;

    }

    public Map getUserMapByToken(String token){
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] decode = base64UrlCodec.decode(tokenUserInfo);
        String userInfoJson = null;
        try {
            userInfoJson = new String(decode,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map map = JSON.parseObject(userInfoJson, Map.class);
        return map;
    }
}
