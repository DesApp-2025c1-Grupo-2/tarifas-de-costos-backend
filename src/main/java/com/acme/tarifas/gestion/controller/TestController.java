package com.acme.tarifas.gestion.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class TestController {
    @GetMapping("/test")
    public ResponseEntity<?> testConnection() {
        Map<String, String> body = new HashMap<>();
        body.put("status", "success");
        body.put("message", "Conexión establecida correctamente");
        body.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok().body(body);
    }
}