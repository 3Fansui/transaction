package com.wu.transaction.entity.query;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;
import java.util.List;

@Data
public class CategoryQuery {
    @ToolParam(required = false, description = "分类名称，支持模糊搜索")
    private String name;

    @ToolParam(required = false, description = "父分类ID")
    private Integer parentId;

    @ToolParam(required = false, description = "排序方式")
    private List<Sort> sorts;

    @Data
    public static class Sort {
        @ToolParam(required = false, description = "排序字段: sort_order-排序顺序，created_at-创建时间")
        private String field;

        @ToolParam(required = false, description = "是否是升序: true/false")
        private Boolean asc;
    }
}
