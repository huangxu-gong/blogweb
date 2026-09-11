package com.example.blogweb.pojo;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;           // 文章标题
    private String content;         // 文章内容
    private String summary;         // 文章摘要
    private Long userId;            // 作者ID
    private Integer viewCount;      // 浏览量
    private Integer status;         // 状态：1-发布  0-草稿
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    // 非数据库字段，用于显示作者昵称
    @TableField(exist = false)
    private String authorName;
}
