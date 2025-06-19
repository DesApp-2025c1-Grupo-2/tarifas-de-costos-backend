package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TransportistaRepository;
import com.acme.tarifas.gestion.entity.Transportista;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TransportistaService {

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Transactional
    public Transportista guardarTransportista(Transportista transportista) {
        return transportistaRepository.save(transportista);
    }

    public List<Transportista> obtenerTodos() {
        return transportistaRepository.findAll();
    }

    public Optional<Transportista> obtenerPorId(Long id) {
        return transportistaRepository.findById(id);
    }

    @Transactional
    public Optional<Transportista> actualizarTransportista(Long id, Transportista nuevosDatos) {
        return transportistaRepository.findById(id).map(existente -> {
            existente.setNombreEmpresa(nuevosDatos.getNombreEmpresa());
            existente.setContactoNombre(nuevosDatos.getContactoNombre());
            existente.setContactoEmail(nuevosDatos.getContactoEmail());
            existente.setContactoTelefono(nuevosDatos.getContactoTelefono());
            existente.setEvaluacionDesempeno(nuevosDatos.getEvaluacionDesempeno());
            existente.setActivo(nuevosDatos.getActivo());
            return transportistaRepository.save(existente);
        });
    }

    @Transactional
    public void eliminarTransportista(Long id) throws Exception {
        Transportista transportista = transportistaRepository.findById(id)
                .orElseThrow(() -> new Exception("Transportista no encontrado"));

        transportistaRepository.delete(transportista);
    }

    public Transportista baja(Long id) throws Exception{
        Transportista transportista = transportistaRepository.findById(id)
                .orElseThrow(() -> new Exception("Transportista no encontrado"));

        if(transportista.getActivo()){
            transportista.setActivo(false);
            return transportistaRepository.save(transportista);
        }else{
            throw new Exception("El transportista ya está inactivo");
        }
    }
}