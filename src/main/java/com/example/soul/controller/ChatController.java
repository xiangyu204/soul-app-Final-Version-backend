package com.example.soul.controller;

import com.example.soul.dto.AiSuggestRequest;
import com.example.soul.dto.ChatSendRequest;
import com.example.soul.entity.ChatMessage;
import com.example.soul.entity.ChatRoom;
import com.example.soul.entity.User;
import com.example.soul.repository.ChatMessageRepository;
import com.example.soul.repository.ChatRoomRepository;
import com.example.soul.repository.UserRepository;
import com.example.soul.service.OllamaService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.soul.dto.RatingRequest;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final OllamaService ollamaService;

    public ChatController(
            ChatMessageRepository chatMessageRepository,
            ChatRoomRepository chatRoomRepository,
            UserRepository userRepository,
            OllamaService ollamaService
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.ollamaService = ollamaService;
    }

    // =========================
    // 创建或获取两个人之间的聊天室
    // =========================
    @PostMapping("/direct")
    public Map<String, Object> createDirectChatRoom(@RequestBody ChatSendRequest request) {
        Map<String, Object> result = new HashMap<>();

        if (request.getSenderUsername() == null || request.getSenderUsername().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "senderUsername이 비어 있습니다.");
            return result;
        }

        if (request.getReceiverUsername() == null || request.getReceiverUsername().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "receiverUsername이 비어 있습니다.");
            return result;
        }

        if (request.getSenderUsername().equals(request.getReceiverUsername())) {
            result.put("success", false);
            result.put("message", "자기 자신과 채팅방을 만들 수 없습니다.");
            return result;
        }

        try {
            var sender = userRepository.findByUsername(request.getSenderUsername());
            var receiver = userRepository.findByUsername(request.getReceiverUsername());

            if (sender.isEmpty() || receiver.isEmpty()) {
                result.put("success", false);
                result.put("message", "사용자를 찾을 수 없습니다.");
                return result;
            }

            Long senderId = sender.get().getId();
            Long receiverId = receiver.get().getId();

            var room = chatRoomRepository.findRoomBetweenUsers(senderId, receiverId);

            if (room.isPresent()) {
                result.put("success", true);
                result.put("roomId", room.get().getId());
                result.put("message", "이미 존재하는 채팅방입니다.");
                return result;
            }

            // 创建聊天室
            ChatRoom chatRoom = new ChatRoom();

// 当前时间
            LocalDateTime now = LocalDateTime.now();

// 聊天双方
            chatRoom.setUser1Id(senderId);
            chatRoom.setUser2Id(receiverId);

// 默认信息
            chatRoom.setLastMessage("");
            chatRoom.setLastSenderType("SYSTEM");

// 时间信息
            chatRoom.setLastTime(now);

// 历史匹配时间
            chatRoom.setCreatedAt(now);

// 更新时间
            chatRoom.setUpdatedAt(now);

// 保存聊天室
            ChatRoom savedRoom =
                    chatRoomRepository.save(chatRoom);

            result.put("success", true);
            result.put("roomId", savedRoom.getId());
            result.put("message", "채팅방이 생성되었습니다.");

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "채팅방 생성 실패");
            result.put("error", e.getMessage());
            return result;
        }
    }

    // =========================
    // 发送消息（只有匹配成功用户可发送）
    // =========================
    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody ChatSendRequest request) {
        Map<String, Object> result = new HashMap<>();

        if (request.getSenderUsername() == null || request.getSenderUsername().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "senderUsername이 비어 있습니다.");
            return result;
        }

        if (request.getReceiverUsername() == null || request.getReceiverUsername().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "receiverUsername이 비어 있습니다.");
            return result;
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "content가 비어 있습니다.");
            return result;
        }

        try {
            var sender = userRepository.findByUsername(request.getSenderUsername());
            var receiver = userRepository.findByUsername(request.getReceiverUsername());

            if (sender.isEmpty() || receiver.isEmpty()) {
                result.put("success", false);
                result.put("message", "사용자를 찾을 수 없습니다.");
                return result;
            }

            Long senderId = sender.get().getId();
            Long receiverId = receiver.get().getId();

            var room = chatRoomRepository.findRoomBetweenUsers(senderId, receiverId);

            if (room.isEmpty()) {
                result.put("success", false);
                result.put("message", "매칭된 채팅방이 없습니다.");
                return result;
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderUsername(request.getSenderUsername());
            chatMessage.setReceiverUsername(request.getReceiverUsername());
            chatMessage.setContent(request.getContent());

            ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

            ChatRoom chatRoom = room.get();
            chatRoom.setLastMessage(request.getContent());
            chatRoom.setLastSenderType("USER");
            chatRoom.setLastTime(LocalDateTime.now());

            chatRoomRepository.save(chatRoom);

            result.put("success", true);
            result.put("message", savedMessage);

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "채팅 메시지 저장 실패");
            result.put("error", e.getMessage());
            return result;
        }
    }

    // =========================
    // 获取聊天记录（只有聊天室成员可查看）
    // =========================
    @GetMapping("/messages")
    public List<ChatMessage> getMessages(
            @RequestParam String me,
            @RequestParam String partner
    ) {
        var meUser = userRepository.findByUsername(me);
        var partnerUser = userRepository.findByUsername(partner);

        if (meUser.isEmpty() || partnerUser.isEmpty()) {
            return List.of();
        }

        Long meId = meUser.get().getId();
        Long partnerId = partnerUser.get().getId();

        var room = chatRoomRepository.findRoomBetweenUsers(meId, partnerId);

        if (room.isEmpty()) {
            return List.of();
        }

        return chatMessageRepository.findMessagesBetweenUsers(me, partner);
    }

    // =========================
    // 获取当前用户聊天室列表
    // =========================
    @GetMapping("/rooms")
    public List<Map<String, Object>> getMyRooms(
            @RequestParam String username
    ) {
        var user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            return List.of();
        }

        Long myId = user.get().getId();

        var rooms = chatRoomRepository.findByUser1IdOrUser2Id(myId, myId);

        return rooms.stream().map(room -> {
            Long partnerId;

            if (room.getUser1Id().equals(myId)) {
                partnerId = room.getUser2Id();
            } else {
                partnerId = room.getUser1Id();
            }

            var partner = userRepository.findById(partnerId);

            Map<String, Object> roomData = new HashMap<>();

            roomData.put("roomId", room.getId());
            roomData.put("partnerId", partnerId);
            roomData.put(
                    "partnerUsername",
                    partner.map(u -> u.getUsername()).orElse("")
            );
            roomData.put(
                    "partnerName",
                    partner.map(u -> u.getName()).orElse("")
            );
            // 对方头像
            roomData.put(
                    "partnerAvatar",
                    partner.map(User::getAvatar)
                            .orElse("")
            );

            // 对方平均评分
            roomData.put(
                    "averageRating",
                    partner.map(User::getAverageRating)
                            .orElse(0.0)
            );

            // 对方评分人数
            roomData.put(
                    "ratingCount",
                    partner.map(User::getRatingCount)
                            .orElse(0)
            );
            roomData.put("lastMessage", room.getLastMessage());
            roomData.put("lastTime", room.getLastTime());

            return roomData;

        }).collect(Collectors.toList());
    }

    // =========================
    // AI 学习帮助：解释聊天中的知识点
    // =========================
    @PostMapping("/ai-help")
    public Map<String, Object> aiHelp(@RequestBody AiSuggestRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            String answer = ollamaService.explainKnowledge(request);

            result.put("success", true);
            result.put("answer", answer);

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "AI 학습 도움 생성 실패");
            result.put("error", e.getMessage());

            return result;
        }
    }
    /**
     * =========================
     * 用户评分
     * =========================
     */
    @PostMapping("/rating")
    public Map<String, Object> rateUser(
            @RequestBody RatingRequest request
    ) {

        Map<String, Object> result =
                new HashMap<>();

        try {

            // =========================
            // 评分范围校验
            // =========================
            if (
                    request.getScore() == null
                            || request.getScore() < 1
                            || request.getScore() > 5
            ) {

                result.put("success", false);
                result.put("message", "评分必须在1~5之间");

                return result;
            }

            ChatRoom room =
                    chatRoomRepository.findById(
                            request.getRoomId()
                    ).orElse(null);

            if (room == null) {

                result.put("success", false);
                result.put("message", "聊天室不存在");

                return result;
            }

            var user =
                    userRepository.findByUsername(
                            request.getUsername()
                    );

            if (user.isEmpty()) {

                result.put("success", false);
                result.put("message", "用户不存在");

                return result;
            }

            Long userId =
                    user.get().getId();

            Long targetUserId = null;

            // user1 给 user2 评分
            if (room.getUser1Id().equals(userId)) {

                room.setUser2Rating(
                        request.getScore()
                );

                targetUserId =
                        room.getUser2Id();
            }

            // user2 给 user1 评分
            else if (
                    room.getUser2Id().equals(userId)
            ) {

                room.setUser1Rating(
                        request.getScore()
                );

                targetUserId =
                        room.getUser1Id();
            }

            chatRoomRepository.save(room);

            // 重新计算被评分用户平均分
            recalculateUserRating(
                    targetUserId
            );

            result.put("success", true);
            result.put("message", "评分成功");

            return result;

        } catch (Exception e) {

            result.put("success", false);
            result.put("message", e.getMessage());

            return result;
        }
    }
    /**
     * =========================
     * 查询我的评分
     * =========================
     */
    @GetMapping("/rating")
    public Map<String, Object> getMyRating(
            @RequestParam Long roomId,
            @RequestParam String username
    ) {

        Map<String, Object> result =
                new HashMap<>();

        ChatRoom room =
                chatRoomRepository.findById(roomId)
                        .orElse(null);

        if (room == null) {

            result.put("rating", 0);

            return result;
        }

        var user =
                userRepository.findByUsername(
                        username
                );

        if (user.isEmpty()) {

            result.put("rating", 0);

            return result;
        }

        Long userId =
                user.get().getId();

        Integer rating = 0;

        if (room.getUser1Id().equals(userId)) {

            rating =
                    room.getUser2Rating();
        }

        else if (
                room.getUser2Id().equals(userId)
        ) {

            rating =
                    room.getUser1Rating();
        }

        result.put(
                "rating",
                rating == null ? 0 : rating
        );

        return result;
    }
    /**
     * =========================
     * 重新计算用户平均分
     * =========================
     */
    private void recalculateUserRating(
            Long userId
    ) {

        List<ChatRoom> rooms =
                chatRoomRepository
                        .findByUser1IdOrUser2Id(
                                userId,
                                userId
                        );

        int count = 0;

        double total = 0;

        for (ChatRoom room : rooms) {

            // 别人给当前用户的评分
            if (
                    room.getUser1Id().equals(userId)
                            &&
                            room.getUser1Rating() != null
            ) {

                total += room.getUser1Rating();

                count++;
            }

            // 别人给当前用户的评分
            if (
                    room.getUser2Id().equals(userId)
                            &&
                            room.getUser2Rating() != null
            ) {

                total += room.getUser2Rating();

                count++;
            }
        }

        User user =
                userRepository.findById(userId)
                        .orElse(null);

        if (user == null) {
            return;
        }

        user.setRatingCount(count);

        if (count == 0) {

            user.setAverageRating(0.0);

        } else {

            user.setAverageRating(
                    Math.round(
                            (total / count) * 10
                    ) / 10.0
            );
        }

        userRepository.save(user);
    }
}