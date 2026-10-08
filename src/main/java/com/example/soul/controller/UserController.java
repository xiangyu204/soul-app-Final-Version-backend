package com.example.soul.controller;

import com.example.soul.entity.User;
import com.example.soul.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * 用户信息相关控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;


    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }


    // =====================================================
    // 获取用户个人资料
    //
    // GET:
    // /api/user/profile?username=xiangyu
    // =====================================================

    @GetMapping("/profile")
    public User getUserProfile(
            @RequestParam String username
    ) {

        log.info(
                "Fetching profile for username: {}",
                username
        );


        return userService
                .findByUsername(username)
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "User not found: "
                                                + username
                                )
                );
    }


    // =====================================================
    // 修改用户个人资料
    //
    // PUT:
    // /api/user/profile
    // =====================================================

    @PutMapping("/profile")
    public User updateProfile(
            @RequestBody User request
    ) {

        log.info(
                "Updating profile for username: {}",
                request.getUsername()
        );


        // username 不能为空
        if (
                request.getUsername() == null
                        ||
                        request.getUsername().isBlank()
        ) {

            throw new RuntimeException(
                    "username is required"
            );
        }


        // =================================================
        // 先读取数据库中的原用户
        //
        // 不可以直接 save(request)
        // 否则 password / role 等没有从 Android
        // 传过来的字段有可能被覆盖
        // =================================================

        User user =
                userService
                        .findByUsername(
                                request.getUsername()
                        )
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "User not found: "
                                                        +
                                                        request.getUsername()
                                        )
                        );


        // =================================================
        // 更新用户允许修改的信息
        // =================================================

        user.setName(
                request.getName()
        );


        user.setAge(
                request.getAge()
        );


        user.setGender(
                request.getGender()
        );


        user.setPhone(
                request.getPhone()
        );


        user.setNationality(
                request.getNationality()
        );


        user.setAddress(
                request.getAddress()
        );


        user.setTimeSlot(
                request.getTimeSlot()
        );


        // =================================================
        // 保存数据库
        // =================================================

        User saved =
                userService.save(
                        user
                );


        log.info(
                "Profile updated successfully: {}",
                saved.getUsername()
        );


        return saved;
    }


    // =====================================================
    // 修改头像
    //
    // POST:
    // /api/user/avatar
    //
    // multipart/form-data:
    //
    // username = xiangyu
    // file = xxx.jpg
    // =====================================================

    @PostMapping(
            value = "/avatar",
            consumes = "multipart/form-data"
    )
    public Map<String, Object> uploadAvatar(

            @RequestPart("username")
            String username,

            @RequestPart("file")
            MultipartFile file

    ) {

        Map<String, Object> result =
                new HashMap<>();


        try {

            // =================================================
            // 用户检查
            // =================================================

            if (
                    username == null
                            ||
                            username.isBlank()
            ) {

                result.put(
                        "success",
                        false
                );

                result.put(
                        "message",
                        "username is required"
                );

                return result;
            }


            User user =
                    userService
                            .findByUsername(
                                    username
                            )
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "User not found: "
                                                            +
                                                            username
                                            )
                            );


            // =================================================
            // 文件检查
            // =================================================

            if (
                    file == null
                            ||
                            file.isEmpty()
            ) {

                result.put(
                        "success",
                        false
                );

                result.put(
                        "message",
                        "파일이 없습니다."
                );

                return result;
            }


            // =================================================
            // 只允许图片
            // =================================================

            String contentType =
                    file.getContentType();


            if (
                    contentType == null
                            ||
                            !contentType.startsWith(
                                    "image/"
                            )
            ) {

                result.put(
                        "success",
                        false
                );

                result.put(
                        "message",
                        "이미지 파일만 업로드할 수 있습니다."
                );

                return result;
            }


            // =================================================
            // 最大 5MB
            // =================================================

            long maxSize =
                    5L
                            *
                            1024
                            *
                            1024;


            if (
                    file.getSize()
                            >
                            maxSize
            ) {

                result.put(
                        "success",
                        false
                );

                result.put(
                        "message",
                        "이미지는 5MB 이하만 가능합니다."
                );

                return result;
            }


            // =================================================
            // 创建保存目录
            //
            // backend/
            // └── uploads/
            //     └── avatars/
            // =================================================

            Path avatarDirectory =
                    Paths
                            .get(
                                    "uploads",
                                    "avatars"
                            )
                            .toAbsolutePath()
                            .normalize();


            Files.createDirectories(
                    avatarDirectory
            );


            // =================================================
            // 根据 MIME 获取扩展名
            //
            // 不直接相信原始文件名
            // =================================================

            String extension;


            switch (contentType) {

                case "image/png":
                    extension = ".png";
                    break;

                case "image/webp":
                    extension = ".webp";
                    break;

                case "image/gif":
                    extension = ".gif";
                    break;

                case "image/jpeg":
                case "image/jpg":
                default:
                    extension = ".jpg";
                    break;
            }


            // =================================================
            // 创建随机文件名
            // =================================================

            String fileName =
                    UUID
                            .randomUUID()
                            .toString()
                            +
                            extension;


            Path target =
                    avatarDirectory.resolve(
                            fileName
                    );


            // =================================================
            // 写入服务器硬盘
            // =================================================

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );


            // =================================================
            // 数据库存储的是相对 URL
            //
            // 例如：
            //
            // /uploads/avatars/xxx.jpg
            // =================================================

            String avatarUrl =
                    "/uploads/avatars/"
                            +
                            fileName;


            user.setAvatar(
                    avatarUrl
            );


            userService.save(
                    user
            );


            // =================================================
            // 返回 Android
            // =================================================

            result.put(
                    "success",
                    true
            );


            result.put(
                    "avatar",
                    avatarUrl
            );


            result.put(
                    "message",
                    "프로필 사진이 변경되었습니다."
            );


            log.info(
                    "Avatar uploaded: username={}, avatar={}",
                    username,
                    avatarUrl
            );


            return result;


        } catch (IOException e) {

            log.error(
                    "Avatar file save failed",
                    e
            );


            result.put(
                    "success",
                    false
            );


            result.put(
                    "message",
                    "파일 저장 실패: "
                            +
                            e.getMessage()
            );


            return result;


        } catch (Exception e) {

            log.error(
                    "Avatar upload failed",
                    e
            );


            result.put(
                    "success",
                    false
            );


            result.put(
                    "message",
                    e.getMessage()
            );


            return result;
        }
    }
}