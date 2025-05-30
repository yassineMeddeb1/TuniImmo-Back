package com.pfe.BienImmobilier.model;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
