package com.wu.transaction.service;

import com.wu.transaction.entity.po.Transaction;
import com.baomidou.mybatisplus.extension.service.IService;
import java.math.BigDecimal;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
public interface ITransactionService extends IService<Transaction> {
    /**
     * 创建交易记录
     */
    Transaction createTransaction(Integer userId, String orderId, BigDecimal amount);
    
    /**
     * 处理支付
     */
    boolean processPayment(String orderId);
    
    /**
     * 退款
     */
    boolean refund(String orderId);
    
    /**
     * 查询交易状态
     */
    String getTransactionStatus(String orderId);
}
