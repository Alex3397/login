package com.alex.login.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(path = "/user")
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

    @GetMapping(path = "/user/confirm")
    public String userTokenConfirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }

    @GetMapping(path = "/admin/confirm")
    public String adminTokenConfirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}
