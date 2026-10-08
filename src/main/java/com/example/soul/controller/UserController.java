package com.example.soul.controller;

import com.example.soul.entity.User;
import com.example.soul.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息相关控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取用户个人资料
     * 对应 Android 端: GET /api/user/profile?username=xxx
     */
    @GetMapping("/profile")
    public User getUserProfile(@RequestParam String username) {
        log.info("Fetching profile for username: {}", username);

        // 从数据库中查找用户，如果找不到则抛出异常（Spring Boot 会自动转为错误响应）
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * 更新用户个人资料 (可选，为您后续扩展准备)
     * POST /api/user/update
     */
    @PostMapping("/update")
    public User updateProfile(@RequestBody User user) {
        log.info("Updating profile for user: {}", user.getUsername());
        // 这里可以调用您的 userService.save(user) 方法
        // return userService.updateUser(user);
        return user;
    }
}