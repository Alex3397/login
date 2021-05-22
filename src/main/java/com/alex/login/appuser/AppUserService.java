package com.alex.login.appuser;

import com.alex.login.client.Client;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user %s not found";
    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,username)));
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository.findByUsername(appUser.getUsername()).isPresent();
        boolean emailTaken = appUserRepository.findByEmail(appUser.getEmail()).isPresent();
        if (userExists) {
            throw new IllegalStateException("username is already in use");
        }
        if (emailTaken) {
            throw new IllegalStateException("email is already taken");
        }
        appUserRepository.save(appUser);
        //Todo: send confirmation token
        return String.format("%1s\n%2s\n%3s\n%4s",appUser.getUsername(),appUser.getPassword(),appUser.getAppUserRole(),appUser.getEmail());
    }
}
