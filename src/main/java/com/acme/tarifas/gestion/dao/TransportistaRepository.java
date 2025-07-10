package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.dto.TransportistaTarifasDTO;
import com.acme.tarifas.gestion.entity.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransportistaRepository extends JpaRepository<Transportista, Long> {

    @Query("SELECT new com.acme.tarifas.gestion.dto.TransportistaTarifasDTO(t.transportista.nombreEmpresa, COUNT(t)) " +
           "FROM TarifaCosto t " +
           "WHERE t.transportista IS NOT NULL " +
           "GROUP BY t.transportista.nombreEmpresa " +
           "ORDER BY COUNT(t) DESC")
    List<TransportistaTarifasDTO> findTransportistasMasUtilizados();



    boolean existsByContactoNombreAndActivoTrue(String contactoNombre);
}