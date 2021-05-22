package com.alex.login.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Embeddable
@Table(name = "client_address")
public class ClientAddresses {
    private String cep;
    private String log;
    private String bar;
    private String uf;
    private String complement = "";

    public ClientAddresses(String cep, String log, String bar, String uf, String complement) {
        this.cep = cep;
        this.log = log;
        this.bar = bar;
        this.uf = uf;
        this.complement = complement;
    }
}
