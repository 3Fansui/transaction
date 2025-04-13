package com.wu.transaction.entity.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 交易流水表
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("transaction")
@Schema(description = "交易流水表")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "交易ID，主键")
    @TableId(value = "tx_id", type = IdType.AUTO)
    private Integer txId;

    @Schema(description = "用户ID，关联user表")
    private Integer userId;

    @Schema(description = "交易金额，负数为扣减金额")
    private BigDecimal amount;

    @Schema(description = "关联订单号，关联order表")
    private String orderId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
