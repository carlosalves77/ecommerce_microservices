package com.carldev.notification_service.dto;

public record ResetPasswordConsumer (
        String passwordToken,
        String username,
        String userEmail
){
}
