package com.example.api.trabajofinal.interfaces;

import com.example.api.trabajofinal.DTO.PsicologoDTO;


import java.util.Optional;

public interface PsicologoInterface {
    public PsicologoDTO registrarPsicologo(PsicologoDTO psicologoDTO);
    public Optional<PsicologoDTO> obtenerPsicologoPorId(Long id);
}
