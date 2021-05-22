package com.alex.login.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ClientRepository extends JpaRepository<Client,Long> {

    Optional<Client> findByName(String name);
    Optional<Client> findByCpf(String cpf);
//    Optional<Object> findByEmail(List<ClientEmails> emailList);
//    Optional<Object> findByPhone(List<ClientPhones> phoneList);
    @Query("select c from Client c")
    List<Client> findAllClient();

}
