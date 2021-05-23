package com.alex.login.registration;

import com.alex.login.appuser.AppUser;
import com.alex.login.appuser.AppUserRole;
import com.alex.login.appuser.AppUserService;
import com.alex.login.client.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;

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
        Iterator<ClientAddresses> addressIterator = request.getAddressList().iterator();
        while (addressIterator.hasNext()) {
            ClientAddresses clientAddresses = addressIterator.next();
            System.out.println(clientAddresses.getCep());
            System.out.println(clientAddresses.getLog());
            System.out.println(clientAddresses.getBar());
            System.out.println(clientAddresses.getUf());
            System.out.println(clientAddresses.getComplement());
        }
        Iterator<ClientEmails> clientEmailsIterator = request.getEmailList().iterator();
        while (clientEmailsIterator.hasNext()) {
            ClientEmails clientEmails = clientEmailsIterator.next();
            System.out.println(clientEmails.getEmail());
        }
        Iterator<ClientPhones> clientPhonesIterator = request.getPhoneList().iterator();
        while (clientPhonesIterator.hasNext()) {
            ClientPhones clientPhones = clientPhonesIterator.next();
            System.out.println(clientPhones.getPhone());
        }
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
