package com.acme.tarifas.gestion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que define cómo se muestran los datos en la tabla de "Gestionar
 * Transportistas".
 */
@Data
@NoArgsConstructor
public class TransportistaViewDTO {
    private String id;
    private String cuit;
    private String nombreEmpresa;
    private String contactoNombre;
    private String contactoEmail;
    private String contactoTelefono;
    private boolean activo = true;

    public TransportistaViewDTO(TransportistaDTO dto) {
        this.id = dto.getId();
        this.cuit = dto.getCuit();
        this.nombreEmpresa = dto.getNombreComercial();
        this.contactoNombre = dto.getNombreParaMostrar();

        if (dto.getContacto() != null) {
            this.contactoEmail = dto.getContacto().getEmail();
            if (dto.getContacto().getTelefono() != null) {
                this.contactoTelefono = dto.getContacto().getTelefono().getNumero();
            }
        }
    }
}