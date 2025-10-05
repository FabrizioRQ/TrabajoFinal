package com.example.api.trabajofinal.interfaces;

import com.example.api.trabajofinal.DTO.NiñoDTO;

import java.util.Optional;

public interface NiñoInterface {
    public NiñoDTO registrarNiño(NiñoDTO niñoDTO);
    public Optional<NiñoDTO> obtenerNiñoPorId(Long id);
}