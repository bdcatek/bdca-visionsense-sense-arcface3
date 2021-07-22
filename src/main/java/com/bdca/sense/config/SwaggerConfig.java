package com.bdca.sense.config;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger配置
 * 
 * @author yangjd
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	// @Value("${version}")
	private String version = "1.0";

	@Value("${spring.application.name:数据服务}")
	private String title;

	public static final HashSet<String> consumes = new HashSet<String>() {
		{
			add("application/x-www-form-urlencoded");
		}
	};

	@Bean
	public Docket createRestApi() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.bdca")).build().apiInfo(apiInfo());
		// if (!StringUtils.isEmpty(host)) {
		// docket.host(host);
		// }
		return docket;

	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(title).description("API接口").version(version)
				.termsOfServiceUrl("http://127.0.0.1:8080").build();
	}
}
