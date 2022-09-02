package com.hank.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hank.common.utils.HttpUtil;
import com.hank.service.ISysConfigService;
import com.hank.service.MsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service("msgService")
@Slf4j
public class MsgServiceImpl implements MsgService {

    @Resource
    private ISysConfigService iSysConfigService;

    @Override
    public String getMsgFromThird() {
        String jitangKey = iSysConfigService.getValue("juhe_jitang_key");
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

    @Override
    public JSONObject getEnglish() {
        String show_id = iSysConfigService.getValue("showapi_appid");
        String show_secret = iSysConfigService.getValue("showapi_secret");
        if (StringUtils.isEmpty(show_id)) {
            return null;
        }
        if (StringUtils.isEmpty(show_secret)) {
            return null;
        }
        String get = HttpUtil.doGet("https://route.showapi.com/1211-1?" +
                "showapi_appid=" + show_id+
                "&showapi_sign="+show_secret);
        if (StringUtils.isNotEmpty(get)) {
            JSONObject jsonObject = JSON.parseObject(get);
            if (0 != jsonObject.getInteger("showapi_res_code")) {
                return null;
            }
            return jsonObject
                    .getJSONObject("showapi_res_body")
                    .getJSONArray("data")
                    .getJSONObject(0);
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
        String weatherKey = iSysConfigService.getValue("juhe_weather_key");
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
        String yikeappid = iSysConfigService.getValue("yike_appid");
        String yikeappsecret = iSysConfigService.getValue("yike_secret");
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

    @Override
    public JSONObject getWeatherFromThird3(String city) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(city)) {
            return null;
        }
        String get = HttpUtil.doGet("https://v0.yiketianqi.com/api?unescape=1&version=v91" +
                "&appid=43656176" +
                "&appsecret=I42og6Lm" +
                "&ext=&cityid=&city=" +
                "&city="+city);
        if (StringUtils.isNotEmpty(get)) {
            JSONObject jsonObject = JSON.parseObject(get);
            if (!jsonObject.containsKey("cityid")) {
                return null;
            }
            return jsonObject.getJSONArray("data").getJSONObject(0);
        }
        return null;
    }

}
