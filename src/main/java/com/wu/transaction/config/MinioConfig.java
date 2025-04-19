package com.wu.transaction.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * MinIO服务地址
     */
    private String endpoint;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 私有密钥
     */
    private String secretKey;

    /**
     * 桶配置
     */
    private Bucket bucket;

    @Data
    public static class Bucket {
        /**
         * 商品图片存储桶
         */
        private String productImages;

        /**
         * 用户头像存储桶
         */
        private String userAvatars;
    }

    /**
     * 初始化MinIO客户端
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
} 