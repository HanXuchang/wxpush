package com.hank.controller;

import com.alibaba.fastjson.JSONObject;
import com.hank.entity.UserEntity;
import com.hank.service.ISysColorService;
import com.hank.service.ISysConfigService;
import com.hank.service.MsgService;
import com.hank.service.UserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

@RestController
@Slf4j
public class WxController {



    @Resource
    private ISysConfigService iSysConfigService;
    @Resource
    private MsgService msgService;
    @Resource
    private UserService userService;
    @Resource
    private ISysColorService iSysColorService;

    private JSONObject weatherFromThird = null;
    private String info = null;           //天气
    private String temperature = null;    //温度
    private String tem1 = null;           //最高温度
    private String tem2 = null;           //最低温度
    private String humidity = null;       //湿度
    private String date = null;           //时间
    private String content = null;

    private String appId = null;
    private String secret = null;

    @GetMapping("/wx/push")
    public String wxPush() {
        List<UserEntity> list = userService.list();
        for (UserEntity entity : list) {
            push2(entity);
        }
        return "推送成功";
    }

    @Scheduled(cron = "${wx.mp.cron:#{null}}")
    public void autoPush() {
        List<UserEntity> list = userService.list();
        if (list.size()==0) {
            return;
        }
        for (UserEntity userEntity : list) {
            push2(userEntity);
        }
    }


    /**
     * 推送微信模板消息
     * @param userEntity 用户信息
     */
    private void push(UserEntity userEntity) {
        appId = iSysConfigService.getValue("app_id");
        secret = iSysConfigService.getValue("secret");
        if (StringUtils.isEmpty(userEntity.getOpenId())) {
            throw new RuntimeException("推送用户为空");
        }
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(secret)) {
            log.info("微信配置信息，appid:{},secret:{}", appId, secret);
            throw new RuntimeException("微信配置错误，请检查");
        }
        try {
            weatherFromThird = msgService.getWeatherFromThird(userEntity.getCity());
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
                weatherFromThird = msgService.getWeatherFromThird2(userEntity.getCity());
                info = weatherFromThird.getString("wea");
                temperature = weatherFromThird.getString("tem");
                humidity = weatherFromThird.getString("humidity");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (null == weatherFromThird){
                throw new RuntimeException("获取天气出错");
            }
        }
        String jiTangMsg = msgService.getMsgFromThird();
//        String jiTangMsg = "嘿嘿";

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

    /**
     * 推送微信模板消息
     * @param userEntity 用户信息
     */
    private void push2(UserEntity userEntity) {
        int count = iSysColorService.count();
        int colorId = new Random().nextInt(count)+1;
        appId = iSysConfigService.getValue("app_id");
        secret = iSysConfigService.getValue("secret");
        if (StringUtils.isEmpty(userEntity.getOpenId())) {
            throw new RuntimeException("推送用户为空");
        }
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(secret)) {
            throw new RuntimeException("微信配置错误，请检查");
        }
        try {
            weatherFromThird = msgService.getWeatherFromThird2(userEntity.getCity());
            date = weatherFromThird.getString("date")+"  "+weatherFromThird.getString("week");
            info = weatherFromThird.getString("wea");
            temperature = weatherFromThird.getString("tem");
            tem1 = weatherFromThird.getString("tem1");
            tem2 = weatherFromThird.getString("tem2");
            humidity = weatherFromThird.getString("humidity");
            content = weatherFromThird.getString("air_tips");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (null == weatherFromThird){
            throw new RuntimeException("获取天气出错");
        }
        //鸡汤
//        String jiTangMsg = msgService.getMsgFromThird();
        JSONObject english = msgService.getEnglish();
        String en = english.getString("english");
        String cn = english.getString("chinese");

        WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
        wxMpConfigStorage.setAppId(appId);
        wxMpConfigStorage.setSecret(secret);
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().toUser(userEntity.getOpenId()).templateId(userEntity.getTemplateId()).build();
        //第一句话
        templateMessage.addData(new WxMpTemplateData("first", userEntity.getFirstMsg() == null ? "" : userEntity.getFirstMsg(), iSysColorService.getById((colorId)%count+1).getColor()));
        //日期
        templateMessage.addData(new WxMpTemplateData("date", date == null ? "" : date, iSysColorService.getById((colorId+8)%count+1).getColor()));
        //城市
        templateMessage.addData(new WxMpTemplateData("city", userEntity.getCity() == null ? "未知" : userEntity.getCity(), iSysColorService.getById((colorId+1)%count+1).getColor()));
        //天气
        templateMessage.addData(new WxMpTemplateData("weather", info, iSysColorService.getById((colorId+2)%count+1).getColor()));
        //当前温度
        templateMessage.addData(new WxMpTemplateData("temperature", temperature + "℃", iSysColorService.getById((colorId+3)%count+1).getColor()));
        //今日温度
        templateMessage.addData(new WxMpTemplateData("tem1", tem1 + "℃", iSysColorService.getById((colorId+4)%count+1).getColor()));
        templateMessage.addData(new WxMpTemplateData("tem2", tem2 + "℃", iSysColorService.getById((colorId+4)%count+1).getColor()));
        //今日湿度
        templateMessage.addData(new WxMpTemplateData("humidity", humidity, iSysColorService.getById((colorId+5)%count+1).getColor()));
        //英文
        templateMessage.addData(new WxMpTemplateData("en", StringUtils.isEmpty(en) ? "" : en, iSysColorService.getById((colorId+6)%count+1).getColor()));
        //中文
        templateMessage.addData(new WxMpTemplateData("cn", StringUtils.isEmpty(cn) ? "" : cn, iSysColorService.getById((colorId+7)%count+1).getColor()));

        templateMessage.addData(new WxMpTemplateData("content", StringUtils.isEmpty(content) ? "" : content, iSysColorService.getById((colorId+9)%count+1).getColor()));
        try {
            wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (Exception ignored) {
        }
    }

}
