package com.example.blogweb.controller;

import com.example.blogweb.common.JwtUtil;
import com.example.blogweb.common.Result;
import com.example.blogweb.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @GetMapping("/list")
    public Result list(@RequestParam Long userId){
        return commentService.list(userId);
    }
    @PostMapping("/add")
    public Result add(@RequestBody Map<String, Object> params,
                      @RequestHeader String authHeader){
       Long userId = JwtUtil.getUserId(authHeader.substring(7));
       Long articleId = Long.valueOf(params.get("artileId").toString());
       String content =params.get("content") == null ? "" :params.get("content").toString();
       return commentService.add(articleId,content,userId);
    }
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id,
                         @RequestHeader("Authorization") String authHeader){
       Long userId = JwtUtil.getUserId(authHeader.substring(7));
       return commentService.delete(id,userId);
    }
}
