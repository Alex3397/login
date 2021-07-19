package com.alex.login.users.appuser;

import com.alex.login.registration.token.ConfirmationToken;
import com.alex.login.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user %s not found";
    private final AppUserRepository appUserRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        if (appUserRepository.findByUsername(username).isPresent()){
            return appUserRepository.findByUsername(username).orElseThrow(() ->
                    new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,username)));
        } else if (appUserRepository.findByEmail(username).isPresent()) {
            return appUserRepository.findByEmail(username).orElseThrow(() ->
                    new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,username)));
        } else throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,username));
    }

    public String signUpUser(AppUser appUser) {
        Optional<AppUser> userByUsername = appUserRepository.findByUsername(appUser.getUsername());
        Optional<AppUser> userByEmail = appUserRepository.findByEmail(appUser.getEmail());
        boolean userExists = appUserRepository.findByUsername(appUser.getUsername()).isPresent();
        boolean emailIsTaken = appUserRepository.findByEmail(appUser.getEmail()).isPresent();

        if (userExists && emailIsTaken) {
            if (userByEmail.isPresent() && userByUsername.isPresent()) {
                if (userByEmail.get().getId().equals(userByUsername.get().getId())) {
                    if (userByEmail.get().isEnabled()) {
                        throw new IllegalStateException("user is already singed up");
                    } else {
                        appUserRepository.save(appUser);
                        return "revalidating expired token: " + revalidateToken(appUser);
                    }
                } if (!userByEmail.get().getId().equals(userByUsername.get().getId())) {
                    throw new IllegalStateException("username and email are already in use and userByEmail && userByUsername are present and IDs are different");
                }
                throw new IllegalStateException("username and email are already in use and userByEmail && userByUsername are present");
            } else if (userByEmail.isPresent()) {
                throw new IllegalStateException("username and email are already in use and userByUsername is not present");
            } else if (userByUsername.isPresent()) {
                throw new IllegalStateException("username and email are already in use and userByEmail is not present");
            }
        }
        if (userExists) {throw new IllegalStateException("username is already in use");}
        if (emailIsTaken) {throw new IllegalStateException("email is already taken");}

        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);

        return createToken(appUser);
    }

    public String createToken(AppUser appUser) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken ConfirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(25),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(ConfirmationToken);
        return token;
    }

    public String revalidateToken(AppUser appUser) {
        String newToken = UUID.randomUUID().toString();
        ConfirmationToken reConfirmationToken = new ConfirmationToken(
                newToken,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(25),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(reConfirmationToken);
        return newToken;
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
}
