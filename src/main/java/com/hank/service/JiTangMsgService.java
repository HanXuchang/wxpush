package com.hank.service;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;

public interface JiTangMsgService {
    String getMsgFromThird();

    JSONObject getWeatherFromThird(String city) throws UnsupportedEncodingException;

    JSONObject getWeatherFromThird2(String city) throws UnsupportedEncodingException;

}
