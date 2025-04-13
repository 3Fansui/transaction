package com.wu.transaction.repository;

import java.util.List;

public interface ChatHistoryRepository {

    /**
     * 保存会话记录
     * @param type 业务类型，如：chat、service、pdf
     * @param chatId 会话ID
     */
    void save(String type, String chatId);

    /**
     * 获取会话ID列表
     * @param type 业务类型，如：chat、service、pdf
     * @return 会话ID列表
     */
    List<String> getChatIds(String type);
    
    /**
     * 删除会话记录
     * @param type 业务类型，如：chat、service、pdf
     * @param chatId 会话ID
     * @return 是否删除成功
     */
    boolean deleteChatHistory(String type, String chatId);
    
    /**
     * 删除所有特定类型的会话记录
     * @param type 业务类型，如：chat、service、pdf
     * @return 是否删除成功
     */
    boolean deleteAllChatHistory(String type);
}
