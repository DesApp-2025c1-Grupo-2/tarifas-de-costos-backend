package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TipoCargaEspecifica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoCargaEspecificaRepository extends JpaRepository<TipoCargaEspecifica, Long> {
}