package com.acme.tarifas.gestion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class BasicController {
    @GetMapping("/index")
    public String testIndex(){
        return "Test Index Controller";
    }
}
