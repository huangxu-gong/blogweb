package com.example.blogweb.controller;

import com.example.blogweb.common.JwtUtil;
import com.example.blogweb.common.Result;
import com.example.blogweb.pojo.Article;
import com.example.blogweb.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /** 用户列表 GET /api/admin/users */
    @GetMapping("/users")
    public Result users(@RequestHeader("Authorization") String auth) {
        Long adminId = JwtUtil.getUserId(auth.substring(7));
        return adminService.listUsers(adminId);
    }

    /** 禁用/启用 PUT /api/admin/users/status */
    @PutMapping("/users/status")
    public Result updateStatus(@RequestHeader("Authorization") String auth,
                               @RequestBody Map<String, Object> params) {
        Long adminId = JwtUtil.getUserId(auth.substring(7));
        Long userId = Long.valueOf(params.get("userId").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        return adminService.updateStatus(adminId, userId, status);
    }
    @GetMapping("/articles")
    public Result articles(@RequestHeader("Authorization") String auth,
                           @RequestParam(defaultValue = "1") int pageNum,
                           @RequestParam(defaultValue = "10") int pageSize,
                           @RequestParam(required = false) String keyword) {
        Long adminId = JwtUtil.getUserId(auth.substring(7));
        return adminService.articleList(adminId, pageNum, pageSize, keyword);
    }

    @GetMapping("/articles/{id}")
    public Result articleDetail(@RequestHeader("Authorization") String auth,
                                @PathVariable Long id) {
        Long adminId = JwtUtil.getUserId(auth.substring(7));
        return adminService.articleDetail(adminId, id);
    }

    @PutMapping("/articles")
    public Result articleUpdate(@RequestHeader("Authorization") String auth,
                                @RequestBody Article article) {
        Long adminId = JwtUtil.getUserId(auth.substring(7));
        return adminService.articleUpdate(adminId, article);
    }

    @PutMapping("/articles/status")
    public Result articleStatus(@RequestHeader("Authorization") String auth,
                                @RequestBody Map<String, Object> params) {
        Long adminId = JwtUtil.getUserId(auth.substring(7));
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        return adminService.articleStatus(adminId, id, status);
    }

    @DeleteMapping("/articles/{id}")
    public Result articleDelete(@RequestHeader("Authorization") String auth,
                                @PathVariable Long id) {
        Long adminId = JwtUtil.getUserId(auth.substring(7));
        return adminService.articleDelete(adminId, id);
    }
    @GetMapping("/comments")
    public Result comments(@RequestHeader("Authorization") String auth,
                           @RequestParam(defaultValue = "1") int pageNum,
                           @RequestParam(defaultValue = "10") int pageSize,
                           @RequestParam(required = false) String keyword) {
        Long adminId = JwtUtil.getUserId(auth.substring(7));
        return adminService.commentList(adminId, pageNum, pageSize, keyword);
    }

    @PutMapping("/comments/status")
    public Result commentStatus(@RequestHeader("Authorization") String auth,
                                @RequestBody Map<String, Object> params) {
        Long adminId = JwtUtil.getUserId(auth.substring(7));
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        return adminService.commentStatus(adminId, id, status);
    }

    @DeleteMapping("/comments/{id}")
    public Result commentDelete(@RequestHeader("Authorization") String auth,
                                @PathVariable Long id) {
        Long adminId = JwtUtil.getUserId(auth.substring(7));
        return adminService.commentDelete(adminId, id);
    }
}