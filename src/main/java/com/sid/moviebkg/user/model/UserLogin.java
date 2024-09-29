package com.sid.moviebkg.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "T_User")
public class UserLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "A_Usr_Id")
    private String userId;
    @Column(name = "A_Usr_Nm")
    private String username;
    @Column(name = "A_Email")
    private String email;
    @Column(name = "A_Pswd")
    private String password;
    @Column(name = "A_Fst_Nm")
    private String firstName;
    @Column(name = "A_Last_Nm")
    private String lastName;
    @Column(name = "A_Phn")
    private String phone;
    @Column(name = "A_Role")
    private String role;
    @Column(name = "A_Cr_Dtm")
    private LocalDateTime createdDateTime;
    @Column(name = "A_Upd_Dtm")
    private LocalDateTime updatedDateTime;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.EAGER
    )
    private List<UserPreference> preferences;
}
