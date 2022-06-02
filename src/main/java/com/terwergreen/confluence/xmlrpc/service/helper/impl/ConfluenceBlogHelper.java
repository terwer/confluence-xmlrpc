package com.terwergreen.confluence.xmlrpc.service.helper.impl;

import com.alibaba.fastjson.JSON;
import com.terwergreen.confluence.xmlrpc.service.helper.IBlogHelper;
import com.terwergreen.confluence.xmlrpc.utils.ResourceUtil;
import com.terwergreen.confluence.xmlrpc.utils.SystemUtil;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Confluence的metaWeblogApi实现
 *
 * @name: ConfluenceBlogHelper
 * @author: terwer
 * @date: 2022-03-28 19:46
 **/
@Service
public class ConfluenceBlogHelper implements IBlogHelper {
    private static Logger logger = LoggerFactory.getLogger(ConfluenceBlogHelper.class);

    public static final String CONFLUENCE_DEFAULT_SPACE_KEY = "spc";

    public static final String CONFLUENCE_POST_FILE_HASH = "filehash";
    public static final String CONFLUENCE_POST_POST_ID = "postId";
    public static final String CONFLUENCE_POST_CATEGORIES = "categories";

    // Static struct fields
    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String PERMALINK = "permaLink";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORIES = "categories";
    public static final String AUTHOR = "author";
    public static final String PUBDATE = "pubDate";
    public static final String DATECREATED = "dateCreated";
    public static final String HTMLURL = "htmlUrl";
    public static final String RSSURL = "rssUrl";
    public static final String POSTID = "postid";
    public static final String BLOGID = "blogid";

    private String serverUrl;

    private String username;

    private String password;

