package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}