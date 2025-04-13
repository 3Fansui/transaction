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
 * 订单明细表
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_item")
@Schema(description = "订单明细表")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单项ID，主键")
    @TableId(value = "item_id", type = IdType.AUTO)
    private Integer itemId;

    @Schema(description = "订单ID，关联order表")
    private String orderId;

    @Schema(description = "商品ID，关联product表")
    private Integer productId;

    @Schema(description = "成交价格")
    private BigDecimal price;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}

