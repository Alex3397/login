package com.alex.login.client;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/clients")
@AllArgsConstructor
public class ClientController {

    private ClientService clientService;

    @GetMapping()
    public List<Client> findAllClients() {
        List<Client> clients = clientService.findClients();
        return clients;
    }

}
