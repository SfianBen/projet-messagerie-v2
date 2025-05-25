package com.example.clientconsdb.repo;

import com.example.clientconsdb.model.ClientConnecte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientConnecte, String> {}
