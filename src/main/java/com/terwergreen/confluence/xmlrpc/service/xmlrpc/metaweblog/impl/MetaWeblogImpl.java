package com.terwergreen.confluence.xmlrpc.service.xmlrpc.metaweblog.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.terwergreen.confluence.xmlrpc.ConfluenceXmlrpcApplication;
import com.terwergreen.confluence.xmlrpc.service.helper.impl.ConfluenceBlogHelper;
import com.terwergreen.confluence.xmlrpc.service.xmlrpc.metaweblog.IMetaWeblogApi;
import com.terwergreen.confluence.xmlrpc.utils.MWebUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.common.XmlRpcNotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import static com.terwergreen.confluence.xmlrpc.service.helper.impl.ConfluenceBlogHelper.CATEGORIES;
import static com.terwergreen.confluence.xmlrpc.service.helper.impl.ConfluenceBlogHelper.CONFLUENCE_DEFAULT_SPACE_KEY;
import static com.terwergreen.confluence.xmlrpc.service.helper.impl.ConfluenceBlogHelper.DATECREATED;
import static com.terwergreen.confluence.xmlrpc.service.helper.impl.ConfluenceBlogHelper.DESCRIPTION;
import static com.terwergreen.confluence.xmlrpc.service.helper.impl.ConfluenceBlogHelper.TITLE;

/**
 * metaWeblogApi的具体实现
 *
 * @name: MetaWeblogImpl
 * @author: terwer
 * @date: 2022-03-07 14:09
 **/
public class MetaWeblogImpl implements IMetaWeblogApi {
    private static final Logger logger = LoggerFactory.getLogger(MetaWeblogImpl.class);

    private static final ConfluenceBlogHelper blogHelper;
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

    public MetaWeblogImpl() {
        logger.info("容器中注册MetaWeblogImpl");
    }

    private void isValid(String username, String password) throws XmlRpcNotAuthorizedException {
        logger.info("username: {}, password: {}", username, password);
        boolean isValid = blogHelper.getUsername().equals(username) && blogHelper.getPassword().equals(password);
        logger.info("isValid = {}", isValid);
        if (!isValid) {
            throw new XmlRpcNotAuthorizedException("账号或密码有误");
        }
    }

    @Override
    public String newPost(String blogid, String username, String password, Map<String, Object> post, boolean publish) throws XmlRpcException {
        // logger.info("metaWeblog.newPost -> blogid: {}, post: {}, publish: {}", blogid, JSON.toJSONString(post), publish);
        logger.info("metaWeblog.newPost -> blogid: {}, publish: {}", blogid, publish);

        isValid(username, password);

        JSONObject postJson = JSONObject.parseObject(JSON.toJSONString(post));
        logger.debug("postJson = {}", postJson);

        String postId = "0";
        try {

            JSONArray catjsonArray = postJson.getJSONArray("categories");
            Vector<String> categories = new Vector<>();
            for (Object cat : catjsonArray) {
                String catStr = (String) cat;
                categories.add(catStr);
            }
            // 将标签作为新增分类
            String[] tags = postJson.getString("mt_keywords").split(",");
            if (tags.length > 0) {
                for (String tag : tags) {
                    categories.add(tag);
                }
            }

            // 转换为markdown
            String content = postJson.getString("description");
            String parsedMarkdown = MWebUtils.getParsedMarkdown(content);

            List<Object> pParams = new ArrayList<>();
            pParams.add(CONFLUENCE_DEFAULT_SPACE_KEY);
            pParams.add(username);
            pParams.add(password);
            Hashtable<String, Object> struct = new Hashtable<>();
            struct.put(TITLE, postJson.getString("title"));
            struct.put(DESCRIPTION, parsedMarkdown);
            struct.put(DATECREATED, postJson.getDate("dateCreated"));
            struct.put(CATEGORIES, categories);
            pParams.add(struct);// 文章信息
            pParams.add(true);// 是否发布
            String result = (String) blogHelper.executeMeteweblog("metaWeblog.newPost", pParams);
            if (result == null) {
                throw new XmlRpcException(String.format("标题为 %s 的博文已存在", struct.get(TITLE)));
            }
            postId = result;
            // logger.info("发布成功，result = " + result);

            logger.info("Confluence add Post:" + JSON.toJSONString(result));
        } catch (Exception e) {
            logger.error("接口异常", e);
        }

        return postId;
    }

