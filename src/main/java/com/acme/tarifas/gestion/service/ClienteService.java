package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.ClienteRepository;
import com.acme.tarifas.gestion.dao.ViajeRepository;
import com.acme.tarifas.gestion.entity.Cliente;
import com.acme.tarifas.gestion.entity.Viaje;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> obtenerTodosClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        logger.info("Clientes {} encontrados en la base de datos ", clientes.size());
        clientes.forEach(c -> logger.debug("Client: {}", c.getNombreCliente()));
        return clientes;
    }

    public List<Viaje> obtenerViajesCliente(Long clienteId) {
        return viajeRepository.findByClienteId(clienteId);
    }
}