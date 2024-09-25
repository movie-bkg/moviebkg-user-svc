package com.sid.moviebkg.user.authentication.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequestDto {
    private String login;
    private String password;
}
