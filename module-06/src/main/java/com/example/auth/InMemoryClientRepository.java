package com.example.auth;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** A {@link ClientRepository} kept in memory; handy for tests and demos. */
public final class InMemoryClientRepository implements ClientRepository {

    private final Map<String, Client> clients = new ConcurrentHashMap<>();

    @Override
    public Optional<Client> findById(String clientId) {
        return Optional.ofNullable(clients.get(clientId));
    }

    @Override
    public void save(Client client) {
        clients.put(client.clientId(), client);
    }

    public int size() {
        return clients.size();
    }
}
