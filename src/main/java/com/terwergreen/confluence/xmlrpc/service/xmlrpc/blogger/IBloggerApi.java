package com.terwergreen.confluence.xmlrpc.service.xmlrpc.blogger;

import org.apache.xmlrpc.XmlRpcException;

import java.util.List;
import java.util.Map;

/**
 * bloggerAPI
 *
 * @name: IBloggerApi
 * @author: terwer
 * @date: 2022-03-28 21:19
 **/
public interface IBloggerApi {
    /**
     * 获取博客信息：blogger.getUsersBlogs
     * @param appKey
     * @param username
     * @param password
     * @return
     * @throws XmlRpcException
     */
    List<Map<String, Object>> getUsersBlogs(String appKey, String username, String password) throws XmlRpcException;

}
