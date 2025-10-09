package com.viaz.dashboard.auth.dto;

import com.viaz.dashboard.features.user.ViazUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponseDTO {
    private UserInfo user;
    private List<String> effectivePermissions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private String email;
        private String username;
        private List<String> roles;
        private ViazUser.UserStatus status;
    }

}

