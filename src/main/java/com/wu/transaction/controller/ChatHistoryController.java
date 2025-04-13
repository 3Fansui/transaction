package com.wu.transaction.controller;

import com.wu.transaction.entity.vo.MessageVO;
import com.wu.transaction.entity.vo.Result;
import com.wu.transaction.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {

    private final ChatHistoryRepository chatHistoryRepository;

    private final ChatMemory chatMemory;

    /**
     * 查询会话历史列表
     * @param type 业务类型，如：chat,service,pdf
     * @return chatId列表
     */
    @GetMapping("/{type}")
    public List<String> getChatIds(@PathVariable("type") String type) {
        return chatHistoryRepository.getChatIds(type);
    }

    /**
     * 根据业务类型、chatId查询会话历史
     * @param type 业务类型，如：chat,service,pdf
     * @param chatId 会话id
     * @return 指定会话的历史消息
     */
    @GetMapping("/{type}/{chatId}")
    public List<MessageVO> getChatHistory(@PathVariable("type") String type, @PathVariable("chatId") String chatId) {
        List<Message> messages = chatMemory.get(chatId, Integer.MAX_VALUE);
        if(messages == null) {
            return List.of();
        }
        return messages.stream().map(MessageVO::new).toList();
    }
    
    /**
     * 删除指定的会话历史
     * @param type 业务类型，如：chat,service,pdf
     * @param chatId 会话id
     * @return 操作结果
     */
    @DeleteMapping("/{type}/{chatId}")
    public Result deleteChatHistory(@PathVariable("type") String type, @PathVariable("chatId") String chatId) {
        boolean success = chatHistoryRepository.deleteChatHistory(type, chatId);
        return success ? Result.ok() : Result.fail("删除会话历史失败");
    }
    
    /**
     * 删除所有指定类型的会话历史
     * @param type 业务类型，如：chat,service,pdf
     * @return 操作结果
     */
    @DeleteMapping("/{type}")
    public Result deleteAllChatHistory(@PathVariable("type") String type) {
        boolean success = chatHistoryRepository.deleteAllChatHistory(type);
        return success ? Result.ok() : Result.fail("删除所有会话历史失败");
    }
}
