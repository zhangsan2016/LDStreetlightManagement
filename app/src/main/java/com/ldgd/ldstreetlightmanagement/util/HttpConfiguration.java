package com.ldgd.ldstreetlightmanagement.util;

/**
 * Created by ldgd on 2019/9/22.
 * 功能：
 * 说明：http交互配置
 */

public class HttpConfiguration {

    public static String URL_BASE = "https://iot.sz-luoding.com:888/api/";
    // 登录地址
    public static String LOGIN_URl = URL_BASE + "user/login";
    // 获取项目列表地址
    public static String PROJECT_LIST_URL = URL_BASE + "project/list";

}
