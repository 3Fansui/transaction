package com.wu.transaction.service.impl;

import com.wu.transaction.entity.po.Product;
import com.wu.transaction.mapper.ProductMapper;
import com.wu.transaction.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品信息表 服务实现类
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

}
