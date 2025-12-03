package com.example.project3_backend.controllers;


import com.example.project3_backend.model.Notification;
import com.example.project3_backend.model.Setlist;
import com.example.project3_backend.model.User;
import com.example.project3_backend.model.enums.NotificationType;
import com.example.project3_backend.repository.NotificationRepository;
import com.example.project3_backend.repository.UserRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationRepository.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationId(@PathVariable UUID id)
    {
        return notificationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserId(@PathVariable UUID userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notificationRepository.findByUserId(userId));
    }
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationReq request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();

        }
        Notification notification = Notification.builder()
                .user(user)
                .type(request.getType())
                .message(request.getMessage())
                .isRead(false)
                .sentAt(Instant.now())
                .build();
        Notification savedNotification = notificationRepository.save(notification);
        return ResponseEntity.ok(savedNotification);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable @NonNull UUID id, @RequestBody NotificationReq request) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    if (request.getMessage() != null){
                    notification.setMessage(request.getMessage());
                    }
                    if (request.getType() != null){
                        notification.setType(request.getType());
                    }
                    if (request.getIsRead() != null){
                        notification.setRead(request.getIsRead());
                    }
                    notificationRepository.save(notification);
                    return ResponseEntity.ok(notification);



                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable @org.springframework.lang.NonNull UUID id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @Getter
    @Setter
    public static class NotificationReq {
        private UUID userId;
        private String message;
        private NotificationType type;
        private Boolean isRead;
    }

}
