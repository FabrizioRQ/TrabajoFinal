package com.example.api.trabajofinal.services;

import com.example.api.trabajofinal.DTO.NiñoDTO;
import com.example.api.trabajofinal.DTO.AvatarDTO;
import com.example.api.trabajofinal.entities.Avatar;
import com.example.api.trabajofinal.entities.Niño;
import com.example.api.trabajofinal.entities.Usuario;
import com.example.api.trabajofinal.entities.Padre;
import com.example.api.trabajofinal.entities.Psicologo;
import com.example.api.trabajofinal.interfaces.NiñoInterface;
import com.example.api.trabajofinal.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NiñoService implements NiñoInterface {

    @Autowired
    private NiñoRepository niñoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private PadreRepository padreRepository;

    @Autowired
    private PsicologoRepository psicologoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public NiñoDTO registrarNiño(NiñoDTO niñoDTO) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(niñoDTO.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + niñoDTO.getIdUsuario());
        }

        if (niñoRepository.existsByIdUsuario(niñoDTO.getIdUsuario())) {
            throw new RuntimeException("El usuario ya está asignado a otro niño");
        }

        Optional<Avatar> avatarOpt = avatarRepository.findById(niñoDTO.getIdAvatar());
        if (avatarOpt.isEmpty()) {
            throw new RuntimeException("Avatar no encontrado con ID: " + niñoDTO.getIdAvatar());
        }

        Optional<Psicologo> psicologoOpt = psicologoRepository.findById(niñoDTO.getIdPsicologo());
        if (psicologoOpt.isEmpty()) {
            throw new RuntimeException("Psicólogo no encontrado con ID: " + niñoDTO.getIdPsicologo());
        }

        long cantidadNiñosPorPsicologo = niñoRepository.countByIdPsicologo(niñoDTO.getIdPsicologo());
        if (cantidadNiñosPorPsicologo >= 5) {
            throw new RuntimeException("El psicólogo ya tiene el máximo de 5 niños asignados. No puede registrar más niños para este psicólogo.");
        }

        Optional<Padre> padreOpt = padreRepository.findById(niñoDTO.getIdPadre());
        if (padreOpt.isEmpty()) {
            throw new RuntimeException("Padre no encontrado con ID: " + niñoDTO.getIdPadre());
        }

        Niño niño = modelMapper.map(niñoDTO, Niño.class);
        niño.setIdUsuario(usuarioOpt.get());
        niño.setIdAvatar(avatarOpt.get());
        niño.setIdPsicologo(psicologoOpt.get());
        niño.setIdPadre(padreOpt.get());

        Niño niñoGuardado = niñoRepository.save(niño);

        System.out.println("=== NIÑO REGISTRADO EXITOSAMENTE ===");
        System.out.println("Niño ID: " + niñoGuardado.getId());
        System.out.println("Usuario: " + usuarioOpt.get().getCorreoElectronico());
        System.out.println("Psicólogo: " + psicologoOpt.get().getIdUsuario().getCorreoElectronico());
        System.out.println("Niños del psicólogo: " + (cantidadNiñosPorPsicologo + 1) + "/5");
        System.out.println("===================================");

        return modelMapper.map(niñoGuardado, NiñoDTO.class);
    }

    @Override
    public Optional<NiñoDTO> obtenerNiñoPorId(Long id) {
        return niñoRepository.findById(id)
                .map(niño -> modelMapper.map(niño, NiñoDTO.class));
    }

    // NUEVO MÉTODO PARA CONSULTAR AVATARES DESBLOQUEADOS POR NIÑO ID
    public List<AvatarDTO> consultarAvataresDesbloqueadosPorNiñoId(Long niñoId) {
        // Verificar si el niño existe
        Optional<Niño> niñoOpt = niñoRepository.findById(niñoId);
        if (niñoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Niño no encontrado con ID: " + niñoId);
        }

        Niño niño = niñoOpt.get();

        // Obtener el avatar principal del niño y mapearlo a DTO
        Avatar avatarPrincipal = niño.getIdAvatar();
        AvatarDTO avatarDTO = modelMapper.map(avatarPrincipal, AvatarDTO.class);

        // En una implementación futura, aquí agregarías más avatares desbloqueados
        // desde una tabla de relación niño-avatar

        return List.of(avatarDTO);
    }

    // MÉTODO PARA CONSULTAR AVATARES DESBLOQUEADOS POR USUARIO ID (UUID)
    public List<AvatarDTO> consultarAvataresDesbloqueadosPorUsuarioId(Long usuarioId) {
        Optional<Niño> niñoOpt = niñoRepository.findByUsuarioId(usuarioId);
        if (niñoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró un niño asociado al usuario con ID: " + usuarioId);
        }

        Niño niño = niñoOpt.get();
        return consultarAvataresDesbloqueadosPorNiñoId(niño.getId());
    }

    // MÉTODO PARA VERIFICAR SI UN USUARIO TIENE AVATARES DESBLOQUEADOS
    public boolean tieneAvataresDesbloqueados(Long usuarioId) {
        try {
            List<AvatarDTO> avatares = consultarAvataresDesbloqueadosPorUsuarioId(usuarioId);
            return !avatares.isEmpty();
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw e;
        }
    }
}