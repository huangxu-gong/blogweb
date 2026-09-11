package com.example.blogweb.config;
import com.example.blogweb.common.JwtUtil;
import com.example.blogweb.common.Result;
import com.example.blogweb.mapper.UserMapper;
import com.example.blogweb.pojo.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       String token = request.getHeader("Authorization");
       if (token ==null || !token.startsWith("Bearer ")){
           writeError(response, 401, "请先登录");
           return false;
       }
        // 3. 去掉 "Bearer " 前缀，只保留真正的 token
        token = token.substring(7);
        try {
            Long userId = JwtUtil.getUserId(token);
            User user = userMapper.selectById(userId);
            if (user == null || user.getStatus() == null || user.getStatus() != 1) {
                writeError(response, 401, "账号已被禁用");
                return false;
            }
            // 轮播管理接口（除公开的 /api/banner/list）仅管理员可操作
            String uri = request.getRequestURI();
            if (uri.startsWith("/api/banner/") && !uri.endsWith("/list")) {
                if (user.getRole() == null || user.getRole() != 1) {
                    writeError(response, 403, "无管理员权限");
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            // token 过期或被篡改
            writeError(response, 401, "登录已过期，请重新登录");
            return false;
        }
    }
    private void writeError(HttpServletResponse response, int code, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200); // 保持HTTP状态码200，业务错误码放在code里
        response.getWriter().write(new ObjectMapper().writeValueAsString(Result.error(code, msg)));
    }
}
