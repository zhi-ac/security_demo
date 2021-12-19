package com.sheng.securitydemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sheng.securitydemo.model.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
