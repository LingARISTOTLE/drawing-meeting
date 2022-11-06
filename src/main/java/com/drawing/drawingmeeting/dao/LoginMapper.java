package com.drawing.drawingmeeting.dao;

import com.drawing.drawingmeeting.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper {

    @Select("select * from user where id = #{id}")
    User selectById(@Param("id") Integer id);

    @Select("select * from user where username = #{username}")
    User selectByUsername(@Param("username") String username);

}
