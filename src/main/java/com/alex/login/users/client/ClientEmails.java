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
@Table(name = "client_emails")
public class ClientEmails {
    @NotBlank
    private String email;
}
