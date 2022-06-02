package com.terwergreen.confluence.xmlrpc.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * @author: terwer
 * @date: 2022/1/10 16:31
 * @description: ResourceUtil
 */
public class ResourceUtil {
    /**
     * 从资源文件读取
     *
     * @param fileName
     * @return 支持IDEA, unit test 和 JAR 文件
     */
    public static InputStream getResourceAsStream(String fileName) {

        // 类加载器
        ClassLoader classLoader = ResourceUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // 输入流
        if (inputStream == null) {
            throw new IllegalArgumentException("文件不存在! " + fileName);
        } else {
            return inputStream;
        }
    }

    /**
     * 从资源文件读取路径
     *
     * @param fileName
     * @return
     */
    public static String getResource(String fileName) {
        // 类加载器
        ClassLoader classLoader = ResourceUtil.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return resource.toString();
        }
    }

    /**
     * 从资源文件读取路径
     *
     * @param fileName
     * @return
     */
    public static URL getResourceAsURL(String fileName) {
        // 类加载器
        ClassLoader classLoader = ResourceUtil.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return resource;
        }
    }

    /**
     * 读取文件为File对象，不支持JAR
     * 如果从JAR读取Unix会抛异常NoSuchFileException，Window会抛异常InvalidPathException
     *
     * @param fileName 示例：file:java-io.jar!/json/file1.json
     * @return
     * @throws URISyntaxException
     */
    public File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            // return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }

    public static String readStream(InputStream is) {
        StringBuilder sb = new StringBuilder(512);
        try {
            Reader r = new InputStreamReader(is, "UTF-8");
            int c = 0;
            while ((c = r.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    /**
     * 输出输入流
     *
     * @param is 输入流
     */
    private static void printInputStream(InputStream is) {
        try (InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 输出文件为为本
     *
     * @param file 文件
     */
    private static void printFile(File file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String fileName = "editor.html";

        // System.out.println("getResourceAsStream : " + fileName);
        // InputStream is = ResourceUtil.getResourceAsStream(fileName);
        // printInputStream(is);

        String resource = ResourceUtil.getResource(fileName);
        System.out.println("resource = " + resource);
    }
}