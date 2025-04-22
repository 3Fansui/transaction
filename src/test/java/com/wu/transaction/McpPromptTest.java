package com.wu.transaction;

import com.wu.transaction.model.AlibabaOpenAiChatModel;
import io.modelcontextprotocol.client.McpSyncClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class McpPromptTest {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    @Autowired
    private AlibabaOpenAiChatModel aiChatModel;


    @Test
    @DisplayName("测试基本地理位置查询")
    public void testBasicLocationQuery() {
        // 验证工具回调提供者存在
        assertNotNull(toolCallbackProvider, "ToolCallbackProvider 应该存在");

        // 获取工具回调
        ToolCallback[] callbacks = (ToolCallback[]) toolCallbackProvider.getToolCallbacks();
        assertTrue(callbacks.length > 0, "应该至少有一个工具回调");

        // 打印可用工具信息
        System.out.println("===== 可用MCP工具 =====");
        for (ToolCallback callback : callbacks) {
            System.out.println("工具名称: " + callback.getToolDefinition().name());
            System.out.println("工具描述: " + callback.getToolDefinition().description());
            System.out.println("输入Schema: " + callback.getToolDefinition().inputSchema());
            System.out.println("----------------------");
        }

        // 创建系统提示，指导AI使用工具
        String systemPromptText = """
                你是一个智能地图助手，可以帮助用户查询地点、路线和地理信息。
                当用户询问地点或地理信息时，请使用可用的工具来获取准确信息。
                回答用中文返回，保持友好和专业的语气。
                尽可能提供完整的信息，包括地址、坐标和相关详情。
                """;
        SystemMessage systemMessage = new SystemMessage(systemPromptText);

        // 用户问题
        String userQuestion = "请告诉我上海东方明珠的详细位置信息";
        UserMessage userMessage = new UserMessage(userQuestion);

        // 创建提示并设置工具回调
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        // 调用AI
        try {
            System.out.println("===== 发送提示 =====");
            System.out.println("用户问题: " + userQuestion);

            // 构建具有工具回调的ChatClient
            ChatClient clientWithTools = ChatClient.builder(aiChatModel)
                    .defaultTools(callbacks)
                    .build();

            // 调用API
            ChatResponse response = clientWithTools
                    .prompt()
                    .user(userQuestion)
                    .system(systemPromptText)
                    .call()
                    .chatResponse();

            // 输出结果
            System.out.println("===== AI响应 =====");
            String content = response.getResult().getOutput().getText();
            System.out.println(content);

            // 验证结果
            assertNotNull(content, "AI响应不应为空");
            assertTrue(content.length() > 0, "响应内容不应为空");

            // 检查工具使用情况
            if (response.getMetadata() != null) {
                System.out.println("===== 元数据 =====");
                System.out.println(response.getMetadata());
            }
        } catch (Exception e) {
            System.out.println("测试失败: " + e.getMessage());
            e.printStackTrace();
            fail("AI调用应该成功");
        }
    }
    
   /* @Test
    @DisplayName("测试复杂路径规划查询")
    public void testRouteQuery() {
        // 获取工具回调
        ToolCallback[] callbacks = (ToolCallback[])toolCallbackProvider.getToolCallbacks();
        
        // 系统提示，让AI使用路径规划工具
        String systemPromptText = """
            你是一个智能出行助手，专长于路径规划和交通建议。
            当用户询问如何从一个地点到另一个地点时，请利用可用的工具提供最佳路线。
            提供多种交通方式的选择，包括驾车、公交、步行等。
            对于每种交通方式，提供预计时间、距离和简要的路线描述。
            回答应当简洁明了，使用中文，并保持友好专业的语气。
            """;
        SystemMessage systemMessage = new SystemMessage(systemPromptText);
        
        // 用户问题 - 路径规划查询
        String userQuestion = "我想从北京故宫到北京南站，有哪些交通方式可选？请给我详细的路线建议。";
        UserMessage userMessage = new UserMessage(userQuestion);
        
        // 创建提示并设置工具回调
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
                
        // 调用AI
        try {
            System.out.println("===== 发送路径规划提示 =====");
            System.out.println("用户问题: " + userQuestion);
            
            // 构建具有工具回调的ChatClient
            ChatClient clientWithTools = chatClient.builder()
                    .defaultTools(callbacks)
                    .build();
            
            // 调用API
            ChatResponse response = clientWithTools.call(prompt);
            
            // 输出结果
            System.out.println("===== AI响应 =====");
            String content = response.getResult().getOutput().getText();
            System.out.println(content);
            
            // 验证结果
            assertNotNull(content, "AI响应不应为空");
            assertTrue(content.length() > 0, "响应内容不应为空");
            
            // 检查工具使用情况
            if (response.getMetadata() != null) {
                System.out.println("===== 元数据 =====");
                System.out.println(response.getMetadata());
            }
        } catch (Exception e) {
            System.out.println("测试失败: " + e.getMessage());
            e.printStackTrace();
            fail("AI调用应该成功");
        }
    }
    
    @Test
    @DisplayName("测试多轮对话与MCP工具交互")
    public void testConversationalMcpInteraction() {
        // 获取工具回调
        ToolCallback[] callbacks = (ToolCallback[])toolCallbackProvider.getToolCallbacks();
        
        // 创建系统提示模板，使用变量替换
        String systemPromptTemplate = """
            你是一位智能地图助手，可以帮助用户解决有关地点、路线和兴趣点的问题。
            你可以使用以下工具来回答用户问题：{{tools}}
            
            请尽可能使用这些工具来提供准确的信息。当用户询问地理位置相关的问题时，
            应当主动使用相关工具来获取信息，而不是凭记忆回答。
            
            回答应当简洁、准确、有用，并使用中文。
            """;
        
        // 构建工具描述字符串
        StringBuilder toolsDescription = new StringBuilder();
        for (ToolCallback callback : callbacks) {
            toolsDescription.append("- ").append(callback.getToolDefinition().name())
                           .append(": ").append(callback.getToolDefinition().description())
                           .append("\n");
        }
        
        // 创建系统提示，替换变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("tools", toolsDescription.toString());
        SystemMessage systemMessage = new SystemPromptTemplate(systemPromptTemplate).createMessage(variables);
        
        // 用户多轮对话测试
        List<String> userQuestions = List.of(
            "北京有哪些著名的旅游景点？",
            "长城在哪里？距离北京市中心多远？",
            "我在北京站，怎么去长城？"
        );
        
        // 构建具有工具回调的ChatClient
        ChatClient clientWithTools = chatClient.builder()
                .defaultTools(callbacks)
                .build();
        
        // 模拟多轮对话
        for (String question : userQuestions) {
            UserMessage userMessage = new UserMessage(question);
            
            // 创建提示并设置工具回调
            Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
                    
            try {
                System.out.println("\n===== 发送提示 =====");
                System.out.println("用户问题: " + question);
                
                // 调用API
                ChatResponse response = clientWithTools.call(prompt);
                
                // 输出结果
                System.out.println("===== AI响应 =====");
                String content = response.getResult().getOutput().getContent();
                System.out.println(content);
                
                // 验证结果
                assertNotNull(content, "AI响应不应为空");
                assertTrue(content.length() > 0, "响应内容不应为空");
                
                // 为下一轮对话更新系统消息以保持上下文
                systemMessage = new SystemMessage(systemMessage.getContent() + 
                                                "\n用户之前的问题: " + question + 
                                                "\n你的回答: " + content);
            } catch (Exception e) {
                System.out.println("多轮对话测试失败: " + e.getMessage());
                e.printStackTrace();
                fail("AI调用应该成功");
            }
        }
    }
} */
}