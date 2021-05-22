package com.alex.login.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private RegistrationService registrationService;

    @PostMapping
    public String registerUser(@RequestBody UserRegistrationRequest request) {
        return registrationService.registerUser(request);
    }

    @PostMapping(path = "/admin")
    public String registerAdmin(@RequestBody UserRegistrationRequest request) {
        return registrationService.registerAdmin(request);
    }

    @PostMapping(path = "/clients")
    public String registerClient(@RequestBody ClientRegistrationRequest request) {
        return registrationService.registerClient(request);
    }

}
