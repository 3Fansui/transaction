package com.wu.transaction.service.impl;

import com.wu.transaction.entity.po.Category;
import com.wu.transaction.mapper.CategoryMapper;
import com.wu.transaction.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
