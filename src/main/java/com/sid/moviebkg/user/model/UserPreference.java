package com.sid.moviebkg.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
@Entity
@Table(name = "T_User_Pref")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "A_Usr_Pref_Id")
    private String preferenceId;

    @ManyToOne
    @JoinColumn(
            name = "A_Usr_Id",
            referencedColumnName = "A_Usr_Id"
    )
    private UserLogin user;

    @Column(name = "A_Genre")
    private String genre;
    @Column(name = "A_Lang")
    private String language;
    @Column(name = "A_Cr_Dtm")
    private LocalDateTime createdDateTime;
    @Column(name = "A_Upd_Dtm")
    private LocalDateTime updatedDateTime;
}
