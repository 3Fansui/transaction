package com.wu.transaction.entity.po;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 商品信息表
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product")
@Schema(description = "商品信息表")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID，主键")
    @TableId(value = "product_id", type = IdType.AUTO)
    private Integer productId;

    @Schema(description = "卖家ID，关联user表")
    private Integer sellerId;

    @Schema(description = "商品分类ID，关联category表")
    private Integer categoryId;

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "商品图片地址，minio存储路径")
    private String pic;

    @Schema(description = "商品状态：草稿、在售、已售、已下架")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    @Schema(description = "商品的所有分类")
    private List<Category> categories;
}

