package com.drawing.drawingmeeting;

import com.drawing.drawingmeeting.dao.LoginMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DrawingMeetingApplicationTests {

    @Autowired
    LoginMapper loginMapper;

    @Test
    void contextLoads() {

        System.out.println(loginMapper);
        System.out.println(loginMapper.selectByUsername("zhangsan").toString());

    }

}
