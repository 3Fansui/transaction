package com.wu.transaction.controller;

import com.wu.transaction.entity.po.Category;
import com.wu.transaction.entity.query.CategoryQuery;
import com.wu.transaction.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品分类控制器
 */
@RestController
@RequestMapping("/api/categories")
@Tag(name = "分类管理", description = "商品分类相关接口")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取所有分类", description = "获取所有商品分类列表")
    public List<Category> list() {
        return categoryService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "根据分类ID获取分类详情")
    public Category detail(@PathVariable("id") Integer id) {
        return categoryService.getById(id);
    }
} 