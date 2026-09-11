package com.example.blogweb.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogweb.common.Result;
import com.example.blogweb.mapper.ArticleMapper;
import com.example.blogweb.mapper.CommentMapper;
import com.example.blogweb.pojo.Article;
import com.example.blogweb.pojo.Comment;
import com.example.blogweb.pojo.User;
import com.example.blogweb.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ArticleMapper articleMapper;

    public Result checkAdmin(Long adminId) {
        User admin = userMapper.selectById(adminId);
        if (admin == null || admin.getRole() == null || admin.getRole() != 1) {
            return Result.error(403, "无管理员权限");
        }
        return null;
    }

    public Result listUsers(Long adminId) {
        Result deny = checkAdmin(adminId);
        if (deny != null) return deny;

        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime)
        );
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    public Result updateStatus(Long adminId, Long targetId, Integer status) {
        Result deny = checkAdmin(adminId);
        if (deny != null) return deny;

        if (adminId.equals(targetId)) {
            return Result.error("不能禁用自己");
        }
        User target = userMapper.selectById(targetId);
        if (target == null) {
            return Result.error("用户不存在");
        }
        if (target.getRole() != null && target.getRole() == 1) {
            return Result.error("不能禁用管理员");
        }
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态不合法");
        }
        target.setStatus(status);
        userMapper.updateById(target);
        return Result.success(status == 1 ? "已启用" : "已禁用");
    }
    public Result articleList(Long adminId, int pageNum, int pageSize, String keyword) {
        Result deny = checkAdmin(adminId);
        if (deny != null) return deny;

        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Article::getCreateTime);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getSummary, keyword));
        }
        Page<Article> result = articleMapper.selectPage(page, wrapper);
        result.getRecords().forEach(a -> {
            User user = userMapper.selectById(a.getUserId());
            if (user != null) {
                a.setAuthorName(user.getNickname());
            }
        });
        return Result.success(result);
    }

    public Result articleDetail(Long adminId, Long id) {
        Result deny = checkAdmin(adminId);
        if (deny != null) return deny;
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }

    public Result articleUpdate(Long adminId, Article article) {
        Result deny = checkAdmin(adminId);
        if (deny != null) return deny;
        if (article.getId() == null) {
            return Result.error("文章ID不能为空");
        }
        Article old = articleMapper.selectById(article.getId());
        if (old == null) {
            return Result.error("文章不存在");
        }
        if (article.getTitle() != null && !article.getTitle().trim().isEmpty()) {
            old.setTitle(article.getTitle().trim());
        }
        if (article.getContent() != null && !article.getContent().trim().isEmpty()) {
            old.setContent(article.getContent());
        }
        if (article.getSummary() != null) {
            old.setSummary(article.getSummary());
        }
        if (article.getStatus() != null) {
            old.setStatus(article.getStatus());
        }
        articleMapper.updateById(old);
        return Result.success();
    }

    public Result articleStatus(Long adminId, Long id, Integer status) {
        Result deny = checkAdmin(adminId);
        if (deny != null) return deny;
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态不合法");
        }
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        article.setStatus(status);
        articleMapper.updateById(article);
        return Result.success(status == 1 ? "已发布" : "已下架");
    }

    public Result articleDelete(Long adminId, Long id) {
        Result deny = checkAdmin(adminId);
        if (deny != null) return deny;
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        articleMapper.deleteById(id);
        return Result.success();
    }
    public Result commentList(Long adminId, int pageNum, int pageSize, String keyword) {
        Result deny = checkAdmin(adminId);
        if (deny != null) return deny;

        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Comment::getCreateTime);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Comment::getContent, keyword);
        }
        Page<Comment> result = commentMapper.selectPage(page, wrapper);
        result.getRecords().forEach(c -> {
            User user = userMapper.selectById(c.getUserId());
            if (user != null) {
                c.setNickname(user.getNickname());
            }
        });
        return Result.success(result);
    }

    public Result commentStatus(Long adminId, Long id, Integer status) {
        Result deny = checkAdmin(adminId);
        if (deny != null) return deny;
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态不合法");
        }
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        comment.setStatus(status);
        commentMapper.updateById(comment);
        return Result.success(status == 1 ? "已显示" : "已隐藏");
    }

    public Result commentDelete(Long adminId, Long id) {
        Result deny = checkAdmin(adminId);
        if (deny != null) return deny;
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        commentMapper.deleteById(id);
        return Result.success();
    }
}