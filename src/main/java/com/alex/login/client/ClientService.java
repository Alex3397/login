package com.alex.login.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientService {
    
    private final ClientRepository clientRepository;

    public String signUpClient(Client client) {
        boolean userExists = clientRepository.findByName(client.getName()).isPresent();
//        boolean emailTaken = clientRepository.findByEmail(client.getEmailList()).isPresent();
        boolean emailTaken = false;
        boolean cpfTaken = clientRepository.findByCpf(client.getCpf()).isPresent();
        if (userExists) {
            throw new IllegalStateException("username is already in use");
        } else if (emailTaken) {
            throw new IllegalStateException("email is already taken");
        } else if (cpfTaken) {
            throw new IllegalStateException("cpf is already used");
        }
        clientRepository.save(client);
        //Todo: send confirmation token
        return String.format("%1s\n%2s\n%3s\n%4s\n%5s\n%6s",
                client.getId(),
                client.getName(),
                client.getCpf(),
                client.getAddressList(),
                client.getEmailList(),
                client.getPhoneList());
    }

    public List<Client> findClients() {
        List<Client> clients = clientRepository.findAllClient();
        return clients;
    }

}
