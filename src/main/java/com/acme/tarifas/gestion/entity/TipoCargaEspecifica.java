package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TiposCargaEspecifica")
public class TipoCargaEspecifica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TipoCargaEspecifica")
    private Long id;

    private String nombre;
    private Boolean requiereManipulacionEspecial;
    private String descripcion;
}
