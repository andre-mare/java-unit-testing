package com.example.auth;

import java.util.Optional;

/** Stores registered clients. In production this would be a database. */
public interface ClientRepository {

    Optional<Client> findById(String clientId);

    void save(Client client);
}
