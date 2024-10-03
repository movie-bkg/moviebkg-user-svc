package com.sid.moviebkg.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "user")
@Entity
@Table(name = "T_Card_Details")
public class CardDetails {

    @Id
    @UuidGenerator
    @Column(name = "A_Card_Details_Id")
    private String cardDetailsId;

    @ManyToOne
    @JoinColumn(
            name = "A_Usr_Id",
            referencedColumnName = "A_Usr_Id"
    )
    private UserLogin user;

    @Column(name = "A_Crd_No")
    private String cardNo;
    @Column(name = "A_Crd_Type")
    private String cardType;
    @Column(name = "A_Exp_Dt")
    private LocalDate expiryDate;
    @Column(name = "A_Bill_Addrs")
    private String billingAddress;
    @Column(name = "A_Cr_Dtm")
    private LocalDateTime createdDateTime;
    @Column(name = "A_Upd_Dtm")
    private LocalDateTime updatedDateTime;
}
