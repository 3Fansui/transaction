package com.wu.transaction.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "二手商品交易系统API",
                version = "1.0.0",
                description = "本系统提供二手交易的各种功能接口，包括用户管理、商品管理、订单支付等",
                contact = @Contact(
                        name = "开发团队",
                        email = "dev@example.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
public class SwaggerConfig {
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/user/login", "/api/user/register", "/api/products/**", "/api/categories/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi privateApi() {
        return GroupedOpenApi.builder()
                .group("authenticated")
                .pathsToMatch("/api/**")
                .build();
    }
    
    /**
     * 全局添加认证参数
     */
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            // 创建一个全局组件，包含参数定义
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            
            // 添加全局请求头参数
            openApi.getComponents()
                    .addParameters("Authorization", new Parameter()
                            .in("header")
                            .name("Authorization")
                            .description("JWT令牌，格式为 Bearer token")
                            .required(false)
                            .schema(new StringSchema()));
        };
    }
    
    /**
     * 为每个操作添加认证参数
     */
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            // 为每个操作添加认证头参数
            Parameter authParam = new Parameter()
                    .in("header")
                    .name("Authorization")
                    .description("JWT令牌，格式为 Bearer token")
                    .required(false)
                    .schema(new StringSchema());
            
            operation.addParametersItem(authParam);
            return operation;
        };
    }
}
