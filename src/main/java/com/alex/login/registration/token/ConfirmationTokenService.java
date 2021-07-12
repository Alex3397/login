package com.alex.login.registration.token;

import com.alex.login.users.appuser.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }
    public void removeConfirmationToken(ConfirmationToken token) {confirmationTokenRepository.delete(token);}
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }
    public Optional<ConfirmationToken> getTokenByAppUser(AppUser appUser) {
        return confirmationTokenRepository.findByAppUser(appUser);
    }
    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
