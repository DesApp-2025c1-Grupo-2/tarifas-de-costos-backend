    package com.acme.tarifas.gestion.controller;

    import com.acme.tarifas.gestion.dao.ZonaViajeRepository;
    import com.acme.tarifas.gestion.entity.TarifaCosto;
    import com.acme.tarifas.gestion.entity.Transportista;
    import com.acme.tarifas.gestion.entity.ZonaViaje;
    import com.acme.tarifas.gestion.service.ZonaViajeService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/zonas")
    public class ZonaViajeController {

        @Autowired
        private ZonaViajeService zonaService;

        @GetMapping
        public List<ZonaViaje> obtenerTodasLasZonas(){return zonaService.getZonas();}

        @GetMapping("/{id}")
        public ResponseEntity<ZonaViaje> obtenerZonaPorId(@PathVariable Long id) {
            return zonaService.getZonaById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        public ResponseEntity<ZonaViaje> crearZona(@RequestBody ZonaViaje zona){
            ZonaViaje nuevaZona = zonaService.guardarZona(zona);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaZona);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> eliminarZona(@PathVariable Long id) {
            try {
                zonaService.eliminarZona(id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/comparativa-costos")
        public Map<String, Object> compararCostosPorZona() {
            return zonaService.obtenerComparativaCostos();
        }

        @GetMapping("/{id}/tarifas")
        public List<TarifaCosto> obtenerTarifasPorZona(@PathVariable Long id) {
            return zonaService.obtenerTarifasZona(id);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ZonaViaje> actualizarZona(@PathVariable Long id,@RequestBody ZonaViaje zona){
            return zonaService.actualizarZona(id, zona)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
    }