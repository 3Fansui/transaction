package com.wu.transaction.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wu.transaction.entity.po.Product;
import com.wu.transaction.entity.po.ProductCategoryRel;
import com.wu.transaction.entity.query.ProductQuery;
import com.wu.transaction.service.IProductCategoryRelService;
import com.wu.transaction.service.IProductService;
import com.wu.transaction.service.MinioService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品信息表 控制器
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "product-controller", description = "商品的增删改查接口")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;
    
    private final MinioService minioService;
    
    private final MinioClient minioClient;
    
    private final IProductCategoryRelService productCategoryRelService;

    @GetMapping
    @Operation(summary = "获取商品列表", description = "根据查询条件分页获取商品列表")
    public Page<Product> list(ProductQuery query,
                              @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer pageNum,
                              @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        // 创建分页对象
        Page<Product> page = new Page<>(pageNum, pageSize);
        
        // 分类ID特殊处理：需要考虑商品多分类的情况
        Integer categoryId = query != null ? query.getCategoryId() : null;
        
        // 如果没有指定分类ID，使用普通查询
        if (categoryId == null) {
            // 使用lambdaQuery简化条件查询
            return productService.lambdaQuery()
                // 按标题模糊查询
                .like(query != null && query.getTitle() != null && !query.getTitle().isEmpty(), 
                    Product::getTitle, query.getTitle())
                // 按最低价格查询
                .ge(query != null && query.getMinPrice() != null, 
                    Product::getPrice, query.getMinPrice())
                // 按最高价格查询
                .le(query != null && query.getMaxPrice() != null, 
                    Product::getPrice, query.getMaxPrice())
                // 按状态查询
                .eq(query != null && query.getStatus() != null && !query.getStatus().isEmpty(), 
                    Product::getStatus, query.getStatus())
                // 添加排序(如果有排序条件)
                .last(query != null && query.getSorts() != null && !query.getSorts().isEmpty() ? 
                    buildOrderBySql(query) : "")
                // 执行分页查询
                .page(page);
        } else {
            // 如果指定了分类ID，需要通过关联表查询
            // 1. 先从product_category_rel表中查询符合分类的商品ID
            QueryWrapper<ProductCategoryRel> relWrapper = new QueryWrapper<>();
            relWrapper.eq("category_id", categoryId);
            List<Object> productIds = productCategoryRelService.listObjs(relWrapper);
            
            // 如果没有找到符合分类的商品ID，直接返回空页
            if (productIds.isEmpty()) {
                return new Page<>();
            }
            
            // 2. 然后根据这些商品ID查询商品详情
            return productService.lambdaQuery()
                // 按商品ID列表查询
                .in(Product::getProductId, productIds)
                // 按标题模糊查询
                .like(query.getTitle() != null && !query.getTitle().isEmpty(), 
                    Product::getTitle, query.getTitle())
                // 按最低价格查询
                .ge(query.getMinPrice() != null, 
                    Product::getPrice, query.getMinPrice())
                // 按最高价格查询
                .le(query.getMaxPrice() != null, 
                    Product::getPrice, query.getMaxPrice())
                // 按状态查询
                .eq(query.getStatus() != null && !query.getStatus().isEmpty(), 
                    Product::getStatus, query.getStatus())
                // 添加排序(如果有排序条件)
                .last(query.getSorts() != null && !query.getSorts().isEmpty() ? 
                    buildOrderBySql(query) : "")
                // 执行分页查询
                .page(page);
        }
    }
    
    /**
     * 构建排序SQL
     */
    private String buildOrderBySql(ProductQuery query) {
        StringBuilder sb = new StringBuilder(" ORDER BY ");
        boolean isFirst = true;
        
        for (ProductQuery.Sort sort : query.getSorts()) {
            if (sort.getField() != null && sort.getAsc() != null) {
                if (!isFirst) {
                    sb.append(", ");
                }
                
                if ("price".equals(sort.getField())) {
                    sb.append("price ");
                } else if ("created_at".equals(sort.getField())) {
                    sb.append("created_at ");
                } else {
                    continue;
                }
                
                sb.append(sort.getAsc() ? "ASC" : "DESC");
                isFirst = false;
            }
        }
        
        return isFirst ? "" : sb.toString();
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门商品", description = "获取指定的热门商品列表")
    public List<Product> getPopularProducts() {
        // 指定的热门商品ID列表
        List<Integer> productIds = new ArrayList<>();
        productIds.add(50); // MacBook Pro 2021 16-inch
        productIds.add(51); // Sony PlayStation 5 Digital Edition
        productIds.add(52); // Vintage Leather Sofa
        productIds.add(53); // Canon EOS R5 Camera
        productIds.add(54); // Handmade Ceramic Vase Set
        productIds.add(55); // Vintage Vinyl Records Collection
        
        // 查询指定ID的商品
        return productService.lambdaQuery()
            .in(Product::getProductId, productIds)
            .list();
    }

    
    @GetMapping("/new")
    @Operation(summary = "获取新推出商品", description = "获取指定的新品商品列表")
    public List<Product> getNewProducts() {
        // 测试阶段使用相同的商品列表
        List<Integer> productIds = new ArrayList<>();
        productIds.add(50); // MacBook Pro 2021 16-inch
        productIds.add(51); // Sony PlayStation 5 Digital Edition
        productIds.add(52); // Vintage Leather Sofa
        productIds.add(53); // Canon EOS R5 Camera
        productIds.add(54); // Handmade Ceramic Vase Set
        productIds.add(55); // Vintage Vinyl Records Collection
        
        // 查询指定ID的商品
        return productService.lambdaQuery()
            .in(Product::getProductId, productIds)
            .list();
    }
    
    @GetMapping("/offers")
    @Operation(summary = "获取优惠套餐", description = "获取指定的优惠商品列表")
    public List<Product> getOfferProducts() {
        // 测试阶段使用相同的商品列表
        List<Integer> productIds = new ArrayList<>();
        productIds.add(50); // MacBook Pro 2021 16-inch
        productIds.add(51); // Sony PlayStation 5 Digital Edition
        productIds.add(52); // Vintage Leather Sofa
        productIds.add(53); // Canon EOS R5 Camera
        productIds.add(54); // Handmade Ceramic Vase Set
        productIds.add(55); // Vintage Vinyl Records Collection
        
        // 查询指定ID的商品
        return productService.lambdaQuery()
            .in(Product::getProductId, productIds)
            .list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详情")
    public Product detail(@PathVariable("id") Integer id) {
        return productService.getById(id);
    }

    @PostMapping
    @Operation(summary = "添加商品", description = "添加新商品，图片字段可选，可以先通过图片上传接口获取图片路径")
    public Product add(@RequestBody Product product) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        
        // 保存商品信息
        productService.save(product);
        
        return product;
    }
    
    @PostMapping("/category-relation")
    @Operation(summary = "添加商品分类关联", description = "添加商品与分类的关联关系")
    public Boolean addCategoryRelation(@RequestBody ProductCategoryRel relation) {
        return productCategoryRelService.save(relation);
    }
    
    @DeleteMapping("/{productId}/categories")
    @Operation(summary = "删除商品分类关联", description = "删除商品与分类的所有关联关系，用于重建关联")
    public Boolean deleteCategoryRelations(@PathVariable("productId") Integer productId) {
        QueryWrapper<ProductCategoryRel> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId);
        return productCategoryRelService.remove(wrapper);
    }
    
    @PostMapping("/upload-image")
    @Operation(summary = "上传商品图片", description = "单独上传商品图片，返回图片路径和URL，可用于添加或更新商品时设置")
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 上传图片到MinIO
        String imagePath = minioService.uploadProductImage(file);
        
        Map<String, String> result = new HashMap<>();
        result.put("path", imagePath);
        result.put("url", getProductImageUrl(imagePath));
        
        return result;
    }
    
    @GetMapping("/image")
    @Operation(summary = "获取图片URL", description = "根据图片路径获取访问URL")
    public String getImageUrl(@RequestParam("path") String imagePath) {
        return minioService.getFileUrl(minioService.getProductImageBucket(), imagePath);
    }
    
    @GetMapping("/image-info")
    @Operation(summary = "获取图片信息", description = "根据图片路径获取图片信息，包括URL")
    public Map<String, String> getImageInfo(@RequestParam("path") String imagePath) {
        Map<String, String> result = new HashMap<>();
        result.put("path", imagePath);
        result.put("url", getProductImageUrl(imagePath));
        return result;
    }
    
    /**
     * 获取商品图片完整URL
     */
    private String getProductImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return minioService.getFileUrl(minioService.getProductImageBucket(), imagePath);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品", description = "更新商品信息，图片字段可选，可以先通过图片上传接口获取图片路径")
    public Product update(@PathVariable("id") Integer id, @RequestBody Product product) {
        // 获取原商品数据，用于比较图片URL是否修改
        Product originalProduct = productService.getById(id);
        if (originalProduct == null) {
            throw new RuntimeException("商品不存在：" + id);
        }
        
        // 设置商品ID和更新时间
        product.setProductId(id);
        product.setUpdatedAt(LocalDateTime.now());
        
        // 如果图片地址发生变化且原图片不为空，删除原图片
        if (originalProduct.getPic() != null && !originalProduct.getPic().isEmpty() &&
                (product.getPic() == null || !originalProduct.getPic().equals(product.getPic()))) {
            minioService.deleteFile(minioService.getProductImageBucket(), originalProduct.getPic());
        }
        
        // 更新商品信息
        productService.updateById(product);
        
        // 返回更新后的商品信息
        return productService.getById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "根据商品ID删除商品及其图片")
    public boolean delete(@PathVariable("id") Integer id) {
        // 获取商品信息，用于删除图片
        Product product = productService.getById(id);
        if (product != null && product.getPic() != null) {
            // 删除商品图片
            minioService.deleteFile(minioService.getProductImageBucket(), product.getPic());
        }
        
        // 删除商品分类关联
        QueryWrapper<ProductCategoryRel> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", id);
        productCategoryRelService.remove(wrapper);
        
        // 删除商品
        return productService.removeById(id);
    }

    @GetMapping(value = "/image-content", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "获取图片内容", description = "直接返回图片文件内容")
    public ResponseEntity<byte[]> getImageContent(@RequestParam("path") String imagePath) {
        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioService.getProductImageBucket())
                            .object(imagePath)
                            .build());
                            
            byte[] data = response.readAllBytes();
            response.close();
            
            // 确定内容类型
            String contentType = "image/jpeg"; // 默认JPEG
            if (imagePath.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (imagePath.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (imagePath.toLowerCase().endsWith(".webp")) {
                contentType = "image/webp";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
} 