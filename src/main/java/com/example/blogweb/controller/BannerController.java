package com.example.blogweb.controller;
import com.example.blogweb.common.Result;
import com.example.blogweb.pojo.Banner;
import com.example.blogweb.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /** 首页轮播（公开） */
    @GetMapping("/list")
    public Result list() {
        return bannerService.list();
    }

    /** 管理列表（需登录） */
    @GetMapping("/all")
    public Result all() {
        return bannerService.all();
    }

    /** 新增轮播（需登录） */
    @PostMapping("/add")
    public Result add(@RequestBody Banner banner) {
        return bannerService.add(banner);
    }

    /** 删除（需登录） */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        return bannerService.delete(id);
    }
}