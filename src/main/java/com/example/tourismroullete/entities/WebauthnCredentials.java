package com.example.tourismroullete.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "webauthn_credentials",
        indexes = {
                @Index(name = "idx_credential_id", columnList = "credential_id", unique = true)
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebauthnCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credential_id", nullable = false, unique = true)
    private String credentialId;

    @Column(name = "public_key_cose", nullable = false)
    private String publicKeyCose;

    @Column(nullable = false)
    private Long signCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "passkey_user_id", nullable = false)
    private PasskeyUser passkeyUser;
}