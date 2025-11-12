package com.acme.tarifas.gestion.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Tarifas_Adicionales", uniqueConstraints = @UniqueConstraint(columnNames = { "ID_TarifaCosto",
        "ID_Adicional" }))
public class TarifaAdicional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Tarifa_Adicional")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_TarifaCosto")
    @JsonBackReference
    private TarifaCosto tarifaCosto;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ID_Adicional")
    private Adicional adicional;

    private Double costoEspecifico;

    @Column(name = "Activo", nullable = false)
    private boolean activo = true;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TarifaAdicional that = (TarifaAdicional) o;
        return Objects.equals(tarifaCosto != null ? tarifaCosto.getId() : null,
                that.tarifaCosto != null ? that.tarifaCosto.getId() : null) &&
                Objects.equals(adicional != null ? adicional.getId() : null,
                        that.adicional != null ? that.adicional.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                tarifaCosto != null ? tarifaCosto.getId() : null,
                adicional != null ? adicional.getId() : null);
    }
}