package com.example.blogweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blogweb.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
