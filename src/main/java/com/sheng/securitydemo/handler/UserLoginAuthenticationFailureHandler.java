package com.sheng.securitydemo.handler;

import com.google.gson.Gson;
import com.sheng.securitydemo.common.JsonData;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用户认证失败处理类
 */
@Component("userLoginAuthenticationFailureHandler")
public class UserLoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        System.out.println("===" + exception.getMessage());
        JsonData jsonData = new JsonData(403,"账号或密码错误");
        String json = new Gson().toJson(jsonData);//包装成Json 发送的前台
        System.out.println(json);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.write(json);
        out.flush();
        out.close();
    }
}
