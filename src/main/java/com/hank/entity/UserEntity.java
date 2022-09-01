package com.hank.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user")
@Data
public class UserEntity {

    @TableId
    //id
    private int id;
    //第一句话
    private String firstMsg;
    //昵称
    private String nickName;
    //openid
    private String openId;
    //城市
    private String city;
    //模板id
    private String templateId;
}
