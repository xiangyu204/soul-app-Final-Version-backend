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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/match")
@CrossOrigin(origins = "*")
public class MatchQueryController {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public MatchQueryController(
            UserRepository userRepository,
            ChatRoomRepository chatRoomRepository
    ) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }


    // =====================================================
    // 用户详情 + 匹配历史
    // GET /api/match/profile/{userId}
    // =====================================================

    @GetMapping("/profile/{userId}")
    public UserMatchProfileResponse getProfile(
            @PathVariable Long userId
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new RuntimeException("用户不存在")
                );

        List<ChatRoom> rooms =
                chatRoomRepository
                        .findByUser1IdOrUser2Id(
                                userId,
                                userId
                        );

        List<MatchHistoryResponse> histories =
                new ArrayList<>();


        for (ChatRoom room : rooms) {

            Long partnerId;

            if (room.getUser1Id().equals(userId)) {
                partnerId = room.getUser2Id();
            } else {
                partnerId = room.getUser1Id();
            }

            User partner =
                    userRepository
                            .findById(partnerId)
                            .orElse(null);

            if (partner == null) {
                continue;
            }

            histories.add(
                    new MatchHistoryResponse(
                            partner.getId(),

                            partner.getName() == null
                                    || partner.getName().isBlank()
                                    ? partner.getUsername()
                                    : partner.getName(),

                            partner.getAvatar(),

                            room.getCreatedAt()
                    )
            );
        }


        histories.sort(
                Comparator.comparing(
                        MatchHistoryResponse::getMatchTime,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );


        UserMatchProfileResponse response =
                new UserMatchProfileResponse();

        response.setId(
                user.getId()
        );

        response.setUsername(
                user.getUsername()
        );

        response.setName(
                user.getName()
        );

        response.setAge(
                user.getAge()
        );

        response.setNationality(
                user.getNationality()
        );

        response.setAvatar(
                user.getAvatar()
        );

        response.setSkillOffer(
                user.getSkillOffer()
        );

        response.setSkillWant(
                user.getSkillWant()
        );

        response.setAverageRating(
                user.getAverageRating()
        );

        response.setRatingCount(
                user.getRatingCount()
        );

        response.setHistories(
                histories
        );

        return response;
    }


    // =====================================================
    // 推荐可匹配用户
    //
    // GET /api/match?username=xiangyu
    //
    // 排除：
    // 1. 自己
    // 2. 已经匹配的人
    // =====================================================

    @GetMapping
    public List<MatchUserResponse> match(

            @RequestParam
            String username,

            @RequestParam(required = false)
            String haveSkill,

            @RequestParam(required = false)
            String wantSkill,

            @RequestParam(required = false)
            String timeSlot,

            @RequestParam(required = false)
            String skillWantLevel,

            @RequestParam(required = false)
            String skillOfferLevel,

            @RequestParam(defaultValue = "12")
            int limit
    ) {

        var currentUserOptional =
                userRepository.findByUsername(
                        username
                );

        if (currentUserOptional.isEmpty()) {
            return List.of();
        }

        User currentUser =
                currentUserOptional.get();

        Long currentUserId =
                currentUser.getId();


        /*
         * 找出已经匹配的人
         */
        List<ChatRoom> existingRooms =
                chatRoomRepository
                        .findByUser1IdOrUser2Id(
                                currentUserId,
                                currentUserId
                        );


        Set<Long> matchedUserIds =
                new HashSet<>();


        for (ChatRoom room : existingRooms) {

            if (
                    room.getUser1Id()
                            .equals(currentUserId)
            ) {

                matchedUserIds.add(
                        room.getUser2Id()
                );

            } else {

                matchedUserIds.add(
                        room.getUser1Id()
                );
            }
        }


        String have =
                normalize(
                        haveSkill
                );

        String want =
                normalize(
                        wantSkill
                );

        String time =
                normalize(
                        timeSlot
                );

        String wantLevel =
                normalize(
                        skillWantLevel
                );

        String offerLevel =
                normalize(
                        skillOfferLevel
                );


        List<User> all =
                userRepository.findAll();

        List<User> filtered =
                new ArrayList<>();


        for (User u : all) {

            /*
             * 排除自己
             */
            if (
                    u.getId()
                            .equals(currentUserId)
            ) {
                continue;
            }


            /*
             * 排除已经匹配的人
             */
            if (
                    matchedUserIds.contains(
                            u.getId()
                    )
            ) {
                continue;
            }


            boolean ok = true;


            /*
             * 对方会我想学的
             */
            if (want != null) {

                ok =
                        ok
                                && containsSkill(
                                u.getSkillOffer(),
                                want
                        );
            }


            /*
             * 对方想学我会的
             */
            if (have != null) {

                ok =
                        ok
                                && containsSkill(
                                u.getSkillWant(),
                                have
                        );
            }


            /*
             * 时间
             */
            if (time != null) {

                ok =
                        ok
                                && time.equalsIgnoreCase(
                                normalize(
                                        u.getTimeSlot()
                                )
                        );
            }


            /*
             * 想学习等级
             */
            if (wantLevel != null) {

                ok =
                        ok
                                && wantLevel.equalsIgnoreCase(
                                normalize(
                                        u.getSkillWantLevel()
                                )
                        );
            }


            /*
             * 擅长等级
             */
            if (offerLevel != null) {

                ok =
                        ok
                                && offerLevel.equalsIgnoreCase(
                                normalize(
                                        u.getSkillOfferLevel()
                                )
                        );
            }


            if (ok) {
                filtered.add(u);
            }
        }


        Collections.shuffle(
                filtered
        );


        if (limit < 0) {
            limit = 0;
        }


        return filtered
                .stream()
                .limit(limit)
                .map(this::toResponse)
                .collect(
                        Collectors.toList()
                );
    }


    // =====================================================
    // 获取当前用户已经匹配的人
    //
    // GET /api/match/mine?username=xiangyu
    // =====================================================

    @GetMapping("/mine")
    public List<MatchUserResponse> getMyMatches(

            @RequestParam
            String username
    ) {

        var currentUserOptional =
                userRepository.findByUsername(
                        username
                );


        if (
                currentUserOptional.isEmpty()
        ) {
            return List.of();
        }


        Long currentUserId =
                currentUserOptional
                        .get()
                        .getId();


        List<ChatRoom> rooms =
                chatRoomRepository
                        .findByUser1IdOrUser2Id(
                                currentUserId,
                                currentUserId
                        );


        /*
         * 最新匹配排前面
         */
        rooms.sort(

                Comparator.comparing(

                        ChatRoom::getCreatedAt,

                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );


        List<MatchUserResponse> result =
                new ArrayList<>();


        for (ChatRoom room : rooms) {

            Long partnerId;


            if (
                    room.getUser1Id()
                            .equals(currentUserId)
            ) {

                partnerId =
                        room.getUser2Id();

            } else {

                partnerId =
                        room.getUser1Id();
            }


            User partner =
                    userRepository
                            .findById(
                                    partnerId
                            )
                            .orElse(null);


            if (partner != null) {

                result.add(
                        toResponse(
                                partner
                        )
                );
            }
        }


        return result;
    }


    // =====================================================
    // User -> DTO
    // =====================================================

    private MatchUserResponse toResponse(
            User u
    ) {

        String name =
                u.getName();


        if (
                name == null
                        || name
                        .trim()
                        .isEmpty()
        ) {

            name =
                    u.getUsername();
        }


        return new MatchUserResponse(

                u.getId(),

                u.getUsername(),

                name,

                u.getAge(),

                u.getGender(),

                u.getNationality(),

                u.getAvatar(),

                splitSkills(
                        u.getSkillOffer()
                ),

                splitSkills(
                        u.getSkillWant()
                ),

                u.getTimeSlot(),

                u.getSkillWantLevel(),

                u.getSkillOfferLevel(),

                u.getAverageRating(),

                u.getRatingCount()
        );
    }


    // =====================================================
    // 技能字符串 -> List
    // =====================================================

    private List<String> splitSkills(
            String raw
    ) {

        String v =
                normalize(
                        raw
                );


        if (v == null) {

            return List.of();
        }


        String[] parts =
                v.split(",");


        List<String> result =
                new ArrayList<>();


        for (String part : parts) {

            String value =
                    normalize(
                            part
                    );


            if (value != null) {

                result.add(
                        value
                );
            }
        }


        return result;
    }


    // =====================================================
    // 技能匹配
    // =====================================================

    private boolean containsSkill(

            String raw,

            String target
    ) {

        if (raw == null) {
            return false;
        }


        String hay =
                raw.toLowerCase(
                        Locale.ROOT
                );


        String needle =
                target.toLowerCase(
                        Locale.ROOT
                );


        for (
                String part :
                hay.split(",")
        ) {

            String value =
                    part.trim();


            if (
                    !value.isEmpty()
                            && value.equals(
                            needle
                    )
            ) {

                return true;
            }
        }


        return hay.contains(
                needle
        );
    }


    private String normalize(
            String value
    ) {

        if (value == null) {
            return null;
        }


        String trimmed =
                value.trim();


        return trimmed.isEmpty()
                ? null
                : trimmed;
    }
}