package com.terwergreen.confluence.xmlrpc.service.xmlrpc.blogger.impl;

import com.terwergreen.confluence.xmlrpc.ConfluenceXmlrpcApplication;
import com.terwergreen.confluence.xmlrpc.service.helper.IBlogHelper;
import com.terwergreen.confluence.xmlrpc.service.helper.impl.ConfluenceBlogHelper;
import com.terwergreen.confluence.xmlrpc.service.xmlrpc.blogger.IBloggerApi;
import com.terwergreen.confluence.xmlrpc.service.xmlrpc.metaweblog.impl.MetaWeblogImpl;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.terwergreen.confluence.xmlrpc.service.helper.impl.ConfluenceBlogHelper.CONFLUENCE_DEFAULT_SPACE_KEY;

/**
 * bloggerAPI
 *
 * @name: BloggerImpl
 * @author: terwer
 * @date: 2022-03-28 21:15
 **/
public class BloggerImpl implements IBloggerApi {
    private static final Logger logger = LoggerFactory.getLogger(MetaWeblogImpl.class);

    private static final IBlogHelper blogHelper;
    private static Properties blogProps = new Properties();

    static {
        InputStream blogPropsStream = ConfluenceXmlrpcApplication.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            blogProps.load(blogPropsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        blogHelper = new ConfluenceBlogHelper((String) blogProps.get("blog.meteweblog.confluence.serverUrl"), (String) blogProps.get("blog.meteweblog.confluence.username"), (String) blogProps.get("blog.meteweblog.confluence.password"));
    }

    public List<Map<String, Object>> getUsersBlogs(String appKey, String username, String password) throws XmlRpcException {
        logger.info("[blogger.getUsersBlogs] -> appKey: {}, username: {}, password: {}", appKey, username, password);

        Map<String, Object> userBlog = blogHelper.getUsersBlogs();
        String blogid = (String) userBlog.get("blogid");
        String newblogid = CONFLUENCE_DEFAULT_SPACE_KEY;
        String url = ((String) userBlog.get("url"));
        String newurl = url.replace(blogid, newblogid);
        String newblogName = "Confluence知识库博文";

        List<Map<String, Object>> usersBlogs = new ArrayList<>();
        Map<String, Object> blogInfo = new HashMap<>();
        blogInfo.put("blogid", newblogid);
        blogInfo.put("url", newurl);
        blogInfo.put("blogName", newblogName);
        usersBlogs.add(blogInfo);

        return usersBlogs;
    }
}
