package com.wu.transaction.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisChatHistory implements ChatHistoryRepository{

    private final StringRedisTemplate redisTemplate;
    private final RedisChatMemory redisChatMemory;

    private final static String CHAT_HISTORY_KEY_PREFIX = "chat:history:";

    @Override
    public void save(String type, String chatId) {
        redisTemplate.opsForSet().add(CHAT_HISTORY_KEY_PREFIX + type, chatId);
    }

    @Override
    public List<String> getChatIds(String type) {
        Set<String> chatIds = redisTemplate.opsForSet().members(CHAT_HISTORY_KEY_PREFIX + type);
        if(chatIds == null || chatIds.isEmpty()) {
            return Collections.emptyList();
        }
        return chatIds.stream().sorted(String::compareTo).toList();
    }
    
    @Override
    public boolean deleteChatHistory(String type, String chatId) {
        try {
            // 1. 从集合中删除chatId
            Long removed = redisTemplate.opsForSet().remove(CHAT_HISTORY_KEY_PREFIX + type, chatId);
            
            // 2. 同时清除会话内容
            redisChatMemory.clear(chatId);
            
            log.info("删除会话历史: type={}, chatId={}, result={}", type, chatId, removed);
            return removed != null && removed > 0;
        } catch (Exception e) {
            log.error("删除会话历史失败: type={}, chatId={}", type, chatId, e);
            return false;
        }
    }
    
    @Override
    public boolean deleteAllChatHistory(String type) {
        try {
            // 1. 获取所有会话ID
            Set<String> chatIds = redisTemplate.opsForSet().members(CHAT_HISTORY_KEY_PREFIX + type);
            if (chatIds == null || chatIds.isEmpty()) {
                return true;
            }
            
            // 2. 逐个清除会话内容
            for (String chatId : chatIds) {
                redisChatMemory.clear(chatId);
            }
            
            // 3. 删除整个集合
            Boolean result = redisTemplate.delete(CHAT_HISTORY_KEY_PREFIX + type);
            
            log.info("删除所有会话历史: type={}, count={}, result={}", type, chatIds.size(), result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("删除所有会话历史失败: type={}", type, e);
            return false;
        }
    }
}