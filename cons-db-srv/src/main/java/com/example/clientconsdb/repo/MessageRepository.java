package com.example.clientconsdb.repo;

import com.example.clientconsdb.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {}
