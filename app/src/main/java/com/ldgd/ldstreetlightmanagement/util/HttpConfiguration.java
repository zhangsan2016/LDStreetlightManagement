package com.ldgd.ldstreetlightmanagement.util;

/**
 * Created by ldgd on 2019/9/22.
 * 功能：
 * 说明：http交互配置
 */

public class HttpConfiguration {
    // content-type 用户登录
    public  static  String CONTENT_TYPE_USER_LOGIN = "user/login";
    // content-type 项目列表
    public static String  CONTENT_TYPE_PROJECT_LIST = "project/list";
    // content-type 电箱路灯列表
    public static String  CONTENT_TYPE_DEVICE_LAMP_LIST = "v_device_lamp/list";



    private static String URL_BASE = "https://iot.sz-luoding.com:888/api/";
    // 登录地址
    public static String LOGIN_URl = URL_BASE + CONTENT_TYPE_USER_LOGIN;
    // 获取项目列表地址
    public static String PROJECT_LIST_URL = URL_BASE + CONTENT_TYPE_PROJECT_LIST;
    // 获取项目下路灯地址
    public static String DEVICE_LAMP_LIST_URL = URL_BASE + CONTENT_TYPE_DEVICE_LAMP_LIST;

}
