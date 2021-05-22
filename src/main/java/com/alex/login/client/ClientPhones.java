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
@Table(name = "client_phone_numbers")
public class ClientPhones {
    private String phone;
}
