package com.hank.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hank.common.utils.HttpUtil;
import com.hank.service.IConfigService;
import com.hank.service.JiTangMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service("jiTangMsgService")
@Slf4j
public class JiTangMsgServiceImpl implements JiTangMsgService {

    @Resource
    private IConfigService iConfigService;

    @Override
    public String getMsgFromThird() {
        String jitangKey = iConfigService.getById(1).getJuheJitangKey();
        if (StringUtils.isEmpty(jitangKey)) {
            return null;
        }
        String get = HttpUtil.doGet("https://apis.juhe.cn/fapig/soup/query?key=" + jitangKey);
        log.info("从第三方平台获取到心灵鸡汤的结果是:{}", get);
        if (StringUtils.isNotEmpty(get)) {
            JSONObject jsonObject = JSON.parseObject(get);
            if (0 != jsonObject.getInteger("error_code")) {
                return null;
            }
            JSONObject result = jsonObject.getJSONObject("result");
            return result.getString("text");
        }
        return null;
    }

    /**
     *
     * @param city 城市
     * @return 数据
     * @throws UnsupportedEncodingException 异常
     */
    public JSONObject getWeatherFromThird(String city) throws UnsupportedEncodingException {
        String weatherKey = iConfigService.getById(1).getJuheWeatherKey();
        if (StringUtils.isEmpty(weatherKey)) {
            return null;
        }
        if (StringUtils.isEmpty(city)) {
            return null;
        }
        String get = HttpUtil.doGet("http://apis.juhe.cn/simpleWeather/query?city=" + URLEncoder.encode(city, "UTF-8") + "&key=" + weatherKey);
        log.info("从第三方平台获取天气预报的结果是:{}", get);
        if (StringUtils.isNotEmpty(get)) {
            JSONObject jsonObject = JSON.parseObject(get);
            if (0 != jsonObject.getInteger("error_code")) {
                return null;
            }
            return jsonObject.getJSONObject("result");
        }
        return null;
    }

    @Override
    public JSONObject getWeatherFromThird2(String city) throws UnsupportedEncodingException {
        String yikeappid = iConfigService.getById(1).getYikeAppid();
        String yikeappsecret = iConfigService.getById(1).getYikeAppsecret();
        if (StringUtils.isEmpty(yikeappid)) {
            return null;
        }
        if (StringUtils.isEmpty(city)) {
            return null;
        }
        String get = HttpUtil.doGet("https://v0.yiketianqi.com/api?unescape=1&version=v61" +
                "&appid="+yikeappid+
                "&appsecret="+yikeappsecret+
                "&city="+city);
        log.info("从第三方平台获取天气预报的结果是:{}", get);
        if (StringUtils.isNotEmpty(get)) {
            JSONObject jsonObject = JSON.parseObject(get);
            if (!jsonObject.containsKey("cityid")) {
                return null;
            }
            return jsonObject;
        }
        return null;

    }

}
