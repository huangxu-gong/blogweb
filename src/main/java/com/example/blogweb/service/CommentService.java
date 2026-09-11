package com.example.blogweb.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blogweb.common.Result;
import com.example.blogweb.mapper.ArticleMapper;
import com.example.blogweb.mapper.CommentMapper;
import com.example.blogweb.mapper.UserMapper;
import com.example.blogweb.pojo.Article;
import com.example.blogweb.pojo.Comment;
import com.example.blogweb.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleMapper articleMapper;

    public Result list(Long articleId){
        List<Comment> list =commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment ::getArticleId,articleId)
                        .eq(Comment::getStatus, 1)
                        .orderByDesc(Comment::getCreateTime)
        );
        list.forEach( comment -> {
            User user =userMapper.selectById(comment.getUserId());
            if (user != null){
                comment.setNickname(user.getNickname());
            }
        });
        return Result.success(list);
    }
    public Result add(Long articleId,String content ,Long userId){
        if (content == null || content.trim().isEmpty()){
            return Result.error("评论内容不能为空");
        }
        Article article =articleMapper.selectById(articleId);
        if (article == null || article.getStatus() != 1){
            return Result.error("文章不存在或未发布");
        }
        if (content.trim().length() > 2000) {
            return Result.error("评论不能超过2000字");
        }
        Comment comment =new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(content.trim());
        commentMapper.insert(comment);
        return Result.success();
    }
    public Result delete(Long id ,Long userId){
        Comment comment = commentMapper.selectById(id);
        if (comment ==null){
            return Result.error("评论不存在");
        }
        if (!comment.getUserId().equals(userId)){
            return  Result.error("你无权删除该评论");
        }
        commentMapper.deleteById(id);
        return Result.success();
    }
}
