package com.terwergreen.confluence.xmlrpc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * MWeb工具类
 */
public class MWebUtils {
    private static final Logger logger = LoggerFactory.getLogger(MWebUtils.class);

    /**
     * 移除MWeb正文中的标题
     *
     * @param content
     * @return
     */
    private static String removeTitleFromContent(String content) {

        // 去除标题
        String[] carr = content.split("\n\n");
        String titleline = null;
        int i = 0;
        for (String cstr : carr) {
            if (!StringUtils.isEmpty(cstr)) {
                i++;
            }

            if (i == 1 && cstr.startsWith("#")) {
                titleline = cstr;
                logger.warn("标题行，忽略");
                break;
            }
        }

        return content.replace(titleline, "").trim();
    }

    /**
     * 组装Confluence需要得出Markdown格式
     *
     * @param content
     * @return
     */
    public static String getParsedMarkdown(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ac:structured-macro ac:name=\"markdown\" ac:schema-version=\"1\" ac:macro-id=\"529e4807-3a3b-401e-a337-8cccc762b3fe\"><ac:plain-text-body><![CDATA[");
        sb.append(MWebUtils.removeTitleFromContent(content));
        sb.append("]]></ac:plain-text-body></ac:structured-macro>");
        return sb.toString();
    }
}
