package com.sheng.securitydemo.config;

import com.sheng.securitydemo.annotation.NoAuthentication;
import com.sheng.securitydemo.handler.UserLoginAuthenticationFailureHandler;
import com.sheng.securitydemo.handler.UserLoginAuthenticationSuccessHandler;
import com.sheng.securitydemo.util.ApplicationContextHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    UserDetailsService userDetailsService;

    @Resource
    private UserLoginAuthenticationFailureHandler userLoginAuthenticationFailureHandler;//验证失败的处理类

    @Resource
    private UserLoginAuthenticationSuccessHandler userLoginAuthenticationSuccessHandler;//验证成功的处理类

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(password());
    }

    @Bean
    PasswordEncoder password() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.formLogin() // 表单登录
                .loginPage("/login") //配置登录页面  引入了thymeleaf
                .loginProcessingUrl("/user/login")//设置哪个是登录的url
                .failureHandler(userLoginAuthenticationFailureHandler)//验证失败处理
                .successHandler(userLoginAuthenticationSuccessHandler)//验证成功处理
                .permitAll()
                .and().authorizeRequests()
                .anyRequest()//其他请求
                .authenticated();//需要认证

        //关闭csrf
        http.csrf().disable();
    }

    //设置哪些不需要认证
    @Override
    public void configure(WebSecurity web) throws Exception {
        //静态资源放行，我就随便写写，根据自己静态资源结构去写。
        String[] urls = new String[]{
                "/js/**",
                "/imgs/**",
                "/css/**"
        };
        ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
        List<String> whiteList = new ArrayList<>();
        for (String url : urls) {
            whiteList.add(url);
        }

        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodEntry : map.entrySet()) {
            RequestMappingInfo key = requestMappingInfoHandlerMethodEntry.getKey();
            HandlerMethod value = requestMappingInfoHandlerMethodEntry.getValue();
            Set<String> patterns = key.getPatternsCondition().getPatterns();
            //无需权限都可以访问的类型
            NoAuthentication noAuthentication = value.getBeanType().getAnnotation(NoAuthentication.class);
            if (null != noAuthentication) {//整个controller不需要权限访问的
                RequestMapping annotation = value.getBeanType().getAnnotation(RequestMapping.class);
                if (null != annotation) {
                    String path = annotation.value()[0];
                    String suffix = "**";
                    if (path.lastIndexOf("/") != path.length() - 1)
                        suffix = "/**";
                    String s = path + suffix;
                    if (!whiteList.contains(s)) {
                        whiteList.add(s);
                    }
                }
            } else {//方法不需要权限访问的
                NoAuthentication annotation = value.getMethod().getAnnotation(NoAuthentication.class);
                if (null != annotation) {
                    patterns.forEach(p -> {
                        if (!whiteList.contains(p)) {
                            whiteList.add(p);
                        }
                    });
                }
            }
        }
        System.out.println("-----");
        for (String s : whiteList) {
            System.out.println(s);
        }
        urls = whiteList.toArray(urls);
        super.configure(web);
        web.httpFirewall(defaultHttpFirewall());

        web.ignoring().antMatchers(urls);
    }

    /**
     * 允许出现双斜杠的URL
     *
     * @return
     */
    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }
}