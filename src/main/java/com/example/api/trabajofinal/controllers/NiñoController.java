package com.example.api.trabajofinal.controllers;

import com.example.api.trabajofinal.DTO.NiñoDTO;
import com.example.api.trabajofinal.DTO.AvatarDTO;
import com.example.api.trabajofinal.services.NiñoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/niños")
public class NiñoController {

    @Autowired
    private NiñoService niñoService;

    @PostMapping
    public ResponseEntity<?> registrarNiño(@RequestBody NiñoDTO niñoDTO) {
        try {
            NiñoDTO niñoRegistrado = niñoService.registrarNiño(niñoDTO);
            return ResponseEntity.ok(niñoRegistrado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerNiño(@PathVariable Long id) {
        Optional<NiñoDTO> niño = niñoService.obtenerNiñoPorId(id);
        if (niño.isPresent()) {
            return ResponseEntity.ok(niño.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{niñoId}/avatares-desbloqueados")
    public ResponseEntity<?> consultarAvataresDesbloqueadosPorNiño(@PathVariable Long niñoId) {
        try {
            List<AvatarDTO> avatares = niñoService.consultarAvataresDesbloqueadosPorNiñoId(niñoId);

            if (avatares.isEmpty()) {
                return ResponseEntity.ok()
                        .body("{\"message\": \"El niño no tiene avatares desbloqueados\", \"avatares\": []}");
            }

            return ResponseEntity.ok(avatares);

        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/usuario/{usuarioId}/avatares-desbloqueados")
    public ResponseEntity<?> consultarAvataresDesbloqueadosPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<AvatarDTO> avatares = niñoService.consultarAvataresDesbloqueadosPorUsuarioId(usuarioId);

            if (avatares.isEmpty()) {
                return ResponseEntity.ok()
                        .body("{\"message\": \"El usuario no tiene avatares desbloqueados\", \"avatares\": []}");
            }

            return ResponseEntity.ok(avatares);

        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/usuario/{usuarioId}/tiene-avatares")
    public ResponseEntity<?> verificarAvataresDesbloqueados(@PathVariable Long usuarioId) {
        try {
            boolean tieneAvatares = niñoService.tieneAvataresDesbloqueados(usuarioId);
            return ResponseEntity.ok().body("{\"tieneAvatares\": " + tieneAvatares + "}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"Error al verificar avatares: " + e.getMessage() + "\"}");
        }
    }
}