    public String getServerUrl() {
        return serverUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ConfluenceBlogHelper() {
    }

    public ConfluenceBlogHelper(String serverUrl, String username, String password) {
        this.serverUrl = serverUrl;
        this.username = username;
        this.password = password;
    }

    // ====================
    // 通用api开始
    // ====================
    public Object executeMeteweblog(String pMethodName, List pParams) {
        Object result = null;
        try {
            // Create an object to represent our server.
            XmlRpcClient client = new XmlRpcClient();
            XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
            URL url = new URL(serverUrl);
            clientConfig.setServerURL(url);
            client.setConfig(clientConfig);

            // Call the server, and get our result.
            result = client.execute(pMethodName, pParams);
        } catch (XmlRpcException exception) {
            logger.error("JavaClient: XML-RPC Fault #" +
                    Integer.toString(exception.code) + ": " +
                    exception.toString());
        } catch (Exception exception) {
            logger.error("JavaClient: " + exception.toString());
        }
        return result;
    }

    protected void executeMeteweblogAsync(String pMethodName, List pParams, AsyncCallback pCallback) {
        Object result = null;
        try {
            // Create an object to represent our server.
            XmlRpcClient client = new XmlRpcClient();
            XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
            URL url = new URL(serverUrl);
            clientConfig.setServerURL(url);
            client.setConfig(clientConfig);

            // Call the server, and get our result.
            client.executeAsync(pMethodName, pParams, pCallback);
        } catch (XmlRpcException exception) {
            logger.error("JavaClient: XML-RPC Fault #" +
                    Integer.toString(exception.code) + ": " +
                    exception.toString());
        } catch (Exception exception) {
            logger.error("JavaClient: " + exception.toString());
        }
    }
    // ====================
    // 通用api结束
    // ====================

    // ====================
    // bloggerApi开始
    // ====================
    public Map<String, Object> getUsersBlogs() {
        List<String> pParams = new ArrayList<>();
        pParams.add("default");
        pParams.add(this.username);
        pParams.add(this.password);

        Object[] result = (Object[]) this.executeMeteweblog("blogger.getUsersBlogs", pParams);

        HashMap<String, Object> userBlog = new HashMap<>();
        if (result != null && result.length > 0) {
            userBlog = (HashMap<String, Object>) result[0];
        }

        logger.debug("blogger.getUsersBlogs=>");
        return userBlog;
    }
    // ====================
    // bloggerApi结束
    // ====================

    // ====================
    // metaWeblogApi开始
    // ====================

    /**
     * 参数：包含下列key值的Map
     * CONFLUENCE_POST_FILE_HASH
     * CONFLUENCE_POST_POST_ID
     * CONFLUENCE_POST_CATEGORIES
     * CONFLUENCE_BLOG_TYPE
     *
     * @param mappedParams
     * @return
     */
    @Override
    public boolean newPost(Map<String, Object> mappedParams) {
        boolean flag = false;
        try {
            long filehash = (long) mappedParams.get(CONFLUENCE_POST_FILE_HASH);
            String postId = (String) mappedParams.get(CONFLUENCE_POST_POST_ID);
            Vector<String> categories = (Vector<String>) mappedParams.get(CONFLUENCE_POST_CATEGORIES);

            // 正文
            String basepath = "C:/Users/terwer/Documents/share/";
            if (SystemUtil.isLinux()) {
                basepath = "/Users/terwer/Documents/share/";
            }
            String postPath = basepath + "cross/MWeb/MWebLibrary/docs/" + filehash + ".md";
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(postPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String content = ResourceUtil.readStream(inputStream);
            // logger.info("content = " + content);

            // 文章标题
            String postTitle = content.split("")[0];
            logger.info("postTitle = " + postTitle);

            // 转换为markdown
            StringBuilder sb = new StringBuilder();
            sb.append("<ac:structured-macro ac:name=\"markdown\" ac:schema-version=\"1\" ac:macro-id=\"529e4807-3a3b-401e-a337-8cccc762b3fe\"><ac:plain-text-body><![CDATA[");
            sb.append(content);
            sb.append("]]></ac:plain-text-body></ac:structured-macro>");
            String parsedMarkdown = sb.toString();

            List<Object> pParams = new ArrayList<>();
            pParams.add("ds");
            pParams.add(this.username);
            pParams.add(this.password);
            Hashtable<String, Object> struct = new Hashtable<>();
            struct.put(TITLE, postTitle);
            struct.put(DESCRIPTION, parsedMarkdown);
            struct.put(DATECREATED, new Date());
            struct.put(CATEGORIES, categories);
            pParams.add(struct);// 文章信息
            pParams.add(true);// 是否发布
            Object result = this.executeMeteweblog("metaWeblog.newPost", pParams);

            logger.debug("Confluence add Post:" + JSON.toJSONString(result));
            flag = true;
        } catch (Exception e) {
            logger.error("接口异常", e);
        }

        return flag;
    }

    /**
     * 参数：包含下列key值的Map
     * CONFLUENCE_POST_FILE_HASH
     * CONFLUENCE_POST_POST_ID
     * CONFLUENCE_POST_CATEGORIES
     * CONFLUENCE_BLOG_TYPE
     *
     * @param mappedParams
     * @return
     */
    @Override
    public boolean editPost(Map<String, Object> mappedParams) {
        boolean flag = false;
        try {
            long filehash = (long) mappedParams.get(CONFLUENCE_POST_FILE_HASH);
            String postId = (String) mappedParams.get(CONFLUENCE_POST_POST_ID);
            Vector<String> categories = (Vector<String>) mappedParams.get(CONFLUENCE_POST_CATEGORIES);

            // 正文
            String basepath = "C:/Users/terwer/Documents/share/";
            if (SystemUtil.isLinux()) {
                basepath = "/Users/terwer/Documents/share/";
            }
            String postPath = basepath + "cross/MWeb/MWebLibrary/docs/" + filehash + ".md";
            FileInputStream inputStream = null;
            inputStream = new FileInputStream(postPath);
            String content = ResourceUtil.readStream(inputStream);
            // logger.info("content = " + content);

            // 文章标题
            String postTitle = content.split("\n")[0].replace("# ", "");
            logger.info("postTitle = " + postTitle);

            // 转换为markdown
            StringBuilder sb = new StringBuilder();
            sb.append("<ac:structured-macro ac:name=\"markdown\" ac:schema-version=\"1\" ac:macro-id=\"529e4807-3a3b-401e-a337-8cccc762b3fe\"><ac:plain-text-body><![CDATA[");
            sb.append(content);
            sb.append("]]></ac:plain-text-body></ac:structured-macro>");
            String parsedMarkdown = sb.toString();

            List<Object> pParams = new ArrayList<>();
            pParams.add(postId);
            pParams.add(this.username);
            pParams.add(this.password);
            Hashtable<String, Object> struct = new Hashtable<>();
            struct.put(TITLE, postTitle);
            struct.put(DESCRIPTION, parsedMarkdown);
            struct.put(DATECREATED, new Date());
            struct.put(CATEGORIES, categories);
            pParams.add(struct);// 文章信息
            pParams.add(true);// 是否发布
            Object result = this.executeMeteweblog("metaWeblog.editPost", pParams);
            logger.info("result = " + JSON.toJSONString(result));

            logger.debug("Confluence update Post");
            flag = true;
        } catch (Exception e) {
            logger.error("接口异常", e);
        }

        return flag;
    }

    @Override
    public <T> T getPost(Map<String, Object> mappedParams) {
        List<Object> pParams = new ArrayList<>();
        pParams.add("7241730");
        pParams.add(this.username);
        pParams.add(this.password);
        T result = (T) this.executeMeteweblog("metaWeblog.getPost", pParams);
        return result;
    }

    public <T> List<T> getRecentPosts_blogger(Map<String, Object> mappedParams) {
        List<Object> pParams = new ArrayList<>();
        pParams.add("default");
        pParams.add("ds");
        pParams.add(this.username);
        pParams.add(this.password);
        pParams.add(10);
        List<T> result = (List<T>) this.executeMeteweblog("blogger.getRecentPosts", pParams);
        return result;
    }

    @Override
    public <T> List<T> getRecentPosts(Map<String, Object> mappedParams) {
        List<Object> pParams = new ArrayList<>();
        pParams.add("spc");
        pParams.add(this.username);
        pParams.add(this.password);
        pParams.add(10);
        Object[] dataList = (Object[]) this.executeMeteweblog("metaWeblog.getRecentPosts", pParams);
        List<T> result = new ArrayList<>();
        for (Object obj : dataList) {
            T data = (T) obj;
            result.add(data);
        }
        return result;
    }

    @Override
    public <T> List<T> getCategories(Map<String, Object> mappedParams) {
        List<Object> pParams = new ArrayList<>();
        pParams.add("ds");
        pParams.add(this.username);
        pParams.add(this.password);
        Object result = this.executeMeteweblog("metaWeblog.getCategories", pParams);
        return null;
    }

    @Override
    public boolean newMediaObject(Map<String, Object> pParams) {
        throw new RuntimeException("此方法未实现");
    }
    // ====================
    // metaWeblogApi结束
    // ====================
}
