package com.wu.transaction.service.impl;

import com.wu.transaction.config.MinioConfig;
import com.wu.transaction.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO服务实现类
 */
@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 上传商品图片
     *
     * @param file 上传的图片文件
     * @return 图片访问路径
     */
    @Override
    public String uploadProductImage(MultipartFile file) {
        return uploadFile(getProductImageBucket(), file);
    }

    /**
     * 上传用户头像
     *
     * @param file 上传的头像文件
     * @return 头像访问路径
     */
    @Override
    public String uploadUserAvatar(MultipartFile file) {
        return uploadFile(getUserAvatarBucket(), file);
    }
    
    /**
     * 获取商品图片存储桶名称
     *
     * @return 商品图片存储桶名称
     */
    @Override
    public String getProductImageBucket() {
        return minioConfig.getBucket().getProductImages();
    }
    
    /**
     * 获取用户头像存储桶名称
     *
     * @return 用户头像存储桶名称
     */
    @Override
    public String getUserAvatarBucket() {
        return minioConfig.getBucket().getUserAvatars();
    }

    /**
     * 上传文件到MinIO
     *
     * @param bucketName 存储桶名称
     * @param file 上传的文件
     * @return 文件访问路径
     */
    private String uploadFile(String bucketName, MultipartFile file) {
        try {
            // 检查存储桶是否存在，不存在则创建
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("创建存储桶：{}", bucketName);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 按日期进行文件夹划分
            String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String objectName = dateFolder + "/" + UUID.randomUUID() + suffix;

            // 上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build());

            // 返回文件路径
            return objectName;
        } catch (Exception e) {
            log.error("上传文件到MinIO失败", e);
            throw new RuntimeException("上传文件失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件路径）
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return true;
        } catch (Exception e) {
            log.error("从MinIO删除文件失败", e);
            return false;
        }
    }

    /**
     * 获取文件访问URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件路径）
     * @return 文件访问URL
     */
    @Override
    public String getFileUrl(String bucketName, String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(7, TimeUnit.DAYS) // URL有效期7天
                            .build());
        } catch (Exception e) {
            log.error("获取MinIO文件URL失败", e);
            throw new RuntimeException("获取文件URL失败：" + e.getMessage());
        }
    }
} 