package com.example.blogweb.controller;
import com.example.blogweb.common.JwtUtil;
import com.example.blogweb.common.Result;
import com.example.blogweb.pojo.Article;
import com.example.blogweb.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    /**
     * 发布文章（需要登录）
     */
    @PostMapping("/publish")
    public Result publish(@RequestBody Article article,
                          @RequestHeader("Authorization") String authHeader) {

        // 从 token 中解析出当前登录用户的ID
        String token = authHeader.substring(7);  // 去掉 "Bearer "
        Long userId = JwtUtil.getUserId(token);
        return articleService.publish(article, userId);
    }
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10")int pageSize,
                       @RequestParam(required = false) String keyword){
        return articleService.list(pageNum,pageSize,keyword);
    }
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id,
                         jakarta.servlet.http.HttpServletRequest request){
        // 未登录游客 viewerId 为 null，只能看已发布文章；作者带 token 可查看自己的草稿
        Long viewerId = null;
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            try {
                viewerId = JwtUtil.getUserId(token.substring(7));
            } catch (Exception ignored) { }
        }
        return articleService.detail(id, viewerId);
    }
    @GetMapping("my")
    public Result myArticles(@RequestHeader("Authorization") String authHeader,
                             @RequestParam(defaultValue = "1") int pageNum,
                             @RequestParam(defaultValue = "10") int pageSize){
        String token =authHeader.substring(7);
        Long userId = JwtUtil.getUserId(token);
        return articleService.myArticles(userId,pageNum,pageSize);
    }
    /**
     * 更新文章（需要登录）
     * PUT /api/article/update
     *
     * 请求头：
     * Authorization: Bearer 你的token
     *
     * 请求体示例：
     * {
     *   "id": 1,
     *   "title": "修改后的标题",
     *   "content": "修改后的内容",
     *   "status": 1
     * }
     */
    @PutMapping("/update")
    public Result update(@RequestBody Article article,
                         @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.getUserId(token);
        return articleService.update(article, userId);
    }
    /**
     * 删除文章（需要登录）
     * DELETE /api/article/1
     *
     * 请求头：
     * Authorization: Bearer 你的token
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id,
                         @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.getUserId(token);
        return articleService.delete(id, userId);
    }
}
