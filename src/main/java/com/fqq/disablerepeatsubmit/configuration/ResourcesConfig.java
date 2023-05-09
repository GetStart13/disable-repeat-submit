package com.fqq.disablerepeatsubmit.configuration;

import com.fqq.disablerepeatsubmit.interceptor.RepeatSubmitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 配置注册拦截器
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {
    @Resource
    private RepeatSubmitInterceptor repeatSubmitInterceptor;

    /**
     * 自定义拦截规则，注册拦截器，设置拦截路径
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
    }

}
