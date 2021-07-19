package com.alex.login.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping( path = "/")
public class Login {

    @GetMapping(value = "/login")
    public String loginPage() {
        Path file = Path.of(System.getProperty("user.dir"),"/src/main/resources/templates/login.html");
        String rawContent = "";
        try {
            rawContent = Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawContent;
    }

    @GetMapping(value = "/success")
    public String successTest() {
        Path file = Path.of(System.getProperty("user.dir"),"/src/main/resources/templates/allowed.html");
        String rawContent = "";
        try {
            rawContent = Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawContent;
    }

    @GetMapping(value = "/")
    public String landingPage() {
        Path file = Path.of(System.getProperty("user.dir"),"/src/main/resources/templates/landingPage.html");
        String rawContent = "";
        try {
            rawContent = Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawContent;
    }

    @GetMapping(value = "/join")
    public String signUp() {
        Path file = Path.of(System.getProperty("user.dir"),"/src/main/resources/templates/signUp.html");
        String rawContent = "";
        try {
            rawContent = Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawContent;
    }

}
