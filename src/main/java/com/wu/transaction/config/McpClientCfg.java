package com.wu.transaction.config;

import io.modelcontextprotocol.client.McpClient;
import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
public class McpClientCfg implements McpSyncClientCustomizer {


    @Override
    public void customize(String name, McpClient.SyncSpec spec) {
        // do nothing
        // 设置请求的超时时间为60秒
        spec.requestTimeout(Duration.ofSeconds(60));
    }
}
