package com.wu.transaction;


import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AmapMcpToolsTest {

    @Autowired
    private List<McpSyncClient> mcpSyncClients;

    @Autowired
    private ChatClient chatClient;

    @Autowired(required = false)
    private ToolCallbackProvider toolCallbackProvider;

    @Test
    @DisplayName("测试高德地图MCP客户端配置")
    public void testAmapMcpClientConfiguration() {
        // 验证MCP客户端存在
        assertFalse(mcpSyncClients.isEmpty(), "应该至少有一个MCP客户端");

        // 输出客户端信息
        System.out.println("=== MCP客户端列表 ===");
        for (McpSyncClient client : mcpSyncClients) {
            System.out.println("客户端: " + client.listTools());
        }


    }

    }