    @Override
    public boolean editPost(String postid, String username, String password, Map<String, Object> post, boolean publish) throws XmlRpcException {
        // logger.info("metaWeblog.editPost -> postid: {}, post: {}", postid, JSON.toJSONString(post));
        logger.info("metaWeblog.editPost -> postid: {}", postid);

        boolean flag = false;
        try {

            JSONObject postJson = JSONObject.parseObject(JSON.toJSONString(post));
            logger.debug("postJson = {}", postJson);

            JSONArray catjsonArray = postJson.getJSONArray("categories");
            Vector<String> categories = new Vector<>();
            for (Object cat : catjsonArray) {
                String catStr = (String) cat;
                categories.add(catStr);
            }
            // 将标签作为新增分类
            String[] tags = postJson.getString("mt_keywords").split(",");
            if (tags.length > 0) {
                for (String tag : tags) {
                    categories.add(tag);
                }
            }

            // 转换为markdown
            String content = postJson.getString("description");
            String parsedMarkdown = MWebUtils.getParsedMarkdown(content);
            logger.warn("parsedMarkdown=>" + parsedMarkdown);

            List<Object> pParams = new ArrayList<>();
            pParams.add(postid);
            pParams.add(username);
            pParams.add(password);
            Hashtable<String, Object> struct = new Hashtable<>();
            struct.put(TITLE, postJson.getString("title"));
            struct.put(DESCRIPTION, parsedMarkdown);
            struct.put(DATECREATED, postJson.getDate("dateCreated"));
            struct.put(CATEGORIES, categories);
            pParams.add(struct);// 文章信息
            pParams.add(true);// 是否发布
            boolean result = (boolean) blogHelper.executeMeteweblog("metaWeblog.editPost", pParams);
            if (!result) {
                throw new XmlRpcException(String.format("ID为 %s 的博文不存在", struct.get(TITLE)));
            }
            // logger.info("result = " + JSON.toJSONString(result));

            flag = result;
            logger.info("Confluence update Post：" + JSON.toJSONString(result));
            flag = true;
        } catch (Exception e) {
            logger.error("接口异常", e);
        }

        return flag;
    }

    @Override
    public Map<String, Object> getPost(String postid, String username, String password) throws XmlRpcException {
        logger.info("metaWeblog.getPost -> postid: {}", postid);

        isValid(username, password);

        Map<String, Object> post = new HashMap<>();
        try {
            List<Object> pParams = new ArrayList<>();
            pParams.add(postid);
            pParams.add(username);
            pParams.add(password);
            post = (Map<String, Object>) blogHelper.executeMeteweblog("metaWeblog.getPost", pParams);
        } catch (Exception e) {
            e.printStackTrace();
            throw new XmlRpcException(500, e.getMessage());
        }

        return post;
    }

    @Override
    public List<Map<String, String>> getCategories(String blogid, String username, String password) throws XmlRpcException {
        logger.info("metaWeblog.getCategories -> blogid: {}", blogid);

        isValid(username, password);

        List<Object> pParams = new ArrayList<>();
        pParams.add(CONFLUENCE_DEFAULT_SPACE_KEY);
        pParams.add(username);
        pParams.add(password);
        Map<String, Object> categories = (Map<String, Object>) blogHelper.executeMeteweblog("metaWeblog.getCategories", pParams);

        List<Map<String, String>> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry : categories.entrySet()) {
            Map<String, String> cat = new HashMap<>();
            cat.put("title", entry.getKey());
            Map<String, String> value = (Map<String, String>) entry.getValue();
            cat.putAll(value);
            result.add(cat);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getRecentPosts(String blogid, String username, String password, int numberOfPosts) throws XmlRpcException {
        logger.info("metaWeblog.getRecentPosts -> blogid: {}, numberOfPosts: {}", blogid, numberOfPosts);

        List<Object> pParams = new ArrayList<>();
        pParams.add(CONFLUENCE_DEFAULT_SPACE_KEY);
        pParams.add(username);
        pParams.add(password);
        pParams.add(10);
        Object[] dataList = (Object[]) blogHelper.executeMeteweblog("metaWeblog.getRecentPosts", pParams);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object obj : dataList) {
            Map<String, Object> data = (Map<String, Object>) obj;
            result.add(data);
        }

        return result;
    }

    @Override
    public Map<String, String> newMediaObject(String blogid, String username, String password, Map<String, Object> post) throws XmlRpcException {
        /*
        logger.info("metaWeblog.newMediaObject -> blogid: {}", blogid);

        isValid(username, password);

        Map<String, String> urlData = new HashMap<>();

        try {
            String retUrl = "http://oss.terwergreen.com/%s";
            String name = post.get("name").toString();
            //  {year}/{mon}/{day}/{filename}{.suffix}
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String forder = sdf.format(now);
            System.out.println("forder = " + forder);
            String fileName = "bugucms/" + forder + "/" + name;
            String url = String.format(retUrl, fileName);

            byte[] bits = (byte[]) post.get("bits");
            logger.info("准备上传图片，url = " + url);
            // 开始上传图片
            OssManager manager = OssManager.getInstance();
            manager.upload(fileName, bits);

            // 水印
            // String watermark = String.format("?x-oss-process=%s", "image/auto-orient,1/quality,q_90/format,jpg/watermark,image_YnVndWNtcy9sb2dvLWRhcmsucG5nP3gtb3NzLXByb2Nlc3M9aW1hZ2UvcmVzaXplLFBfNjI,g_se,x_10,y_10");
            // String markedUrl = url + watermark;

            urlData.put("url", url);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("图片上传错误", e);
        }

        logger.info("urlData = {}", urlData);
        return urlData;
        */
        throw new RuntimeException("未实现此API");
    }
}
