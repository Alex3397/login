package com.alex.login.registration;

import com.alex.login.appuser.AppUser;
import com.alex.login.appuser.AppUserRole;
import com.alex.login.appuser.AppUserService;
import com.alex.login.client.Client;
import com.alex.login.client.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private AppUserService appUserService;
    private ClientService clientService;
    private EmailValidator validator;

    public String registerUser(UserRegistrationRequest request) {
        boolean isValidEmail = validator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException(String.format("%s is not a valid email",request.getEmail()));
        }
        return appUserService.signUpUser( new AppUser(
                request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                AppUserRole.USER
            )
        );
    }

    public String registerAdmin(UserRegistrationRequest request) {
        boolean isValidEmail = validator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException(String.format("%s is not a valid email",request.getEmail()));
        }
        return appUserService.signUpUser( new AppUser(
                        request.getEmail(),
                        request.getUsername(),
                        request.getPassword(),
                        AppUserRole.ADMIN
                )
        );
    }

    public String registerClient(ClientRegistrationRequest request) {
        boolean isValidEmail = validator.test(request.getEmailList());
        if (!isValidEmail) {
            throw new IllegalStateException(String.format("%s is not a valid email",request.getEmailList()));
        }
        System.out.println(request);
        System.out.println(request.getName().isEmpty());
        System.out.println(request.getCpf().isEmpty());
//        System.out.println(request.getAddressList().size());
//        System.out.println(request.getEmailList().size());
//        System.out.println(request.getPhoneList().size());
        return clientService.signUpClient( new Client(
                request.getName(),
                request.getCpf(),
                request.getAddressList(),
                request.getEmailList(),
                request.getPhoneList()
                )
        );
    }

}
