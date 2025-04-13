package com.wu.transaction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wu.transaction.entity.po.ProductCategoryRel;
import com.wu.transaction.mapper.ProductCategoryRelMapper;
import com.wu.transaction.service.IProductCategoryRelService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品分类关联表 服务实现类
 * </p>
 *
 * @author Fs
 * @since 2025-04-12
 */
@Service
public class ProductCategoryRelServiceImpl extends ServiceImpl<ProductCategoryRelMapper, ProductCategoryRel> implements IProductCategoryRelService {
} 