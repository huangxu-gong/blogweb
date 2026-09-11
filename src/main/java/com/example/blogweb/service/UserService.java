package com.example.blogweb.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blogweb.common.JwtUtil;
import com.example.blogweb.common.Result;
import com.example.blogweb.mapper.UserMapper;
import com.example.blogweb.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder =new BCryptPasswordEncoder();
    public Result register(User user){
        if (user.getUsername()==null || user.getUsername().trim().isEmpty()){
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null ||user.getPassword().length() <6){
            return Result.error("密码长度不能少于6位");
        }
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername,user.getUsername()));
        if (count > 0){
            return Result.error("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        userMapper.insert(user);
        return Result.success("注册成功");
    }
    public Result login(String username,String password){
        User user =userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername,username));
        if (user == null){
            return Result.error("用户名为空");
        }
        if (!passwordEncoder.matches(password,user.getPassword())){
            return Result.error("密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            return Result.error("账号已被禁用");
        }
        String token = JwtUtil.createToken(user.getId(),user.getUsername());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("role", user.getRole());
        return Result.success(data);
    }

    public Result getById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 排除密码字段
        user.setPassword(null);
        return Result.success(user);
    }

    public Result update(Long userId, Map<String, String> params) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (params.containsKey("nickname")) {
            String nickname = params.get("nickname");
            if (nickname == null || nickname.trim().isEmpty()) {
                return Result.error("昵称不能为空");
            }
            user.setNickname(nickname.trim());
        }
        if (params.containsKey("email")) {
            user.setEmail(params.get("email"));
        }
        if (params.containsKey("avatar")) {
            user.setAvatar(params.get("avatar"));
        }
        userMapper.updateById(user);
        user.setPassword(null);
        return Result.success(user);
    }
}
