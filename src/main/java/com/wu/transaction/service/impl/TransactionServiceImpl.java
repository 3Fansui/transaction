package com.wu.transaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wu.transaction.entity.po.Order;
import com.wu.transaction.entity.po.OrderItem;
import com.wu.transaction.entity.po.Product;
import com.wu.transaction.entity.po.Transaction;
import com.wu.transaction.entity.po.User;
import com.wu.transaction.mapper.TransactionMapper;
import com.wu.transaction.service.IOrderItemService;
import com.wu.transaction.service.IOrderService;
import com.wu.transaction.service.IProductService;
import com.wu.transaction.service.ITransactionService;
import com.wu.transaction.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements ITransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final IUserService userService;
    private final IOrderService orderService;
    private final IOrderItemService orderItemService;
    private final IProductService productService;

    @Override
    public Transaction createTransaction(Integer userId, String orderId, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setOrderId(orderId);
        transaction.setAmount(amount);
        transaction.setCreatedAt(LocalDateTime.now());
        this.save(transaction);
        return transaction;
    }

    @Override
    @Transactional
    public boolean processPayment(String orderId) {
        try {
            Transaction transaction = this.getOne(new LambdaQueryWrapper<Transaction>()
                    .eq(Transaction::getOrderId, orderId));
            if (transaction == null) {
                log.error("支付处理失败：未找到订单{}对应的交易记录", orderId);
                return false;
            }
    
            // 检查用户余额是否足够
            User user = userService.getById(transaction.getUserId());
            if (user == null) {
                log.error("支付处理失败：未找到用户ID {}", transaction.getUserId());
                return false;
            }
            
            if (user.getBalance().compareTo(transaction.getAmount().abs()) < 0) {
                log.error("支付处理失败：用户{} 余额不足，当前余额：{}，所需金额：{}", 
                         user.getUserId(), user.getBalance(), transaction.getAmount().abs());
                return false;
            }
    
            // 扣减用户余额
            BigDecimal newBalance = user.getBalance().subtract(transaction.getAmount().abs());
            user.setBalance(newBalance);
            userService.updateById(user);
            log.info("用户{}余额已扣减，新余额：{}", user.getUserId(), newBalance);
            
            // 更新订单状态为已支付
            Order order = orderService.getById(orderId);
            if (order == null) {
                log.error("支付处理异常：找不到订单 {}", orderId);
                throw new RuntimeException("订单不存在：" + orderId);
            }
            
            order.setStatus("PAID");
            orderService.updateById(order);
            log.info("订单{}状态已更新为已支付", orderId);
            
            // 获取订单中的所有商品
            List<OrderItem> orderItems = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, orderId)
            );
            
            // 更新每个商品的状态为已售
            for (OrderItem item : orderItems) {
                Product product = productService.getById(item.getProductId());
                if (product != null) {
                    // 所有商品都直接设置为已售状态
                    product.setStatus("已售");
                    productService.updateById(product);
                    log.info("商品{}状态已更新为已售", product.getProductId());
                }
            }
            
            log.info("支付处理完成：订单{}", orderId);
            return true;
        } catch (Exception e) {
            log.error("支付处理发生异常，订单ID：" + orderId, e);
            // 重新抛出异常以确保事务回滚
            throw new RuntimeException("支付处理失败", e);
        }
    }

    @Override
    @Transactional
    public boolean refund(String orderId) {
        try {
            Transaction transaction = this.getOne(new LambdaQueryWrapper<Transaction>()
                    .eq(Transaction::getOrderId, orderId));
            if (transaction == null) {
                log.error("退款处理失败：未找到订单{}对应的交易记录", orderId);
                return false;
            }
    
            // 返还用户余额
            User user = userService.getById(transaction.getUserId());
            if (user == null) {
                log.error("退款处理失败：未找到用户ID {}", transaction.getUserId());
                return false;
            }
            
            BigDecimal newBalance = user.getBalance().add(transaction.getAmount().abs());
            user.setBalance(newBalance);
            userService.updateById(user);
            log.info("用户{}余额已返还，新余额：{}", user.getUserId(), newBalance);
            
            // 更新订单状态为已取消
            Order order = orderService.getById(orderId);
            if (order == null) {
                log.error("退款处理异常：找不到订单 {}", orderId);
                throw new RuntimeException("订单不存在：" + orderId);
            }
            
            order.setStatus("CANCELLED");
            orderService.updateById(order);
            log.info("订单{}状态已更新为已取消", orderId);
            
            // 获取订单中的所有商品
            List<OrderItem> orderItems = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, orderId)
            );
            
            // 恢复每个商品的状态为可用
            for (OrderItem item : orderItems) {
                Product product = productService.getById(item.getProductId());
                if (product != null) {
                    // 所有商品都直接恢复为可用状态
                    product.setStatus("在售");
                    productService.updateById(product);
                    log.info("商品{}状态已恢复为可用", product.getProductId());
                }
            }
            
            log.info("退款处理完成：订单{}", orderId);
            return true;
        } catch (Exception e) {
            log.error("退款处理发生异常，订单ID：" + orderId, e);
            // 重新抛出异常以确保事务回滚
            throw new RuntimeException("退款处理失败", e);
        }
    }

    @Override
    public String getTransactionStatus(String orderId) {
        Transaction transaction = this.getOne(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getOrderId, orderId));
        
        if (transaction == null) {
            return "FAILED";
        }
        
        // 查询订单状态
        Order order = orderService.getById(orderId);
        return order != null ? order.getStatus() : "SUCCESS";
    }
}
