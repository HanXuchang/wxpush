package com.hank.service;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;

public interface MsgService {
    String getMsgFromThird();

    JSONObject getEnglish() ;

    JSONObject getWeatherFromThird(String city) throws UnsupportedEncodingException;

    JSONObject getWeatherFromThird2(String city) throws UnsupportedEncodingException;

    JSONObject getWeatherFromThird3(String city) throws UnsupportedEncodingException;

}
