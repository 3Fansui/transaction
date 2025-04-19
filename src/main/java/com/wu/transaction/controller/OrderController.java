package com.wu.transaction.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wu.transaction.entity.po.Order;
import com.wu.transaction.entity.po.OrderItem;
import com.wu.transaction.entity.po.Product;
import com.wu.transaction.service.IOrderItemService;
import com.wu.transaction.service.IOrderService;
import com.wu.transaction.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(name = "order-controller", description = "订单管理相关接口")
public class OrderController {

    private final IOrderService orderService;
    private final IOrderItemService orderItemService;
    private final IProductService productService;

    @PostMapping("/create")
    @Operation(summary = "创建订单", description = "创建新订单并返回订单ID")
    public String createOrder(@RequestBody CreateOrderRequest request) {
        // 生成订单ID
        String orderId = UUID.randomUUID().toString().replace("-", "");
        
        // 计算订单总额
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // 创建订单
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(request.getUserId());
        order.setStatus("UNPAID");
        order.setAddressId(request.getAddressId());
        order.setCreatedAt(LocalDateTime.now());
        
        // 创建订单项并计算总金额
        for (OrderItemRequest item : request.getItems()) {
            // 查询商品信息和价格
            Product product = productService.getById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在: " + item.getProductId());
            }
            
            // 获取实际商品价格
            BigDecimal price = product.getPrice();
            
            // 累加到订单总金额（每个商品只能买一个）
            totalAmount = totalAmount.add(price);
            
            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(item.getProductId());
            orderItem.setPrice(price);
            orderItemService.save(orderItem);
        }
        
        // 设置订单总金额
        order.setTotalAmount(totalAmount);
        orderService.save(order);
        
        return orderId;
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "查询订单", description = "根据订单ID查询订单详情")
    public Order getOrder(@PathVariable String orderId) {
        return orderService.getById(orderId);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "用户订单列表", description = "查询指定用户的所有订单")
    public List<Order> getUserOrders(@PathVariable Integer userId) {
        return orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreatedAt));
    }
    
    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "取消订单", description = "取消指定的订单")
    public boolean cancelOrder(@PathVariable String orderId) {
        Order order = orderService.getById(orderId);
        if (order != null && "UNPAID".equals(order.getStatus())) {
            order.setStatus("CANCELLED");
            return orderService.updateById(order);
        }
        return false;
    }
}

// 用于创建订单的请求对象
class CreateOrderRequest {
    private Integer userId;
    private Integer addressId;
    private List<OrderItemRequest> items;
    
    // Getters and setters
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
    
    public List<OrderItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}

// 订单项请求对象
class OrderItemRequest {
    private Integer productId;
    private Integer quantity;
    
    // Getters and setters
    public Integer getProductId() {
        return productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
} 