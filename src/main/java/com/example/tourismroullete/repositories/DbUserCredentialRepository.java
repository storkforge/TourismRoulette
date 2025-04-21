
package com.example.tourismroullete.repositories;

import com.example.tourismroullete.entities.PasskeyUser;
import com.example.tourismroullete.entities.WebauthnCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.webauthn.api.*;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;

import java.util.*;
import java.util.stream.Collectors;

public class DbUserCredentialRepository implements UserCredentialRepository {

    private static final Logger log = LoggerFactory.getLogger(DbUserCredentialRepository.class);

    private final PasskeyCredentialRepository credentialRepository;
    private final PasskeyuserRepository passkeyUserRepository;

    public DbUserCredentialRepository(PasskeyCredentialRepository credentialRepository, PasskeyuserRepository passkeyUserRepository) {
        this.credentialRepository = credentialRepository;
        this.passkeyUserRepository = passkeyUserRepository;
    }

    @Override
    public void delete(Bytes credentialId) {
        log.info("delete: id={}", credentialId.toBase64UrlString());
        credentialRepository.findByCredentialId(credentialId.toBase64UrlString())
                .ifPresent(credentialRepository::delete);
    }

    @Override
    public void save(CredentialRecord credentialRecord) {
        passkeyUserRepository.findByExternalId(credentialRecord.getUserEntityUserId().toBase64UrlString())
                .ifPresent(user -> {
                    credentialRepository.findByCredentialId(credentialRecord.getCredentialId().toBase64UrlString())
                            .map(existingCredential -> credentialRepository.save(toPasskeyCredential(existingCredential, credentialRecord, user)))
                            .orElseGet(() -> credentialRepository.save(toPasskeyCredential(credentialRecord, user)));
                    log.info("save: user={}, externalId={}, label={}", user.getName(), user.getExternalId(), credentialRecord.getLabel());
                });
    }

    @Override
    public CredentialRecord findByCredentialId(Bytes credentialId) {
        log.info("findByCredentialId: id={}", credentialId.toBase64UrlString());
        return credentialRepository.findByCredentialId(credentialId.toBase64UrlString())
                .map(cred -> {
                    PasskeyUser user = cred.getPasskeyUser();
                    return toCredentialRecord(cred, Bytes.fromBase64(user.getExternalId()));
                })
                .orElse(null);
    }

    @Override
    public List<CredentialRecord> findByUserId(Bytes userId) {
        log.info("findByUserId: userId={}", userId);
        Optional<PasskeyUser> user = passkeyUserRepository.findByExternalId(userId.toBase64UrlString());
        return user.map(passkeyUser -> credentialRepository.findByPasskeyUserId(passkeyUser.getId())
                        .stream()
                        .map(cred -> toCredentialRecord(cred, Bytes.fromBase64(passkeyUser.getExternalId())))
                        .collect(Collectors.toList()))
                .orElseGet(List::of);
    }

    // --- Mapping helpers ---

    private static CredentialRecord toCredentialRecord(WebauthnCredentials credential, Bytes userId) {
        log.info("toCredentialRecord: credentialId={}, userId={}", credential.getCredentialId(), userId);
        // If you add more fields to WebauthnCredentials, map them here.
        return ImmutableCredentialRecord.builder()
                .userEntityUserId(userId)
                .label("passkey") // If you add a label field, use credential.getLabel()
                .credentialType(PublicKeyCredentialType.PUBLIC_KEY)
                .credentialId(Bytes.fromBase64(credential.getCredentialId()))
                .publicKey(ImmutablePublicKeyCose.fromBase64(credential.getPublicKeyCose()))
                .signatureCount(credential.getSignCount())
                .build();
    }

    private static WebauthnCredentials toPasskeyCredential(WebauthnCredentials credential, CredentialRecord credentialRecord, PasskeyUser user) {
        credential.setPasskeyUser(user);
        credential.setCredentialId(credentialRecord.getCredentialId().toBase64UrlString());
        credential.setPublicKeyCose(credentialRecord.getPublicKey().toString());
        credential.setSignCount(credentialRecord.getSignatureCount());
        // If you add more fields to WebauthnCredentials, map them here.
        return credential;
    }

    private static WebauthnCredentials toPasskeyCredential(CredentialRecord credentialRecord, PasskeyUser user) {
        WebauthnCredentials credential = new WebauthnCredentials();
        credential.setPasskeyUser(user);
        credential.setCredentialId(credentialRecord.getCredentialId().toBase64UrlString());
        credential.setPublicKeyCose(credentialRecord.getPublicKey().toString());
        credential.setSignCount(credentialRecord.getSignatureCount());
        // If you add more fields to WebauthnCredentials, map them here.
        return credential;
    }
}
