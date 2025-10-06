package com.example.api.trabajofinal.services;

import com.example.api.trabajofinal.DTO.PsicologoDTO;
import com.example.api.trabajofinal.entities.Psicologo;
import com.example.api.trabajofinal.entities.Usuario;
import com.example.api.trabajofinal.interfaces.PsicologoInterface;
import com.example.api.trabajofinal.repositories.PsicologoRepository;
import com.example.api.trabajofinal.repositories.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PsicologoService implements PsicologoInterface {

    @Autowired
    private PsicologoRepository psicologoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PsicologoDTO registrarPsicologo(PsicologoDTO psicologoDTO) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(psicologoDTO.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + psicologoDTO.getIdUsuario());
        }

        Usuario usuario = usuarioOpt.get();
        if (!"ACTIVE".equals(usuario.getEstado())) {
            throw new RuntimeException("El usuario no está activo. Estado actual: " + usuario.getEstado());
        }

        if (psicologoRepository.existsByIdUsuario(psicologoDTO.getIdUsuario())) {
            throw new RuntimeException("El usuario ya tiene asignado el rol de psicólogo");
        }


        if (psicologoRepository.existsByNumeroColegiatura(psicologoDTO.getNumeroColegiatura())) {
            throw new RuntimeException("Ya existe un psicólogo con el número de colegiatura: " + psicologoDTO.getNumeroColegiatura());
        }

        if (psicologoDTO.getEspecialidad() == null || psicologoDTO.getEspecialidad().trim().isEmpty()) {
            throw new RuntimeException("La especialidad es requerida");
        }

        if (psicologoDTO.getNumeroColegiatura() == null || psicologoDTO.getNumeroColegiatura().trim().isEmpty()) {
            throw new RuntimeException("El número de colegiatura es requerido");
        }

        usuario.setTipoUsuario("PSICÓLOGO");
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        Psicologo psicologo = modelMapper.map(psicologoDTO, Psicologo.class);
        psicologo.setIdUsuario(usuarioActualizado);

        Psicologo psicologoGuardado = psicologoRepository.save(psicologo);

        System.out.println("=== PSICÓLOGO REGISTRADO ===");
        System.out.println("Usuario: " + usuarioActualizado.getCorreoElectronico());
        System.out.println("Psicólogo ID: " + psicologoGuardado.getId());
        System.out.println("Especialidad: " + psicologoGuardado.getEspecialidad());
        System.out.println("Colegiatura: " + psicologoGuardado.getNumeroColegiatura());
        System.out.println("=============================");

        return modelMapper.map(psicologoGuardado, PsicologoDTO.class);
    }

    @Override
    public Optional<PsicologoDTO> obtenerPsicologoPorId(Long id) {
        return psicologoRepository.findById(id)
                .map(psicologo -> modelMapper.map(psicologo, PsicologoDTO.class));
    }
}