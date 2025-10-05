package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    public Usuario findByCorreoElectronico(String correo);
    public Optional<Usuario> findById(Long id);
}
