package com.alex.login.users.client;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Table(name = "client_address")
public class ClientAddresses {
    @NotBlank
    private String cep;
    @NotBlank
    private String log;
    @NotBlank
    private String bar;
    @NotBlank
    private String uf;
    private String complement = "";

    public ClientAddresses(ClientAddresses clientAddresses) {
        cep = clientAddresses.getCep();
        log = clientAddresses.getLog();
        bar = clientAddresses.getBar();
        uf = clientAddresses.getUf();
        complement = clientAddresses.getComplement();
    }
}
