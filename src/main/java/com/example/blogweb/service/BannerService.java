package com.example.blogweb.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blogweb.common.Result;
import com.example.blogweb.pojo.Article;
import com.example.blogweb.pojo.Banner;
import com.example.blogweb.mapper.ArticleMapper;
import com.example.blogweb.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BannerService {

    @Autowired
    private BannerMapper bannerMapper;
    @Autowired
    private ArticleMapper articleMapper;

    public Result list() {
        return Result.success(
                bannerMapper.selectList(
                        new LambdaQueryWrapper<Banner>()
                                .eq(Banner::getStatus, 1)
                                .orderByAsc(Banner::getSort)
                                .orderByDesc(Banner::getCreateTime)
                )
        );
    }

    public Result add(Banner banner) {
        if (banner.getImageUrl() == null || banner.getImageUrl().isEmpty()) {
            return Result.error("请上传轮播图片");
        }
        if (banner.getArticleId() == null) {
            return Result.error("请选择跳转的文章");
        }
        Article article = articleMapper.selectById(banner.getArticleId());
        if (article == null || article.getStatus() != 1) {
            return Result.error("文章不存在或未发布");
        }
        if (banner.getSort() == null) {
            banner.setSort(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        if (banner.getTitle() == null || banner.getTitle().isEmpty()) {
            banner.setTitle(article.getTitle());
        }
        bannerMapper.insert(banner);
        return Result.success();
    }

    public Result all() {
        return Result.success(
                bannerMapper.selectList(
                        new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSort)
                )
        );
    }

    public Result delete(Long id) {
        bannerMapper.deleteById(id);
        return Result.success();
    }
}