package com.acme.tarifas.gestion.clients;

import com.acme.tarifas.gestion.dto.TransportistaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Service
public class ViajesClient {

    private final WebClient webClient;

    public ViajesClient(@Value("${VIAJES_URL}") String urlViajes, WebClient.Builder builder) {
        this.webClient = builder.baseUrl(urlViajes).build();
    }

    public WebClient getWebClient() {
        return webClient;
    }


    public List<TransportistaDTO> getTransportistas() {
        return webClient.get()
                .uri("/empresa")
                .retrieve()
                .bodyToFlux(TransportistaDTO.class)
                .collectList()
                .block();
    }

    /*
    Para concatenar el campo 'telefono':
    String numeroCompleto = "+" + contacto.getTelefono().getCodigoPais()
                       + "-" + contacto.getTelefono().getCodigoArea()
                       + "-" + contacto.getTelefono().getNumero();

     VIAJES tiene 'telefono' como una entidad aparte, osea que solo nos llega la referencia mediante un id de mongo
     */


}
