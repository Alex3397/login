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
            emailSender.send(confirmationToken.getAppUser().getEmail(), buildEmail(confirmationToken.getAppUser().getUsername(),link));
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
        emailSender.send(request.getEmail(), buildEmail(request.getUsername(),link));
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
        emailSender.send(request.getEmail(), buildEmail(request.getUsername(),link));
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

    private String buildEmail(String name, String link) {
        return "<div style=\"width:100%!important;min-width:100%;box-sizing:border-box;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;text-align:left;line-height:1.3;font-size:16px;margin:0;padding:0;/*! background-color: #0a0a0a; */\">" +
                "\n" +
                "  <table class=\"m_130184285916875387body\" style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;height:100%;width:100%;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0;/*! background-color: #0a0a0a; */\" bgcolor=\"#fefefe\">" +
                "\n" +
                "    <tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\">" +
                "\n" +
                "      <td style=\"word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0;/*! background-color: #0a0a0a; *//*! background-color: #0a0a0a; */background-image: url('https://cdn.greatsoftwares.com.br/arquivos/paginas_publicadas/www.theadsfactory.com.br/imagens/desktop/1623177237-134036-1371-d6a85a5ec56225b0f6d633a235b95669.jpg');background-size: cover;background-position: 50% 50%;/*! height: 698px; *//*! width: 500px; */\" valign=\"top\" align=\"center\"><span class=\"im\">" +
                "\n" +
                "<table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"font-size:30px;line-height:30px;word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;margin:0;padding:0\" valign=\"top\" height=\"30\" align=\"left\">&nbsp;</td></tr></tbody></table>" +
                "\n" +
                "<table class=\"m_130184285916875387container\" style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:inherit;width:580px;margin:0 auto;padding:0\" bgcolor=\"#fefefe\" align=\"center\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0;/*! background-color: #0a0a0a; */\" valign=\"top\" align=\"left\">" +
                "\n" +
                "<table class=\"m_130184285916875387row\" style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;display:table;padding:0;background-color: #0a0a0a;\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\">" +
                "\n" +
                "  <th class=\"m_130184285916875387small-12 m_130184285916875387columns\" style=\"width:564px;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0 auto;padding:0 16px 16px\" align=\"left\"><table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0;background-color: #0a0a0a;\"><tbody><tr style=\"vertical-align:top;padding:0;background-color: #0a0a0a;\" align=\"left\">" +
                "\n" +
                "<th style=\"color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0;background-color: #0a0a0a;\" align=\"left\">" +
                "\n" +
                "    <p style=\"/*! background-color:#000; */font-size:13px;color:#e1e1e1;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;margin:0 0 10px;padding:10px;background-color: #171717;\" align=\"left\"> Esta mensagem foi enviada pelo software de confirmação de conta da The AdsFactory. <br> Por favor, não responda este e-mail. </p>" +
                "\n" +
                "  </th>" +
                "\n" +
                "<th class=\"m_130184285916875387expander\" style=\"width:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0\" align=\"left\"></th>" +
                "\n" +
                "</tr></tbody></table></th>" +
                "\n" +
                "</tr></tbody></table>" +
                "\n" +
                "<table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0;background-color: #df7300;\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"font-size:10px;line-height:10px;word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;margin:0;padding:0\" valign=\"top\" height=\"10\" align=\"left\">&nbsp;</td></tr></tbody></table>" +
                "\n" +
                "<table class=\"m_130184285916875387row\" style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;display:table;padding:0;background-color: #df7300;\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\">" +
                "\n" +
                "  <th class=\"m_130184285916875387small-12 m_130184285916875387columns\" style=\"width:564px;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0 auto;padding:0 8px 8px;\" align=\"left\"><table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\">" +
                "\n" +
                "<th style=\"color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;margin:0;padding:0\" align=\"left\">" +
                "\n" +
                "    <a href=\"https://www.theadsfactory.com.br/\" style=\"position: relative;left: 35%;top: 10px;\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://u5146472.ct.sendgrid.net/ls/click?upn%3DSYFrgxgqoPpjBRlbb2FXSxE-2FbnEfLZEZ1tqWXN0SOQu88hs1h6n1PDfIzaUHmNC7wKlaoMY4S0WuG-2BP0i3lk2JSnlrpSqxyCexLaewwT4aTLWjutWGeQcQ71E8nveulVaheGNkKgyUzj90v-2Fvi9o8RGgcqsKaG9FEjTUz5VIm49cHmp24-2FbYrwIuXexhTqq3U9-2F4IBQqY-2Fg9niV-2FImSweTwAP2SWFAdK0B-2FxVrglvhw-3DlCpX_-2F4UCKyUR1tbbcpK-2BszhBapES-2Fz0LBHIIXHIYkSAY-2BzOY8dWuhs1EfYl9mPRdyZvUCCHjgIKyorr5E2jDlH8i0T7ndqJoUhgkzIcph-2FsKkwerxKtesXzAJj4AcyEKvRFU-2Fx-2Bx-2FuRrOcyG5fGscrPaF6YLtJoUH8boXycecOaF5kkSJ-2FVxUrme7sxz1mxcK-2Ba0FrTAKW8Hl352-2FEim8madSKBI-2F8ExvAamv7e2VwAN64YqYlF98H7ElTGu96hvgmYq7bOFYbUtnfGSsAlNbQJFFg-3D-3D&amp;source=gmail&amp;ust=1625959135030000&amp;usg=AFQjCNEvWR5klMOKEIhkW5XovxiBf86Iig\">" +
                "\n" +
                "      <img src=\"https://cdn.greatsoftwares.com.br/arquivos/paginas_publicadas/www.theadsfactory.com.br/imagens/desktop/1623177237-1114542-1371-0a98d69f9fff9f2504d0d8d2486952bf.png\" alt=\"The AdsFactory\" style=\"outline:none;text-decoration:none;width:auto;max-width:100%;clear:both;display:block;border:none;/*! background-color: #0a0a0a; */\" class=\"CToWUd\">" +
                "\n" +
                "</a>" +
                "\n" +
                "    <table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"font-size:15px;line-height:15px;word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;margin:0;padding:0\" valign=\"top\" height=\"25\" align=\"left\">&nbsp;</td></tr></tbody></table>" +
                "\n" +
                "    <hr style=\"border:1px solid #eee\">" +
                "\n" +
                "  <table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"font-size:16px;line-height:16px;word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;margin:0;padding:0\" valign=\"top\" height=\"16\" align=\"left\">&nbsp;</td></tr></tbody></table></th>" +
                "\n" +
                "<th class=\"m_130184285916875387expander\" style=\"width:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0\" align=\"left\"></th>" +
                "\n" +
                "</tr></tbody></table></th>" +
                "\n" +
                "</tr></tbody></table>" +
                "\n" +
                "</td></tr></tbody></table>" +
                "\n" +
                "</span><table class=\"m_130184285916875387container\" style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:inherit;width:580px;margin:0 auto;padding:0;background-color: #000000de;\" bgcolor=\"#fefefe\" align=\"center\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0\" valign=\"top\" align=\"left\">" +
                "\n" +
                "<table class=\"m_130184285916875387row\" style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;display:table;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\">" +
                "\n" +
                "  <th class=\"m_130184285916875387small-12 m_130184285916875387columns\" style=\"width:564px;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0 auto;padding:16px;\" align=\"left\"><table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\">" +
                "\n" +
                "<th style=\"color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0\" align=\"left\">" +
                "\n" +
                "    <h1 style=\"color:#cddaee;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;word-wrap:normal;font-size:30px;margin:0 0 10px;padding:0\" align=\"left\"><strong>Quase lá, "+ name +"  ! </strong></h1>" +
                "\n" +
                "    <table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"font-size:16px;line-height:16px;word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;margin:0;padding:0\" valign=\"top\" height=\"16\" align=\"left\">&nbsp;</td></tr></tbody></table>" +
                "\n" +
                "    <h4 style=\"color:#4affcc;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;word-wrap:normal;font-size:20px;margin:0 0 10px;padding:0\" align=\"left\">Parabéns, você realizou seu cadastro,<span style=\"color:#59e2f0;font-family:Helvetica,Arial,sans-serif;font-weight:normal;text-align:left;line-height:1.3;text-decoration:none;margin:0;padding:0\">" +
                "\n" +
                "    <strong>Mas antes você precisa fazer uma coisa.</strong>" +
                "\n" +
                "</span>      </h4><span class=\"im\">" +
                "\n" +
                "    <table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"font-size:16px;line-height:16px;word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;margin:0;padding:0\" valign=\"top\" height=\"16\" align=\"left\">&nbsp;</td></tr></tbody></table>" +
                "\n" +
                "    <hr style=\"border:1px solid #ea5959;\">" +
                "\n" +
                "    <table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"font-size:16px;line-height:16px;word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;margin:0;padding:0\" valign=\"top\" height=\"16\" align=\"left\">&nbsp;</td></tr></tbody></table>" +
                "\n" +
                "    <h2 style=\"color:#4affcc;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;word-wrap:normal;font-size:26px;margin:0 0 10px;padding:0\" align=\"left\"><strong>Confirme sua conta para acessar nossa plataforma.</strong></h2>" +
                "\n" +
                "    <h4 style=\"color:#59e2f0;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;word-wrap:normal;font-size:20px;margin:0 0 10px;padding:0\" align=\"left\">Clique no link abaixo para acessar nossos dados.</h4>" +
                "\n" +
                "    <table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"font-size:32px;line-height:32px;word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;margin:0;padding:0\" valign=\"top\" height=\"32\" align=\"left\">&nbsp;</td></tr></tbody></table><table class=\"m_130184285916875387large m_130184285916875387button\" style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%!important;margin:0 0 16px;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"word-wrap:break-word;border-collapse:collapse!important;color:#f7f2f2;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0\" valign=\"top\" align=\"left\"><table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"word-wrap:break-word;border-collapse:collapse!important;color:#fefefe;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0;border:2px solid #04fdbf;\" valign=\"top\" bgcolor=\"#32B796\" align=\"left\">" +
                "\n" +
                "      <a style=\"color:#fff;font-family:Helvetica,Arial,sans-serif;font-weight:bold;text-align:center;line-height:1.3;text-decoration:none;font-size:20px;display:inline-block;border-radius:3px;width:100%;margin:0;padding:10px 0;border:0 solid #05ffc1;\" href=\"" + link + "\">CONFIRMAR CONTA</a>" +
                "\n" +
                "    </td></tr></tbody></table></td></tr></tbody></table>" +
                "\n" +
                "  </span></th>" +
                "\n" +
                "<th class=\"m_130184285916875387expander\" style=\"width:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0\" align=\"left\"></th>" +
                "\n" +
                "</tr></tbody></table></th>" +
                "\n" +
                "</tr></tbody></table>" +
                "\n" +
                "</td></tr></tbody></table><span class=\"im\">" +
                "\n" +
                "<table class=\"m_130184285916875387container\" style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:inherit;width:580px;margin:0 auto;padding:0\" bgcolor=\"#fefefe\" align=\"center\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0\" valign=\"top\" align=\"left\">" +
                "\n" +
                "<table class=\"m_130184285916875387row\" style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;display:table;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\">" +
                "\n" +
                "  <th class=\"m_130184285916875387small-12 m_130184285916875387columns\" style=\"width:564px;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0 auto;padding:0 16px 16px\" align=\"left\"><table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\">" +
                "\n" +
                "<th style=\"color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0\" align=\"left\">" +
                "\n" +
                "    <hr style=\"border:1px solid #eee\">" +
                "\n" +
                "    <center style=\"width:100%;min-width:532px\">" +
                "\n" +
                "      <table class=\"m_130184285916875387menu\" style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:center;width:auto!important;float:none;margin:0 auto;padding:0\" align=\"center\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\"><td style=\"word-wrap:break-word;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0\" valign=\"top\" align=\"left\"><table style=\"border-spacing:0;border-collapse:collapse;vertical-align:top;text-align:left;width:100%;padding:0\"><tbody><tr style=\"vertical-align:top;padding:0\" align=\"left\">" +
                "\n" +
                "        <th style=\"float:none;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0 auto;padding:10px\" align=\"center\">" +
                "\n" +
                "<a style=\"color:#2a3f54;font-family:Helvetica,Arial,sans-serif;font-weight:normal;text-align:left;line-height:1.3;text-decoration:none;margin:0;padding:0\"></a><a href=\"https://u5146472.ct.sendgrid.net/ls/click?upn=SYFrgxgqoPpjBRlbb2FXSxE-2FbnEfLZEZ1tqWXN0SOQuIU9Bm4scVI1N5pxgbeTiNT2Zhnp5lpTV-2BHIW08y0PCpFGrZWdh-2BZe4O-2BXdRzN-2FZ0xdzUmUzU5LB1IwNGfdZ97L5vYcLMTC1EgCjtnsawAUACrNYNFxvx8C3olldAmfOBbliUc5w4MdEGLVyCENJyygZZd1xX1ufXGnw3J8nQ3td-2BFmi0YyLvqCVjHbVuG6iAwlMx2MOCx3xXD59NiuOgG47Q9_-2F4UCKyUR1tbbcpK-2BszhBapES-2Fz0LBHIIXHIYkSAY-2BzOY8dWuhs1EfYl9mPRdyZvUh3pJn6nzmtUyC8-2BX1Xv-2FgKSSDZXFaU0tNgTHNb-2BmaNf6t7RJIkzFDWobN5-2B293Nz9G8oOCubZa5SP6JGEXRJOoL7Og8UCWZ0VPtYN06diIEW9RYvklVPg0un5H38OLs2trWSKev5rp06-2FbgjEog1jmtxH4e555K-2BoFJthoRVV4SOc1qJ4G5yuCj-2BMbn5kgXDNRoqk22qSyWfhiEP-2BEu3yQ-3D-3D\" style=\"color:#2a3f54;font-family:Helvetica,Arial,sans-serif;font-weight:normal;text-align:left;line-height:1.3;text-decoration:none;margin:0;padding:0\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://u5146472.ct.sendgrid.net/ls/click?upn%3DSYFrgxgqoPpjBRlbb2FXSxE-2FbnEfLZEZ1tqWXN0SOQuIU9Bm4scVI1N5pxgbeTiNT2Zhnp5lpTV-2BHIW08y0PCpFGrZWdh-2BZe4O-2BXdRzN-2FZ0xdzUmUzU5LB1IwNGfdZ97L5vYcLMTC1EgCjtnsawAUACrNYNFxvx8C3olldAmfOBbliUc5w4MdEGLVyCENJyygZZd1xX1ufXGnw3J8nQ3td-2BFmi0YyLvqCVjHbVuG6iAwlMx2MOCx3xXD59NiuOgG47Q9_-2F4UCKyUR1tbbcpK-2BszhBapES-2Fz0LBHIIXHIYkSAY-2BzOY8dWuhs1EfYl9mPRdyZvUh3pJn6nzmtUyC8-2BX1Xv-2FgKSSDZXFaU0tNgTHNb-2BmaNf6t7RJIkzFDWobN5-2B293Nz9G8oOCubZa5SP6JGEXRJOoL7Og8UCWZ0VPtYN06diIEW9RYvklVPg0un5H38OLs2trWSKev5rp06-2FbgjEog1jmtxH4e555K-2BoFJthoRVV4SOc1qJ4G5yuCj-2BMbn5kgXDNRoqk22qSyWfhiEP-2BEu3yQ-3D-3D&amp;source=gmail&amp;ust=1625959135031000&amp;usg=AFQjCNF23yRlJN9fP-trFUx8BCEgTpaG0w\">VAGAS</a>" +
                "\n" +
                "</th>" +
                "\n" +
                "        <th style=\"float:none;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0 auto;padding:10px\" align=\"center\"><a style=\"color:#2a3f54;font-family:Helvetica,Arial,sans-serif;font-weight:normal;text-align:left;line-height:1.3;text-decoration:none;margin:0;padding:0\"></a></th>" +
                "\n" +
                "        <th style=\"float:none;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0 auto;padding:10px\" align=\"center\">" +
                "\n" +
                "<a style=\"color:#2a3f54;font-family:Helvetica,Arial,sans-serif;font-weight:normal;text-align:left;line-height:1.3;text-decoration:none;margin:0;padding:0\"></a><a href=\"https://u5146472.ct.sendgrid.net/ls/click?upn=SYFrgxgqoPpjBRlbb2FXSxE-2FbnEfLZEZ1tqWXN0SOQuB4-2B8KoCf2sd8CuB2hqMJyIxGwtiEsL-2FJFiWFtCQvmSkMXx5ebB9LxQ4I8HjVtgrrsxbLbNUodw-2B8h70acZymm1njy9krvgbTv-2F3EeGenUIWgoEBquBNHXhNLj22tuh-2BxnGyXEzBsixQuWLeZlXXSZbLA7qz7E8H-2F04zRhjSxUwnFYh3llBs7gYL9dQWLJ-2Fr7YyFdGrzz0koWCqDtYP3dBq6fY_-2F4UCKyUR1tbbcpK-2BszhBapES-2Fz0LBHIIXHIYkSAY-2BzOY8dWuhs1EfYl9mPRdyZvUBAyw-2F412L4ffWq0qSky1c6uHMo4lo9oXrHY1veR-2FhQMcE5AK7LaYXpx-2B4Xmgk4HMxYInxzPZtRsSLSzZG39YqDnFFVfP3GLuDT3l9kJFtb3GBTozsGgzoTGp-2ByxJJFU0PljeW-2Fn6Cbq-2FsWuMkQB0M8MIIIHXsOyw3JrqTFfQhguI-2FEUvc0-2BQj7GPAUEMIbAao43NbUqDtDwrKz3FY7r2Og-3D-3D\" style=\"color:#2a3f54;font-family:Helvetica,Arial,sans-serif;font-weight:normal;text-align:left;line-height:1.3;text-decoration:none;margin:0;padding:0\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://u5146472.ct.sendgrid.net/ls/click?upn%3DSYFrgxgqoPpjBRlbb2FXSxE-2FbnEfLZEZ1tqWXN0SOQuB4-2B8KoCf2sd8CuB2hqMJyIxGwtiEsL-2FJFiWFtCQvmSkMXx5ebB9LxQ4I8HjVtgrrsxbLbNUodw-2B8h70acZymm1njy9krvgbTv-2F3EeGenUIWgoEBquBNHXhNLj22tuh-2BxnGyXEzBsixQuWLeZlXXSZbLA7qz7E8H-2F04zRhjSxUwnFYh3llBs7gYL9dQWLJ-2Fr7YyFdGrzz0koWCqDtYP3dBq6fY_-2F4UCKyUR1tbbcpK-2BszhBapES-2Fz0LBHIIXHIYkSAY-2BzOY8dWuhs1EfYl9mPRdyZvUBAyw-2F412L4ffWq0qSky1c6uHMo4lo9oXrHY1veR-2FhQMcE5AK7LaYXpx-2B4Xmgk4HMxYInxzPZtRsSLSzZG39YqDnFFVfP3GLuDT3l9kJFtb3GBTozsGgzoTGp-2ByxJJFU0PljeW-2Fn6Cbq-2FsWuMkQB0M8MIIIHXsOyw3JrqTFfQhguI-2FEUvc0-2BQj7GPAUEMIbAao43NbUqDtDwrKz3FY7r2Og-3D-3D&amp;source=gmail&amp;ust=1625959135031000&amp;usg=AFQjCNFeDBBGy5IQxibNErTNWBoc08PMpg\">MEU DASHBOARD</a>" +
                "\n" +
                "</th>" +
                "\n" +
                "        <th style=\"float:none;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0 auto;padding:10px\" align=\"center\"><a style=\"color:#2a3f54;font-family:Helvetica,Arial,sans-serif;font-weight:normal;text-align:left;line-height:1.3;text-decoration:none;margin:0;padding:0\"></a></th>" +
                "\n" +
                "        <th style=\"float:none;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0 auto;padding:10px\" align=\"center\">" +
                "\n" +
                "<a style=\"color:#2a3f54;font-family:Helvetica,Arial,sans-serif;font-weight:normal;text-align:left;line-height:1.3;text-decoration:none;margin:0;padding:0\"></a><a href=\"https://u5146472.ct.sendgrid.net/ls/click?upn=SYFrgxgqoPpjBRlbb2FXSxE-2FbnEfLZEZ1tqWXN0SOQsqWLtfPJBSrpD4TIwnrDdcuVuVep6xhjH9kVHMsKjWD8-2BeOrD1Svebzp7N4qc-2FBs-2FNFVal1k2An-2BPihTSPCQjdOjTSrgwoe9vOZL-2FKNH6UCQ7ke0KtsRgUrzEIELxHzsA-3DBlgW_-2F4UCKyUR1tbbcpK-2BszhBapES-2Fz0LBHIIXHIYkSAY-2BzOY8dWuhs1EfYl9mPRdyZvUoS44uOCQICWg62gNLtIVHh0qrm2gpfxvFXPBM3N5Bg5qxpPyUBDe323YkKHK6euC02U0SorS4pgsyX0voNgS9qLls15GzW6k19jn-2BUIrAI71GB0HrgOp-2Bb6tOxIp0CHv92wXFbYyMfhej-2FI-2FObjZmK4YYzu41Lz9QJHoYLsgGQ-2FbtM4TCX-2FYbuuy8cTRI4DuyPLX3v8BPe1D0Hmv4AcCrg-3D-3D\" style=\"color:#2a3f54;font-family:Helvetica,Arial,sans-serif;font-weight:normal;text-align:left;line-height:1.3;text-decoration:none;margin:0;padding:0\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://u5146472.ct.sendgrid.net/ls/click?upn%3DSYFrgxgqoPpjBRlbb2FXSxE-2FbnEfLZEZ1tqWXN0SOQsqWLtfPJBSrpD4TIwnrDdcuVuVep6xhjH9kVHMsKjWD8-2BeOrD1Svebzp7N4qc-2FBs-2FNFVal1k2An-2BPihTSPCQjdOjTSrgwoe9vOZL-2FKNH6UCQ7ke0KtsRgUrzEIELxHzsA-3DBlgW_-2F4UCKyUR1tbbcpK-2BszhBapES-2Fz0LBHIIXHIYkSAY-2BzOY8dWuhs1EfYl9mPRdyZvUoS44uOCQICWg62gNLtIVHh0qrm2gpfxvFXPBM3N5Bg5qxpPyUBDe323YkKHK6euC02U0SorS4pgsyX0voNgS9qLls15GzW6k19jn-2BUIrAI71GB0HrgOp-2Bb6tOxIp0CHv92wXFbYyMfhej-2FI-2FObjZmK4YYzu41Lz9QJHoYLsgGQ-2FbtM4TCX-2FYbuuy8cTRI4DuyPLX3v8BPe1D0Hmv4AcCrg-3D-3D&amp;source=gmail&amp;ust=1625959135031000&amp;usg=AFQjCNGVOS_M4p10SOnjjoDEZCo46qBSpw\">BLOG</a>" +
                "\n" +
                "</th>" +
                "\n" +
                "      </tr></tbody></table></td></tr></tbody></table>" +
                "\n" +
                "    </center>" +
                "\n" +
                "    <hr style=\"border:1px solid #eee\">" +
                "\n" +
                "  </th>" +
                "\n" +
                "<th class=\"m_130184285916875387expander\" style=\"width:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-weight:normal;line-height:1.3;font-size:16px;margin:0;padding:0\" align=\"left\"></th>" +
                "\n" +
                "</tr></tbody></table></th>" +
                "\n" +
                "</tr></tbody></table>" +
                "\n" +
                "</td></tr></tbody></table>" +
                "\n" +
                "      </span></td>" +
                "\n" +
                "    </tr>" +
                "\n" +
                "  </tbody></table>" +
                "\n" +
                "<img src=\"https://ci4.googleusercontent.com/proxy/fiTCsVa0x1kDRMmXc5YFa5odeaIAk0ERZ4_Aout8CGB7IsTujvV_BopSqBQw1bh4rzmImLAB6aH08-10LNHF4_ZZ3OMDqzSwZUlQLwGWzlh8yDS5V_bThfJ_vWzavaALLMaR1OlmUqz967cIsh4C_2ssX4D3bn0QT5apCnuHbjzgbpLJ5kn8_M-ULf515Z6TitF4BMZkOeCtQFlmFWsmzpPwUcZouIbDZPx-1kLUyDv7OvrAldQJbC4_K3CrbQyBw10qxHaF_ZhGlV0ta_XtxgivFP-xdLMDVocwZEkM7FTCmumDwLZN9aXoiGtrNyXYOXBJ_KIgxBP9pWRk7Rw0GJ-OmBzv2ofgVlCLjSF4G6UvVdQKUxRZs1wZj8s_rR8rmxAyAsEfHaEzocUSbZ02U-UuQLmX2bbdjUj8EOrAX8WKtd8E878rdKmCbTBwtsdOgZC4Qs3OeHgUnWWQDVPQurlijbZZ4MrEFY3buGymJfmx9Ky5k4NrIfVIQJkKwUyUJSpvhcsJan0S=s0-d-e1-ft#https://u5146472.ct.sendgrid.net/wf/open?upn=5Dj09wA-2BZ2D7TgqQnQlStqpaNVXsjyzWsFdW7KCEUm6QT-2B5bBx4-2BtPYgOdCykG2sikBHDiDkk-2B5tSmCAAhwYd4QHbPlyZxD8rJnwGrJH1TvaXZDlaEDAGGWOm-2F7Pcz-2Bmg9CkKx-2BTukYnUxea8n10In0nK5dOvb1-2BfVgWKQTO1HzeJUAhSYe9whZM4MpCu79IT1vtBrdnOsISOn115JR3TtUPheoAFCwhU9Y1scr3kdbV-2BOSJxki2hd-2B6UsoW0niLsBQmis-2B4JSk-2BbnN3rp2TRSxEWW5TGJbZll04u-2FYCdJs-3D\" alt=\"\" style=\"height:1px!important;width:1px!important;border-width:0!important;margin-top:0!important;margin-bottom:0!important;margin-right:0!important;margin-left:0!important;padding-top:0!important;padding-bottom:0!important;padding-right:0!important;padding-left:0!important\" class=\"CToWUd\" width=\"1\" height=\"1\" border=\"0\"></div>";
    }

}
