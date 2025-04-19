package com.wu.transaction.entity.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单信息表
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`order`")
@Schema(description = "订单信息表")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单号，主键")
    @TableId(value = "order_id", type = IdType.AUTO)
    private String orderId;

    @Schema(description = "买家用户ID，关联user表")
    private Integer userId;

    @Schema(description = "订单总金额，非负")
    private BigDecimal totalAmount;

    @Schema(description = "订单状态：未支付、已支付、已完成、已取消")
    private String status;

    @Schema(description = "收货地址ID，关联address表")
    private Integer addressId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
