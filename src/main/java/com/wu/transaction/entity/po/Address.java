package com.wu.transaction.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户地址信息表
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("address")
@Schema(description = "用户地址信息表")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "地址ID，主键")
    @TableId(value = "address_id", type = IdType.AUTO)
    private Integer addressId;

    @Schema(description = "用户ID，关联user表")
    private Integer userId;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区/县")
    private String district;

    @Schema(description = "详细地址")
    private String detail;

    @Schema(description = "是否默认地址，0否1是")
    private Boolean isDefault;
}
