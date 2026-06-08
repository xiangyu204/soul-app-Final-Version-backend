package com.example.soul.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private String title;

    private String username;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int views;

    private String date;

    private int likes = 0;

    private int commentCount = 0;

    @ElementCollection
    @CollectionTable(
            name = "board_liked_by",
            joinColumns = @JoinColumn(name = "board_id")
    )
    @Column(name = "username")
    private List<String> likedBy = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "board_tags",
            joinColumns = @JoinColumn(name = "board_id")
    )
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Transient
    private String name;

    @Transient
    private String avatar;

    @Transient
    private boolean liked;

    public Board() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public List<String> getLikedBy() { return likedBy; }
    public void setLikedBy(List<String> likedBy) { this.likedBy = likedBy; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }
}