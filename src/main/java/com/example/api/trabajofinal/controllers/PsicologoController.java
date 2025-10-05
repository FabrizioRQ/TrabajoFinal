package com.example.api.trabajofinal.controllers;

import com.example.api.trabajofinal.DTO.PsicologoDTO;
import com.example.api.trabajofinal.services.PsicologoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/psicologos")
public class PsicologoController {

    @Autowired
    private PsicologoService psicologoService;

    @PostMapping
    public ResponseEntity<?> registrarPsicologo(@RequestBody PsicologoDTO psicologoDTO) {
        try {
            PsicologoDTO psicologoRegistrado = psicologoService.registrarPsicologo(psicologoDTO);
            return ResponseEntity.ok(psicologoRegistrado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPsicologo(@PathVariable Long id) {
        Optional<PsicologoDTO> psicologo = psicologoService.obtenerPsicologoPorId(id);
        if (psicologo.isPresent()) {
            return ResponseEntity.ok(psicologo.get());
        }
        return ResponseEntity.notFound().build();
    }
}