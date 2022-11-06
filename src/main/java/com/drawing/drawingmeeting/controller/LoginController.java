package com.drawing.drawingmeeting.controller;

import com.drawing.drawingmeeting.dao.LoginMapper;
import com.drawing.drawingmeeting.pojo.LoginResult;
import com.drawing.drawingmeeting.pojo.User;
import com.drawing.drawingmeeting.utils.JsonTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    LoginMapper loginMapper;

    @GetMapping("/loginTest")
    public String getContext() {
        System.out.println("测试git能不能在idea进行push");
        return "ceshi";
    }

    @GetMapping("/login/{username}/{password}")
    public String login(@PathVariable("username") String username, @PathVariable("password") String password) {

        //判断username和password是否为null
        if (username == null || password == null || username.length() == 0 || password.length() == 0) {
            return JsonTools.getJson(new LoginResult(false, "用户名或密码不能为空"));
        }

        //查数据库
        User user = loginMapper.selectByUsername(username);
        if (user == null) {
            return JsonTools.getJson(new LoginResult(false, "用户不存在"));
        }
        if (user.getPassword().equals(password)) {
            return JsonTools.getJson(new LoginResult(true, "登录成功"));
        } else {

            return JsonTools.getJson(new LoginResult(false, "用户名或密码错误"));

        }
    }

}
