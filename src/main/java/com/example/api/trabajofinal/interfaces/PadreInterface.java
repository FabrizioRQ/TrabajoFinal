package com.example.api.trabajofinal.interfaces;

import com.example.api.trabajofinal.DTO.PadreDTO;
import java.util.List;
import java.util.Optional;

public interface PadreInterface {
    PadreDTO crearPadre(PadreDTO padreDTO);
    List<PadreDTO> obtenerTodosLosPadres();
    Optional<PadreDTO> obtenerPadrePorId(Long id);
    PadreDTO actualizarPadre(Long id, PadreDTO padreDTO);
    void eliminarPadre(Long id);
}