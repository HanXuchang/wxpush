package com.hank;

import com.hank.service.ISysColorService;
import com.hank.service.MsgService;
import com.hank.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@SpringBootTest
class WxpushApplicationTests {

    @Resource
    private MsgService msgService;
    @Resource
    private ISysColorService iSysColorService;
    @Resource
    private UserService userService;

    @Test
    void contextLoads() throws UnsupportedEncodingException {

        System.out.println(userService.count());

    }

}
