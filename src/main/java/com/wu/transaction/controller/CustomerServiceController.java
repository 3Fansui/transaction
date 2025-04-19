package com.wu.transaction.controller;


import com.wu.transaction.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class CustomerServiceController {

    private final ChatClient serviceChatClient;

    private final ChatHistoryRepository chatHistoryRepository;
    
    private final VectorStore vectorStore;
    
    // 检索结果数量限制
    private static final int MAX_RESULTS = 3;
    
    // 相似度阈值
    private static final float SIMILARITY_THRESHOLD = 0.6f;

    @RequestMapping(value = "/service", produces = "text/html;charset=utf-8")
    public Flux<String> service(String prompt, String chatId) {
        // 1.保存会话id
        chatHistoryRepository.save("service", chatId);
        
        // 2.从向量数据库检索相关知识
        String enhancedPrompt = enrichPromptWithKnowledge(prompt);
        
        // 3.请求模型
        return serviceChatClient.prompt()
                .user(enhancedPrompt)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();
    }
    
    /**
     * 根据用户问题从向量数据库检索相关知识，并增强提示词
     * @param userPrompt 用户问题
     * @return 增强后的提示词
     */
    private String enrichPromptWithKnowledge(String userPrompt) {
        // 构建向量搜索请求
        SearchRequest request = SearchRequest.builder()
                .query(userPrompt)
                .topK(MAX_RESULTS)
                .similarityThreshold(SIMILARITY_THRESHOLD)
                .build();
        
        // 向量数据库检索
        List<Document> relevantDocs = vectorStore.similaritySearch(request);
        
        // 如果没有相关知识，直接返回原问题
        if (relevantDocs == null || relevantDocs.isEmpty()) {
            return userPrompt;
        }
        
        // 知识提取和排序，根据相关性评分排序
        String relevantKnowledge = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));
        
        // 构建增强的提示词，更适合通用知识检索
        return String.format("""
                以下是与用户问题相关的信息:
                
                %s
                
                用户问题: %s
                
                请基于以上信息回答用户问题。如果以上信息不足以回答用户问题，请直接告知用户你没有足够信息。
                回答要自然流畅，不要提及你是从哪里获取的信息，也不要直接引用以上内容，而是融入到你的回答中。
                不要试图引导用户讨论特定话题，专注于回答用户的实际问题。
                """, relevantKnowledge, userPrompt);
    }
}