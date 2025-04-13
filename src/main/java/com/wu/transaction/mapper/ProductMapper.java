package com.wu.transaction.mapper;

import com.wu.transaction.entity.po.Category;
import com.wu.transaction.entity.po.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品信息表 Mapper 接口
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 获取商品的所有分类
     * @param productId 商品ID
     * @return 分类列表
     */
    List<Category> getProductCategories(@Param("productId") Integer productId);
}
