package com.wu.transaction.tools;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wu.transaction.constants.ProductStatusConstants;
import com.wu.transaction.entity.po.Category;
import com.wu.transaction.entity.po.Product;
import com.wu.transaction.entity.po.ProductCategoryRel;
import com.wu.transaction.entity.query.CategoryQuery;
import com.wu.transaction.entity.query.ProductQuery;
import com.wu.transaction.mapper.CategoryMapper;
import com.wu.transaction.mapper.ProductCategoryRelMapper;
import com.wu.transaction.mapper.ProductMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductFunctions {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private ProductCategoryRelMapper productCategoryRelMapper;

    /**
     * 转换状态值，兼容英文和中文
     * @param status 传入的状态值
     * @return 转换后的状态值
     */
    private String convertStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return ProductStatusConstants.FOR_SALE;
        }
        
        // 如果已经是中文状态，直接返回
        if (status.equals(ProductStatusConstants.DRAFT) || 
            status.equals(ProductStatusConstants.FOR_SALE) ||
            status.equals(ProductStatusConstants.SOLD) ||
            status.equals(ProductStatusConstants.REMOVED)) {
            return status;
        }
        
        // 如果是英文状态，转换为中文
        switch (status.toUpperCase()) {
            case "DRAFT":
                return ProductStatusConstants.DRAFT;
            case "FOR_SALE":
                return ProductStatusConstants.FOR_SALE;
            case "SOLD":
                return ProductStatusConstants.SOLD;
            case "REMOVED":
                return ProductStatusConstants.REMOVED;
            default:
                return ProductStatusConstants.FOR_SALE;
        }
    }

    @Tool(description = "搜索二手商品，可按分类、标题、价格范围、商品状态等条件筛选")
    public List<Product> searchProducts(ProductQuery query) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 设置查询条件
        if (query.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, query.getCategoryId());
        }

        if (StringUtils.hasText(query.getTitle())) {
            wrapper.like(Product::getTitle, query.getTitle());
        }

        if (query.getMinPrice() != null) {
            wrapper.ge(Product::getPrice, query.getMinPrice());
        }

        if (query.getMaxPrice() != null) {
            wrapper.le(Product::getPrice, query.getMaxPrice());
        }

        if (StringUtils.hasText(query.getStatus())) {
            // 使用转换后的状态值
            wrapper.eq(Product::getStatus, convertStatus(query.getStatus()));
        }

        // 设置排序
        if (query.getSorts() != null && !query.getSorts().isEmpty()) {
            for (ProductQuery.Sort sort : query.getSorts()) {
                if (sort.getField() != null && sort.getAsc() != null) {
                    if ("price".equals(sort.getField())) {
                        wrapper.orderBy(true, sort.getAsc(), Product::getPrice);
                    } else if ("created_at".equals(sort.getField())) {
                        wrapper.orderBy(true, sort.getAsc(), Product::getCreatedAt);
                    }
                }
            }
        }

        List<Product> products = productMapper.selectList(wrapper);
        
        // 加载每个商品的所有分类
        for (Product product : products) {
            List<Category> categories = productMapper.getProductCategories(product.getProductId());
            product.setCategories(categories);
        }

        return products;
    }
    
    @Tool(description = "按多个分类搜索商品，支持跨分类查询")
    public List<Product> searchProductsByCategories(
        @ToolParam(description = "分类ID列表，可以指定多个分类") List<Integer> categoryIds,
        @ToolParam(description = "价格区间下限", required = false) BigDecimal minPrice,
        @ToolParam(description = "价格区间上限", required = false) BigDecimal maxPrice,
        @ToolParam(description = "商品状态", required = false) String status) {
        
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 查询同时属于这些分类的商品
        List<Object> productIds = productCategoryRelMapper.selectObjs(
            new QueryWrapper<ProductCategoryRel>()
                .select("DISTINCT product_id")
                .in("category_id", categoryIds)
        );
        
        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 构建商品查询条件
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Product::getProductId, productIds);
        
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }
        
        if (StringUtils.hasText(status)) {
            // 使用转换后的状态值
            wrapper.eq(Product::getStatus, convertStatus(status));
        } else {
            wrapper.eq(Product::getStatus, ProductStatusConstants.FOR_SALE);
        }
        
        List<Product> products = productMapper.selectList(wrapper);
        
        // 加载每个商品的所有分类
        for (Product product : products) {
            List<Category> categories = productMapper.getProductCategories(product.getProductId());
            product.setCategories(categories);
        }
        
        return products;
    }

    @Tool(description = "浏览商品分类，可按分类名称、父分类筛选，支持层级结构查询")
    public List<Category> browseCategories(CategoryQuery query) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();

        // 设置查询条件
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Category::getName, query.getName());
        }

        if (query.getParentId() != null) {
            wrapper.eq(Category::getParentId, query.getParentId());
        }

        // 设置排序
        if (query.getSorts() != null && !query.getSorts().isEmpty()) {
            for (CategoryQuery.Sort sort : query.getSorts()) {
                if (sort.getField() != null && sort.getAsc() != null) {
                    if ("sort_order".equals(sort.getField())) {
                        wrapper.orderBy(true, sort.getAsc(), Category::getSortOrder);
                    } else if ("created_at".equals(sort.getField())) {
                        wrapper.orderBy(true, sort.getAsc(), Category::getCreatedAt);
                    }
                }
            }
        }

        return categoryMapper.selectList(wrapper);
    }

    @Tool(description = "获取商品详情，返回商品的完整信息")
    public Product getProductDetail(@ToolParam(description = "商品ID") Integer productId) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            // 加载商品的所有分类
            List<Category> categories = productMapper.getProductCategories(product.getProductId());
            product.setCategories(categories);
        }
        return product;
    }

    @Tool(description = "获取分类详情，返回分类的完整信息")
    public Category getCategoryDetail(@ToolParam(description = "分类ID") Integer categoryId) {
        return categoryMapper.selectById(categoryId);
    }

    @Tool(description = "查询该分类下的所有商品")
    public List<Product> getProductsByCategory(@ToolParam(description = "分类ID") Integer categoryId) {
        // 从关联表中查询属于该分类的所有商品ID
        List<Object> productIds = productCategoryRelMapper.selectObjs(
            new QueryWrapper<ProductCategoryRel>()
                .select("product_id")
                .eq("category_id", categoryId)
        );
        
        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 查询商品
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Product::getProductId, productIds)
               .eq(Product::getStatus, ProductStatusConstants.FOR_SALE);
        
        List<Product> products = productMapper.selectList(wrapper);
        
        // 加载每个商品的所有分类
        for (Product product : products) {
            List<Category> categories = productMapper.getProductCategories(product.getProductId());
            product.setCategories(categories);
        }
        
        return products;
    }

    @Tool(description = "获取与指定分类相关的其他分类")
    public List<Category> getRelatedCategories(@ToolParam(description = "分类ID") Integer categoryId) {
        // 1. 首先获取指定分类的父分类
        Category category = categoryMapper.selectById(categoryId);
        List<Category> related = new ArrayList<>();
        
        if (category != null) {
            // 2. 如果是子分类，获取同级分类（具有相同父分类的分类）
            if (category.getParentId() != null && category.getParentId() > 0) {
                LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Category::getParentId, category.getParentId());
                wrapper.ne(Category::getCategoryId, categoryId); // 排除自身
                related.addAll(categoryMapper.selectList(wrapper));
                
                // 3. 获取父分类
                Category parent = categoryMapper.selectById(category.getParentId());
                if (parent != null) {
                    related.add(parent);
                }
            }
            
            // 4. 获取子分类
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getParentId, categoryId);
            List<Category> children = categoryMapper.selectList(wrapper);
            related.addAll(children);
        }
        
        return related;
    }



}
