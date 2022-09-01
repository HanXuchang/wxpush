package com.hank.controller;

import com.alibaba.fastjson.JSONObject;
import com.hank.entity.Config;
import com.hank.entity.UserEntity;
import com.hank.service.IConfigService;
import com.hank.service.JiTangMsgService;
import com.hank.service.UserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@Slf4j
public class WxController {



    @Resource
    private IConfigService iConfigService;
    @Resource
    private JiTangMsgService jiTangMsgService;
    @Resource
    private UserService userService;

    @GetMapping("/wx/push")
    public String wxPush() {
        List<UserEntity> list = userService.list();
        for (UserEntity entity : list) {
            push(entity);
        }
        return "推送成功";
    }

    @Scheduled(cron = "${wx.mp.cron:#{null}}")
    public void autoPush() {
        List<UserEntity> list = userService.list();
        if (list.size()==0) {
            log.error("配置了定时自动推送，但是openid配置为空");
            return;
        }
        for (UserEntity userEntity : list) {
            push(userEntity);
        }
        log.info("定时推送成功");
    }


    /**
     * 推送微信模板消息
     * @param userEntity 用户信息
     */
    private void push(UserEntity userEntity) {
        Config config = iConfigService.getById(1);
        String appId = config.getAppId();
        String secret = config.getSecret();
        if (StringUtils.isEmpty(userEntity.getOpenId())) {
            throw new RuntimeException("推送用户为空");
        }
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(secret)) {
            log.info("微信配置信息，appid:{},secret:{}", appId, secret);
            throw new RuntimeException("微信配置错误，请检查");
        }
        JSONObject weatherFromThird = null;
        JSONObject weatherFromThird2 = null;
        String info = null;           //天气
        String temperature = null;    //温度
        String humidity = null;       //湿度
        try {
            weatherFromThird = jiTangMsgService.getWeatherFromThird(userEntity.getCity());
            if (weatherFromThird!=null){
                info = weatherFromThird.getJSONObject("realtime").getString("info");
                temperature = weatherFromThird.getJSONObject("realtime").getString("temperature");
                humidity = weatherFromThird.getJSONObject("realtime").getString("humidity")+"%";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (null == weatherFromThird) {
            try {
                weatherFromThird2 = jiTangMsgService.getWeatherFromThird2(userEntity.getCity());
                info = weatherFromThird2.getString("wea");
                temperature = weatherFromThird2.getString("tem");
                humidity = weatherFromThird2.getString("humidity");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (null == weatherFromThird2){
                throw new RuntimeException("获取天气出错");
            }
        }
//        String jiTangMsg = jiTangMsgService.getMsgFromThird();
        String jiTangMsg = "嘿嘿";

        WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
        wxMpConfigStorage.setAppId(appId);
        wxMpConfigStorage.setSecret(secret);
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().toUser(userEntity.getOpenId()).templateId(userEntity.getTemplateId()).build();
        templateMessage.addData(new WxMpTemplateData("first", userEntity.getFirstMsg() == null ? "" : userEntity.getFirstMsg(), "#0030EE"));
        templateMessage.addData(new WxMpTemplateData("city", userEntity.getCity() == null ? "未知" : userEntity.getCity(), "#0099FF"));
        templateMessage.addData(new WxMpTemplateData("weather", info, "#0099FF"));
        templateMessage.addData(new WxMpTemplateData("temperature", temperature + "℃", "#E6421A"));
        templateMessage.addData(new WxMpTemplateData("humidity", humidity, "#3333CC"));
        templateMessage.addData(new WxMpTemplateData("content", StringUtils.isEmpty(jiTangMsg) ? "" : jiTangMsg, "#A417C7"));
        try {
            log.info("发送模板消息，模板id:{},消息内容:{}", userEntity.getTemplateId(), templateMessage.toJson());
            wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (Exception e) {
            log.error("推送失败", e);
        }
    }

}
