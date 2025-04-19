package com.wu.transaction.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * MinIO服务接口
 */
public interface MinioService {

    /**
     * 上传商品图片
     *
     * @param file 上传的图片文件
     * @return 图片访问路径
     */
    String uploadProductImage(MultipartFile file);

    /**
     * 上传用户头像
     *
     * @param file 上传的头像文件
     * @return 头像访问路径
     */
    String uploadUserAvatar(MultipartFile file);

    /**
     * 删除文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件路径）
     * @return 是否删除成功
     */
    boolean deleteFile(String bucketName, String objectName);

    /**
     * 获取文件访问URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件路径）
     * @return 文件访问URL
     */
    String getFileUrl(String bucketName, String objectName);
    
    /**
     * 获取商品图片存储桶名称
     *
     * @return 商品图片存储桶名称
     */
    String getProductImageBucket();
    
    /**
     * 获取用户头像存储桶名称
     *
     * @return 用户头像存储桶名称
     */
    String getUserAvatarBucket();
} 