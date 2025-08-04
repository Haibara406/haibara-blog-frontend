package com.blog.config;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author haibara
 * @description 接口文档配置
 * @since 2025/7/27 16:32
 */

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Haibara Blog API")
                        .version("v1.0.0")
                        .description("基于SpringBoot3 + Vue3开发的个人博客系统API文档")
                        .summary("提供博客系统的完整API接口")
                        .termsOfService("https://your-blog.com/terms")
                        .contact(new Contact()
                                .name("Haibara")
                                .email("haibara406@gmail.com")
                                .email("haibaraiii@foxmail.com")
                                .url("https://github.com/Haibara406")
                                .url("https://gitee.com/haibaraiii"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8062").description("开发环境"),
                        new Server().url("https://api.your-blog.com").description("生产环境")));
    }
}
