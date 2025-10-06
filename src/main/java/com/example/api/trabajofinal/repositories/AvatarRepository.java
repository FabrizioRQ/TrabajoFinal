package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
  // Puedes agregar métodos personalizados aquí, por ejemplo:
  // Optional<Avatar> findByNombre(String nombre);
}
