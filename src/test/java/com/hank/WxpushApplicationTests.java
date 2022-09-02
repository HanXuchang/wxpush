package com.hank;

import com.hank.service.MsgService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

@SpringBootTest
class WxpushApplicationTests {

    @Resource
    private MsgService msgService;

    @Test
    void contextLoads() throws UnsupportedEncodingException {

        String en = msgService.getEnglish().getString("english");
        System.out.println(en);

    }

}
