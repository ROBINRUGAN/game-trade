package com.game_trade.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
public class OSSUtil {
    private static final String END_POINT =ConstantOssPropertiesUtils.END_POINT;
    private static final String ACCESS_KEY_ID = ConstantOssPropertiesUtils.ACCESS_KEY_ID;
    private static final String ACCESS_KEY_SECRET = ConstantOssPropertiesUtils.ACCESS_KEY_SECRET;
    private static final String BUCKET_NAME = ConstantOssPropertiesUtils.BUCKET_NAME;
    private static final String URL_PREFIX = ConstantOssPropertiesUtils.URL_PREFIX;
    //OSS上的文件夹名
    private static final String FILE_HOST = "game-trade";

    /**
     * 删除单个图片
     *
     * @param url
     */
    public static void deleteImg(String url) {
        OSS ossClient = new OSSClientBuilder().build(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        //获取文件名在URL中的下标
        int index = url.lastIndexOf("/") + 1;
        //得到文件名
        String fileName = FILE_HOST +"/"+ url.substring(index);
        ossClient.deleteObject(BUCKET_NAME, fileName);
        log.info(fileName);
        ossClient.shutdown();
    }


    /**
     * 上传多文件
     *
     * @param files
     * @return
     */
    public static String uploadFiles(MultipartFile[] files) throws IOException {
        List<String> urlList = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                // 上传地址
                OSSUtil ossUtil = new OSSUtil();
                String url = uploadFile(file);
                urlList.add(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.join(urlList, ">");
    }
    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String uploadFile(MultipartFile file) throws IOException {
        try {
            // 原始文件名称，如a.png
            String originalFilename = file.getOriginalFilename();
            // 唯一的文件名称
            String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");
            InputStream inputStream = file.getInputStream();
            OSSUtil.uploadFileToOSS(inputStream, fileName);
            String url = "https://" + URL_PREFIX + "/" + FILE_HOST + "/" + fileName;
            return url;
        } catch (Exception e) {
            throw new IOException("文件上传失败");
        }
    }

    /**
     * 上传到OSS服务器 如果同名文件会覆盖服务器上的
     *
     * @param instream 文件流
     * @param fileName 文件名称 包括后缀名
     * @return 出错返回"" ,唯一MD5数字签名
     */
    public static String uploadFileToOSS(InputStream instream, String fileName) {
        String ret = "";
        try {
            OSS ossClient = new OSSClientBuilder().build(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            // 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(instream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(getcontentType(fileName.substring(fileName.lastIndexOf("."))));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            // 上传文件
            PutObjectResult putResult = ossClient.putObject(BUCKET_NAME, FILE_HOST + "/" +fileName, instream, objectMetadata);
            ret = putResult.getETag();

        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (instream != null) {
                    instream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Description: 判断OSS服务文件上传时文件的contentType
     */
    public static String getcontentType(String filenameExtension) {
        if (filenameExtension.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        }
        if (filenameExtension.equalsIgnoreCase("gif")) {
            return "image/gif";
        }
        if (filenameExtension.equalsIgnoreCase("jpeg") || filenameExtension.equalsIgnoreCase("jpg")
                || filenameExtension.equalsIgnoreCase("png")) {
            return "image/jpeg";
        }
        if (filenameExtension.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (filenameExtension.equalsIgnoreCase("txt")) {
            return "text/plain";
        }
        if (filenameExtension.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        }
        if (filenameExtension.equalsIgnoreCase("pptx") || filenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (filenameExtension.equalsIgnoreCase("docx") || filenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (filenameExtension.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        return "image/jpg";
    }

    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public String getUrl(String key) {
        // 设置URL过期时间为10年 3600l* 1000*24*365*10
        OSS ossClient = new OSSClientBuilder().build(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(BUCKET_NAME, key, expiration);
        // url = "https://" + bucketName + ".oss-cn-beijing.aliyuncs.com/" + bucketName+"/"+ key;
        if (url != null) {
            String host = "https://" + url.getHost() + url.getPath();
            return host;
        }
        return "";
    }


}
