package com.example.soul.controller;

import com.example.soul.dto.MatchHistoryResponse;
import com.example.soul.dto.MatchUserResponse;
import com.example.soul.dto.UserMatchProfileResponse;
import com.example.soul.entity.ChatRoom;
import com.example.soul.entity.User;
import com.example.soul.repository.ChatRoomRepository;
import com.example.soul.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 매칭 API 컨트롤러
 * 匹配功能 API 控制器
 */
@RestController
@RequestMapping("/api/match")
public class MatchQueryController {

    /**
     * 用户数据库
     */
    private final UserRepository userRepository;

    /**
     * 聊天室数据库
     */
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 构造函数注入
     */
    public MatchQueryController(
            UserRepository userRepository,
            ChatRoomRepository chatRoomRepository
    ) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    /**
     * ==================================================
     * 用户详情 + 历史匹配记录
     * GET /api/match/profile/{userId}
     * ==================================================
     */
    @GetMapping("/profile/{userId}")
    public UserMatchProfileResponse getProfile(
            @PathVariable Long userId
    ) {

        // 查询用户
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("用户不存在")
                );

        // 查询用户参与过的聊天室
        List<ChatRoom> rooms =
                chatRoomRepository.findByUser1IdOrUser2Id(
                        userId,
                        userId
                );

        List<MatchHistoryResponse> histories =
                new ArrayList<>();

        // 遍历聊天室
        for (ChatRoom room : rooms) {

            Long partnerId;

            // 找到对方用户ID
            if (room.getUser1Id().equals(userId)) {
                partnerId = room.getUser2Id();
            } else {
                partnerId = room.getUser1Id();
            }

            User partner = userRepository
                    .findById(partnerId)
                    .orElse(null);

            if (partner == null) {
                continue;
            }

            histories.add(
                    new MatchHistoryResponse(
                            partner.getId(),
                            partner.getName() == null ||
                                    partner.getName().isBlank()
                                    ? partner.getUsername()
                                    : partner.getName(),
                            partner.getAvatar(),
                            room.getCreatedAt()
                    )
            );
        }

        // 时间倒序排序
        histories.sort(
                Comparator.comparing(
                        MatchHistoryResponse::getMatchTime,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );

        // 返回用户资料
        // 返回用户资料
        UserMatchProfileResponse response =
                new UserMatchProfileResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setAge(user.getAge());
        response.setNationality(user.getNationality());
        response.setAvatar(user.getAvatar());

        // 技能信息
        response.setSkillOffer(user.getSkillOffer());
        response.setSkillWant(user.getSkillWant());

        // 平均评分
        response.setAverageRating(
                user.getAverageRating()
        );

        // 评分人数
        response.setRatingCount(
                user.getRatingCount()
        );

        // 历史匹配记录
        response.setHistories(histories);

        return response;
    }

    /**
     * ==================================================
     * 技能匹配
     * GET /api/match
     * ==================================================
     */
    @GetMapping
    public List<MatchUserResponse> match(

            // 我会的技能
            @RequestParam(required = false)
            String haveSkill,

            // 我想学的技能
            @RequestParam(required = false)
            String wantSkill,

            // 学习时间段
            @RequestParam(required = false)
            String timeSlot,

            // 想学习等级
            @RequestParam(required = false)
            String skillWantLevel,

            // 会的技能等级
            @RequestParam(required = false)
            String skillOfferLevel,

            // 返回人数
            @RequestParam(defaultValue = "5")
            int limit
    ) {

        String have = normalize(haveSkill);
        String want = normalize(wantSkill);
        String time = normalize(timeSlot);

        String wantLevel = normalize(skillWantLevel);
        String offerLevel = normalize(skillOfferLevel);

        // 查询所有用户
        List<User> all = userRepository.findAll();

        List<User> filtered =
                new ArrayList<>();

        for (User u : all) {

            boolean ok = true;

            // 对方会我想学的
            if (want != null) {
                ok = ok && containsSkill(
                        u.getSkillOffer(),
                        want
                );
            }

            // 对方想学我会的
            if (have != null) {
                ok = ok && containsSkill(
                        u.getSkillWant(),
                        have
                );
            }

            // 时间段匹配
            if (time != null) {

                ok = ok && time.equalsIgnoreCase(
                        normalize(u.getTimeSlot())
                );
            }

            // 学习等级匹配
            if (wantLevel != null) {

                ok = ok && wantLevel.equalsIgnoreCase(
                        normalize(u.getSkillWantLevel())
                );
            }

            // 擅长等级匹配
            if (offerLevel != null) {

                ok = ok && offerLevel.equalsIgnoreCase(
                        normalize(u.getSkillOfferLevel())
                );
            }

            if (ok) {
                filtered.add(u);
            }
        }

        // 随机排序
        Collections.shuffle(filtered);

        if (limit < 0) {
            limit = 0;
        }

        return filtered.stream()
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * User -> MatchUserResponse
     */
    /**
     * User -> MatchUserResponse
     * 用户实体转匹配返回DTO
     */
    private MatchUserResponse toResponse(User u) {

        String name = u.getName();

        // 没填写昵称则显示账号
        if (name == null ||
                name.trim().isEmpty()) {

            name = u.getUsername();
        }

        return new MatchUserResponse(

                // 用户ID
                u.getId(),

                // 用户账号
                u.getUsername(),

                // 显示名称
                name,

                // 年龄
                u.getAge(),

                // 性别
                u.getGender(),

                // 国籍
                u.getNationality(),

                // 头像
                u.getAvatar(),

                // 擅长技能
                splitSkills(u.getSkillOffer()),

                // 想学习技能
                splitSkills(u.getSkillWant()),

                // 时间段
                u.getTimeSlot(),

                // 想学习等级
                u.getSkillWantLevel(),

                // 擅长等级
                u.getSkillOfferLevel(),

                // 平均评分
                u.getAverageRating(),

                // 评分人数
                u.getRatingCount()
        );
    }

    /**
     * 技能字符串转List
     */
    private List<String> splitSkills(String raw) {

        String v = normalize(raw);

        if (v == null) {
            return List.of();
        }

        String[] parts = v.split(",");

        List<String> res =
                new ArrayList<>();

        for (String p : parts) {

            String t = normalize(p);

            if (t != null) {
                res.add(t);
            }
        }

        return res;
    }

    /**
     * 判断技能是否匹配
     */
    private boolean containsSkill(
            String raw,
            String target
    ) {

        if (raw == null) {
            return false;
        }

        String hay =
                raw.toLowerCase(Locale.ROOT);

        String needle =
                target.toLowerCase(Locale.ROOT);

        for (String part : hay.split(",")) {

            String t = part.trim();

            if (!t.isEmpty()
                    && t.equals(needle)) {

                return true;
            }
        }

        return hay.contains(needle);
    }

    /**
     * 去空格
     */
    private String normalize(String v) {

        if (v == null) {
            return null;
        }

        String t = v.trim();

        return t.isEmpty()
                ? null
                : t;
    }
}