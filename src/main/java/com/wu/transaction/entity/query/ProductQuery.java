package com.wu.transaction.entity.query;

import com.wu.transaction.constants.ProductStatusConstants;
import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductQuery {
    @ToolParam(required = false, description = "商品分类ID，关联category表")
    private Integer categoryId;

    @ToolParam(required = false, description = "商品标题，支持模糊搜索")
    private String title;

    @ToolParam(required = false, description = "价格区间下限")
    private BigDecimal minPrice;

    @ToolParam(required = false, description = "价格区间上限")
    private BigDecimal maxPrice;


    @ToolParam(required = false, description = "商品销售状态：草稿-"+ProductStatusConstants.DRAFT+"，在售-"+ProductStatusConstants.FOR_SALE+"，已售-"+ProductStatusConstants.SOLD+"，已下架-"+ProductStatusConstants.REMOVED)
    private String status;

    @ToolParam(required = false, description = "排序方式")
    private List<Sort> sorts;

    @Data
    public static class Sort {
        @ToolParam(required = false, description = "排序字段: price-价格，created_at-上架时间")
        private String field;

        @ToolParam(required = false, description = "是否是升序: true/false")
        private Boolean asc;
    }
}
