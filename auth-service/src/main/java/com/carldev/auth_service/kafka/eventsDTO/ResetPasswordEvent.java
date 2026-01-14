package com.carldev.auth_service.kafka.eventsDTO;


public record ResetPasswordEvent(
        String passwordToken,
        String username
) {

    public static ResetPasswordEvent fromEntity(String passwordToken, String username) {
        return new ResetPasswordEvent(
             passwordToken, username
        );
    }
}
