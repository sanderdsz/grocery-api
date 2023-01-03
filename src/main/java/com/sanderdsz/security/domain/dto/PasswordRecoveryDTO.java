package com.sanderdsz.security.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRecoveryDTO {

    private String accessToken;

    private String email;

    private String password;

}
