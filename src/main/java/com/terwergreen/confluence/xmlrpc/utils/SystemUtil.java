package com.terwergreen.confluence.xmlrpc.utils;

/**
 * 系统通用工具类
 *
 * @name: SystemUtil
 * @author: terwer
 * @date: 2022-03-28 11:09
 **/
public class SystemUtil {
    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux") ||
                System.getProperty("os.name").toLowerCase().contains("mac");
    }
}
