package com.example.soul.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 聊天用户1
     */
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    /**
     * 聊天用户2
     */
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    /**
     * 聊天室类型
     * PRIVATE / GROUP
     */
    @Column(name = "room_type")
    private String roomType;

    /**
     * 是否开启AI
     */
    @Column(name = "ai_enabled")
    private Boolean aiEnabled;

    /**
     * 最后一条消息
     */
    @Column(name = "last_message", columnDefinition = "TEXT")
    private String lastMessage;

    /**
     * 最后发送者类型
     */
    @Column(name = "last_sender_type")
    private String lastSenderType;

    /**
     * 最后消息时间
     */
    @Column(name = "last_time")
    private LocalDateTime lastTime;

    @Column(name = "user1_rating")
    private Integer user1Rating;

    @Column(name = "user2_rating")
    private Integer user2Rating;

    /**
     * 创建时间
     * 自动生成
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 自动维护
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ChatRoom() {
    }

    public Long getId() {
        return id;
    }

    public Long getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }

    public Long getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Boolean getAiEnabled() {
        return aiEnabled;
    }

    public void setAiEnabled(Boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastSenderType() {
        return lastSenderType;
    }

    public void setLastSenderType(String lastSenderType) {
        this.lastSenderType = lastSenderType;
    }

    public LocalDateTime getLastTime() {
        return lastTime;
    }

    public void setLastTime(LocalDateTime lastTime) {
        this.lastTime = lastTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * user2给user1的评分
     */
    public Integer getUser1Rating() {
        return user1Rating;
    }

    public void setUser1Rating(Integer user1Rating) {
        this.user1Rating = user1Rating;
    }

    /**
     * user1给user2的评分
     */
    public Integer getUser2Rating() {
        return user2Rating;
    }

    public void setUser2Rating(Integer user2Rating) {
        this.user2Rating = user2Rating;
    }
}