package com.terwergreen.confluence.xmlrpc.service.helper;

/**
 * 公共博客接口
 *
 * @name: BlogHelper
 * @author: terwer
 * @date: 2022-03-28 19:45
 **/

import java.util.List;
import java.util.Map;

/**
 * @author: terwer
 * @date: 2022/1/9 18:51
 * @description: BlogHelper
 */
public interface IBlogHelper {
    // ====================
    // bloggerApi开始
    // ====================
    Map<String, Object> getUsersBlogs();
    // ====================
    // bloggerApi结束
    // ====================

    // ====================
    // metaWeblogApi开始
    // ====================
    boolean newPost(Map<String, Object> mappedParams);

    boolean editPost(Map<String, Object> mappedParams);

    <T> T getPost(Map<String, Object> mappedParams);

    <T> List<T> getRecentPosts(Map<String, Object> mappedParams);

    <T> List<T> getCategories(Map<String, Object> mappedParams);

    boolean newMediaObject(Map<String, Object> mappedParams);
    // ====================
    // metaWeblogApi结束
    // ====================
}