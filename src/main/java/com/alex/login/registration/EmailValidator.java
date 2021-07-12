package com.alex.login.registration;

import com.alex.login.users.client.ClientEmails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
//         TODO: Regex to validate here
        return true;
    }

    public boolean test(List<ClientEmails> emailList) {
        return true;
    }
}
