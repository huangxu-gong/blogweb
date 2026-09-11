package com.example.blogweb.pojo;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;      // 登录用户名
    private String password;      // 密码（加密存储）
    private String nickname;      // 昵称
    private String email;         // 邮箱
    private String avatar;        // 头像
    private Integer role;     // 0普通 1 管理员
    private Integer status;   // 1正常 0禁用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
