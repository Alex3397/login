package com.alex.login.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3,max = 100)
    private String name;

    @NotBlank
    private String cpf;

    @ElementCollection
    @CollectionTable(name = "client_address", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "address")
    private List<ClientAddresses> addressList = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "client_emails", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "address")
    private List<ClientEmails> emailList = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "client_phone_numbers", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "address")
    private List<ClientPhones> phoneList = new ArrayList<>();

    public Client(String name,
                  String cpf,
                  List<ClientAddresses> addressList,
                  List<ClientEmails> emailList,
                  List<ClientPhones> phoneList) {
        this.name = name;
        this.cpf = cpf;
        this.addressList = addressList;
        this.emailList = emailList;
        this.phoneList = phoneList;
    }
}
