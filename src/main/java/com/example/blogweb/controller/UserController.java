package com.example.blogweb.controller;
import com.example.blogweb.common.JwtUtil;
import com.example.blogweb.common.Result;
import com.example.blogweb.pojo.User;
import com.example.blogweb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        return userService.register(user);
    }
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params){
        String username = params.get("username");
        String password = params.get("password");
        return userService.login(username, password);
    }
    @GetMapping("/info")
    public Result info(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Long userId = JwtUtil.getUserId(token);
                return userService.getById(userId);
            } catch (Exception e) {
                return Result.error(401, "登录已过期");
            }
        }
        return Result.error(401, "请先登录");
    }
    @PutMapping("/update")
    public Result update(@RequestBody Map<String, String> params, HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Long userId = JwtUtil.getUserId(token);
                return userService.update(userId, params);
            } catch (Exception e) {
                return Result.error(401, "登录已过期");
            }
        }
        return Result.error(401, "请先登录");
    }
}
