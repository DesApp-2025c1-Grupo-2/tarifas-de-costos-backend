package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TarifaCosto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; 

@Repository
public interface TarifaCostoRepository extends JpaRepository<TarifaCosto, Long> {


    List<TarifaCosto> findByEsVigenteTrue();
}