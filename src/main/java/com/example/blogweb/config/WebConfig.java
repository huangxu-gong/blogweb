package com.example.blogweb.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")          // 拦截所有 /api 开头的请求
                .excludePathPatterns(                // 排除不需要登录的接口
                        "/api/user/login",
                        "/api/user/register",
                        "/api/article/list",         // 后面文章列表会用到
                        "/api/article/detail/**",    // 后面文章详情会用到
                        "/api/comment/list",// 评论列表游客可查看（发表/删除仍需登录）
                        "/api/banner/list"
                );
    }

    /**
     * 跨域配置（方便前端调用）
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    @org.springframework.beans.factory.annotation.Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        String path = java.nio.file.Paths.get(uploadPath).toAbsolutePath().normalize().toString();
        if (!path.endsWith("/") && !path.endsWith("\\")) {
            path = path + "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + path);
    }
}