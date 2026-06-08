package com.example.soul.controller;

import com.example.soul.dto.ProfileResponse;
import com.example.soul.dto.ProfileUpdateRequest;
import com.example.soul.entity.User;
import com.example.soul.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 사용자 프로필 API
 * 用户个人资料接口
 */
@RestController
@RequestMapping("/api")
public class ProfileController {

    // 用户数据库连接
    private final UserRepository userRepository;

    // 构造函数注入
    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 按 username 查询个人资料
     * GET /api/profile?username=gxc
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @RequestParam String username
    ) {
        User user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(message("User not found"));
        }

        return ResponseEntity.ok(
                toProfile(user)
        );
    }

    /**
     * 按 username 修改个人资料
     * PUT /api/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody ProfileUpdateRequest request
    ) {
        String username = trimToNull(request.getUsername());

        if (username == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(message("Username is required"));
        }

        User user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(message("User not found"));
        }

        updateUserFromRequest(user, request);

        User saved = userRepository.save(user);

        return ResponseEntity.ok(toProfile(saved));
    }

    /**
     * 把前端传来的资料保存到 User 实体
     */
    private void updateUserFromRequest(
            User user,
            ProfileUpdateRequest request
    ) {
        user.setName(trimToNull(request.getName()));
        user.setPhone(trimToNull(request.getPhone()));
        user.setEmail(trimToNull(request.getEmail()));
        user.setAddress(trimToNull(request.getAddress()));
        user.setAvatar(trimToNull(request.getAvatar()));
        user.setGender(trimToNull(request.getGender()));
        user.setAge(parseNullableInt(request.getAge()));
        user.setNationality(trimToNull(request.getNationality()));

        // 前端字段：teachSkill / learnSkill
        // 数据库字段：skillOffer / skillWant
        user.setSkillOffer(
                trimToNull(request.getTeachSkill())
        );

        user.setSkillWant(
                trimToNull(request.getLearnSkill())
        );

        // 学习时间段
        user.setTimeSlot(
                trimToNull(request.getTimeSlot())
        );

        // 项目 / 奖项 / 证书
        user.setProjectAwards(
                trimToNull(request.getProjectAwards())
        );

        // 擅长技能等级
        // 注意：等级主要由 Quiz 更新，这里只有前端传了值才覆盖
        String skillOfferLevel = trimToNull(request.getSkillOfferLevel());
        if (skillOfferLevel != null) {
            user.setSkillOfferLevel(skillOfferLevel);
        }

        // 想学习技能等级
        // 注意：等级主要由 Quiz 更新，这里只有前端传了值才覆盖
        String skillWantLevel = trimToNull(request.getSkillWantLevel());
        if (skillWantLevel != null) {
            user.setSkillWantLevel(skillWantLevel);
        }

        // Quiz 分数不在个人资料编辑里修改
        // teachQuizScore / learnQuizScore 只由 QuizService 保存
    }

    /**
     * User 转 ProfileResponse
     */
    private ProfileResponse toProfile(User user) {

        ProfileResponse response =
                new ProfileResponse();

        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setAddress(user.getAddress());
        response.setAvatar(user.getAvatar());
        response.setGender(user.getGender());
        response.setAge(user.getAge());

        // 技能
        response.setTeachSkill(
                user.getSkillOffer()
        );

        response.setLearnSkill(
                user.getSkillWant()
        );

        // 国籍
        response.setNationality(
                user.getNationality()
        );

        // 学习时间段
        response.setTimeSlot(
                user.getTimeSlot()
        );

        // 项目 / 奖项 / 证书
        response.setProjectAwards(
                user.getProjectAwards()
        );

        // 想学习的等级
        response.setSkillWantLevel(
                user.getSkillWantLevel()
        );

        // 会的等级
        response.setSkillOfferLevel(
                user.getSkillOfferLevel()
        );

        // 我会的技能 Quiz 分数
        response.setTeachQuizScore(
                user.getTeachQuizScore()
        );

        // 我想学的技能 Quiz 分数
        response.setLearnQuizScore(
                user.getLearnQuizScore()
        );

        return response;
    }

    /**
     * 返回错误信息
     */
    private Map<String, Object> message(String msg) {

        Map<String, Object> res =
                new LinkedHashMap<>();

        res.put("message", msg);

        return res;
    }

    /**
     * 清理字符串空格
     */
    private String trimToNull(String value) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }

    /**
     * 字符串年龄转数字
     */
    private Integer parseNullableInt(
            String value
    ) {
        String trimmed =
                trimToNull(value);

        if (trimmed == null) {
            return null;
        }

        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}