package com.alex.login.registration;

import com.alex.login.client.ClientAddresses;
import com.alex.login.client.ClientEmails;
import com.alex.login.client.ClientPhones;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ClientRegistrationRequest {
    private final String name;
    private final String cpf;
    private final List<ClientAddresses> addressList;
    private final List<ClientEmails> emailList;
    private final List<ClientPhones> phoneList;
}

