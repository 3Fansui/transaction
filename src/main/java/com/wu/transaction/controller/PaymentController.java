package com.wu.transaction.controller;

import com.wu.transaction.entity.po.Transaction;
import com.wu.transaction.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "支付控制器", description = "包含支付交易创建、处理、退款等功能")
public class PaymentController {

    private final ITransactionService transactionService;

    @PostMapping("/create")
    @Operation(summary = "创建交易记录", description = "创建新的支付交易记录")
    public Transaction createTransaction(@RequestParam Integer userId,
                                       @RequestParam String orderId,
                                       @RequestParam BigDecimal amount) {
        return transactionService.createTransaction(userId, orderId, amount);
    }

    @PostMapping("/process/{orderId}")
    @Operation(summary = "处理支付", description = "处理指定订单的支付，扣减用户余额")
    public boolean processPayment(@PathVariable String orderId) {
        return transactionService.processPayment(orderId);
    }

    @PostMapping("/refund/{orderId}")
    @Operation(summary = "退款", description = "对指定订单进行退款处理，返还用户余额")
    public boolean refund(@PathVariable String orderId) {
        return transactionService.refund(orderId);
    }

    @GetMapping("/status/{orderId}")
    @Operation(summary = "查询交易状态", description = "查询指定订单的交易支付状态")
    public String getTransactionStatus(@PathVariable String orderId) {
        return transactionService.getTransactionStatus(orderId);
    }
} 