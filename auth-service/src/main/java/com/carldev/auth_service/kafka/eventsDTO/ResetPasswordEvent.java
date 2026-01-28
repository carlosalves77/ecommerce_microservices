package com.carldev.auth_service.kafka.eventsDTO;


public record ResetPasswordEvent(
        String passwordToken,
        String username,
        String userEmail
) {

}

