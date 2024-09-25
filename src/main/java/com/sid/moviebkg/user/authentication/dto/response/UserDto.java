package com.sid.moviebkg.user.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String token;
}
