package com.wu.transaction.service.impl;

import com.wu.transaction.entity.po.Transaction;
import com.wu.transaction.mapper.TransactionMapper;
import com.wu.transaction.service.ITransactionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements ITransactionService {

}
