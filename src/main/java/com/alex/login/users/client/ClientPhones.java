package com.alex.login.users.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Embeddable
@Table(name = "client_phone_numbers")
public class ClientPhones {
    @NotBlank
    private String phone;
}
