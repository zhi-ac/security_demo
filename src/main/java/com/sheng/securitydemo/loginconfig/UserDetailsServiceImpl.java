package com.sheng.securitydemo.loginconfig;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sheng.securitydemo.mapper.AdminMapper;
import com.sheng.securitydemo.model.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * SpringSecurity 自动调用该类
 * 登录实现类 默认 继承 UserDetailsService
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private AdminMapper adminMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(wrapper);
        if (admin == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        List<GrantedAuthority> auths = new ArrayList<>();
        return new User(admin.getUsername(), new BCryptPasswordEncoder().encode(admin.getPassword()), auths);
    }
}
