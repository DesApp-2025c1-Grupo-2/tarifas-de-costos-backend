package com.acme.tarifas.gestion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

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

        if (dto.getContacto() != null) {
            this.contactoNombre = dto.getContacto().getNombre();
            this.contactoEmail = dto.getContacto().getEmail();
            if (dto.getContacto().getTelefono() != null) {
                this.contactoTelefono = dto.getContacto().getTelefono().getNumero();
            }
        }
    }
}