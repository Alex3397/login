package com.alex.login.client;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Table(name = "client_address")
public class ClientAddresses {
    private String cep;
    private String log;
    private String bar;
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
