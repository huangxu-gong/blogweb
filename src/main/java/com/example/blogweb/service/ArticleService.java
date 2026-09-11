package com.example.blogweb.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogweb.common.Result;
import com.example.blogweb.mapper.ArticleMapper;
import com.example.blogweb.mapper.UserMapper;
import com.example.blogweb.pojo.Article;
import com.example.blogweb.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    public Result publish(Article article, Long userId) {
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            return Result.error("标题不能为空");
        }
        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            return Result.error("内容不能为空");
        }
        article.setUserId(userId);
        if (article.getSummary() == null || article.getSummary().trim().isEmpty()) {
            String content = article.getContent();
            if (content.length() > 150) {
                article.setSummary(content.substring(0, 150));
            } else {
                article.setSummary(content);
            }
        }
        if (article.getStatus() == null) {
            article.setStatus(1);
        }
        articleMapper.insert(article);
        return Result.success(article.getId());
    }
    public Result list(int pageNum,int pageSize ,String keyword){
        Page<Article> page =new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Article> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus,1)
                .orderByDesc(Article::getCreateTime);
        if (keyword !=null && !keyword.trim().isEmpty()){
            wrapper.and(w -> w.like(Article::getTitle,keyword)
                    .or()
                    .like(Article::getSummary,keyword));
        }
        Page<Article> result = articleMapper.selectPage(page,wrapper);
        result.getRecords().forEach(article -> {
            User user = userMapper.selectById(article.getUserId());
            if (user !=null){
                article.setAuthorName(user.getNickname());
            }
        });
        return Result.success(result);
    }
    public Result detail(Long id, Long viewerId){
        Article article =articleMapper.selectById(id);
        if (article ==null){
            return Result.error("文章不存在或未发布");
        }
        // 作者本人可以查看自己的草稿，其他游客只能看已发布文章
        boolean isAuthor = viewerId != null && viewerId.equals(article.getUserId());
        if (article.getStatus() != 1 && !isAuthor){
            return Result.error("文章不存在或未发布");
        }
        // 草稿不累计浏览量
        if (article.getStatus() == 1) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
        }
        User user =userMapper.selectById(article.getUserId());
        if (user !=null){
            article.setAuthorName(user.getNickname());
        }
        return Result.success(article);
    }
    public Result myArticles(Long userId,int pageNum,int pageSize){
        Page<Article> page =new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Article> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(Article::getUserId,userId)
                .orderByDesc(Article ::getCreateTime);
        Page<Article> result =articleMapper.selectPage(page,wrapper);
        return Result.success(result);
    }
    public Result update(Article article ,Long userId){
        if (article.getId() ==null){
            return Result.error("文章id不能为空");
        }
        Article old =articleMapper.selectById(article.getId());
        if (old ==null){
            return Result.error("文章不存在");
        }
        if (!old.getUserId().equals(userId)){
            return Result.error("无权修改此文章");
        }
        if (article.getTitle() !=null && !article.getTitle().trim().isEmpty()){
            old.setTitle(article.getTitle());
        }
        if (article.getContent() != null && !article.getContent().trim().isEmpty()) {
            old.setContent(article.getContent());
            // 内容变了且没传摘要时，重新生成摘要
            if (article.getSummary() == null || article.getSummary().trim().isEmpty()) {
                String content = article.getContent();
                old.setSummary(content.length() > 150 ? content.substring(0, 150) + "..." : content);
            }
        }
        if (article.getSummary() != null && !article.getSummary().trim().isEmpty()) {
            old.setSummary(article.getSummary());
        }
        if (article.getStatus() != null) {
            old.setStatus(article.getStatus());
        }

        articleMapper.updateById(old);
        return Result.success();
    }
    /**
     * 删除文章（需登录且是作者）
     */
    public Result delete(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        if (!article.getUserId().equals(userId)) {
            return Result.error("无权删除此文章");
        }
        articleMapper.deleteById(id);
        return Result.success();
    }
}
