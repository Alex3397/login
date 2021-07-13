package com.alex.login.registration;

import com.alex.login.registration.email.EmailSender;
import com.alex.login.registration.token.ConfirmationToken;
import com.alex.login.registration.token.ConfirmationTokenService;
import com.alex.login.users.appuser.AppUser;
import com.alex.login.users.appuser.AppUserRepository;
import com.alex.login.users.appuser.AppUserRole;
import com.alex.login.users.appuser.AppUserService;
import com.alex.login.users.client.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final ClientService clientService;
    private final EmailValidator validator;
    private final ConfirmationTokenService confirmationTokenService;
    private final AppUserRepository appUserRepository;
    private final EmailSender emailSender;

    @Transactional
    public String confirmToken(String token) {

        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiresAt = confirmationToken.getExpiresAt();

        if (expiresAt.isBefore(LocalDateTime.now()) && !confirmationToken.getAppUser().isEnabled()) {
            String newToken = appUserService.revalidateToken(confirmationToken.getAppUser());
            String link = "http://localhost:8080/api/v1/registration/";
            if (confirmationToken.getAppUser().getAppUserRole() == AppUserRole.USER) {
                link = link + "user" + "/confirm?token=" + newToken;
            } else if (confirmationToken.getAppUser().getAppUserRole() == AppUserRole.ADMIN) {
                link = link + "admin" + "/confirm?token=" + newToken;
            }
            emailSender.send(confirmationToken.getAppUser().getEmail(), buildRevalidationEmail(confirmationToken.getAppUser().getUsername(),link));
            return "token expired, new token is: " + newToken;
        } else if (confirmationToken.getAppUser().isEnabled()) {
            throw new IllegalStateException("account has been enabled");
        }

        confirmationTokenService.setConfirmedAt(token);
        return "confirmed " + appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());
    }

    public String registerUser(UserRegistrationRequest request) {
        boolean isValidEmail = validator.test(request.getEmail());
        boolean isEmailTaken = appUserRepository.findByEmail(request.getEmail()).isPresent();
        if (!isValidEmail) {
            throw new IllegalStateException(String.format("%s is not a valid email",request.getEmail()));
        } else if (isEmailTaken) {
            throw new IllegalStateException(String.format("%s is already taken",request.getEmail()));
        }
        AppUser appUser = new AppUser(request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                AppUserRole.USER);
        String token = appUserService.signUpUser( appUser );
        String link = "http://localhost:8080/api/v1/registration/user/confirm?token=" + token;
        emailSender.send(request.getEmail(), buildConfirmationEmail(request.getUsername(),link));
        return token;
    }

    public String registerAdmin(UserRegistrationRequest request) {
        boolean isValidEmail = validator.test(request.getEmail());
        boolean isEmailTaken = appUserRepository.findByEmail(request.getEmail()).isPresent();
        if (!isValidEmail) {
            throw new IllegalStateException(String.format("%s is not a valid email",request.getEmail()));
        } else if (isEmailTaken) {
            throw new IllegalStateException(String.format("%s is already taken",request.getEmail()));
        }
        AppUser appUser = new AppUser(
                request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                AppUserRole.ADMIN
        );
        String token = appUserService.signUpUser( appUser );
        String link = "http://localhost:8080/api/v1/registration/user/confirm?token=" + token;
        emailSender.send(request.getEmail(), buildConfirmationEmail(request.getUsername(),link));
        return token;
    }

    public String registerClient(ClientRegistrationRequest request) {
        System.out.println(request);
        for (ClientAddresses clientAddresses : request.getAddressList()) {
            System.out.println(clientAddresses.getCep());
            System.out.println(clientAddresses.getLog());
            System.out.println(clientAddresses.getBar());
            System.out.println(clientAddresses.getUf());
            System.out.println(clientAddresses.getComplement());
        }
        for (ClientEmails clientEmails : request.getEmailList()) {
            boolean isValidEmail = validator.test(clientEmails.getEmail());
            if (!isValidEmail) {
                throw new IllegalStateException(String.format("%s is not a valid email",clientEmails.getEmail()));
            }
            System.out.println(clientEmails.getEmail());
        }
        for (ClientPhones clientPhones : request.getPhoneList()) {
            System.out.println(clientPhones.getPhone());
        }
        return clientService.signUpClient( new Client(
                request.getName(),
                request.getCpf(),
                request.getAddressList(),
                request.getEmailList(),
                request.getPhoneList()
                )
        );
    }

    private String buildConfirmationEmail(String name, String link) {
        Path file = Path.of(System.getProperty("user.dir"),"/front-end/src/emails/Confirm Email/confirmEmail.html");
        String imagePath = "https://d1oco4z2z1fhwp.cloudfront.net/templates/default/3971";
        String rawContent = "";
        try {
            rawContent = Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawContent.replace("${USER}",name.toUpperCase()).replace("${LINK}",link).replace("${IMAGELINK}",imagePath);
    }

    private String buildRevalidationEmail(String name, String link) {
        Path file = Path.of(System.getProperty("user.dir"),"/front-end/src/emails/Revalidate Token/revalidateToken.html");
        String imagePath = "https://d1oco4z2z1fhwp.cloudfront.net/templates/default/3986";
        String rawContent = "";
        try {
            rawContent = Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawContent.replace("${USER}",name.toUpperCase()).replace("${LINK}",link).replace("${IMAGELINK}",imagePath);
    }

    private String buildResetPasswordEmail(String name, String link) {
        Path file = Path.of(System.getProperty("user.dir"),"/front-end/src/emails/Reset Password/resetPassword.html");
        String imagePath = "https://d1oco4z2z1fhwp.cloudfront.net/templates/default/4056";
        String rawContent = "";
        try {
            rawContent = Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawContent.replace("${USER}",name.toUpperCase()).replace("${LINK}",link).replace("${IMAGELINK}",imagePath);
    }

}
