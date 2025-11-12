package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargaDeCombustibleRepository extends JpaRepository<CargaDeCombustible, Long> {

    List<CargaDeCombustible> findByEsVigenteTrue();
}