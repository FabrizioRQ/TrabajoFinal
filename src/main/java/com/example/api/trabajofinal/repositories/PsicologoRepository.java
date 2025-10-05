package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
    public boolean existsByNumeroColegiatura(String numeroColegiatura);

    @Query("SELECT COUNT(p) > 0 FROM Psicologo p WHERE p.idUsuario.id = :idUsuario")
    boolean existsByIdUsuario(@Param("idUsuario") Long idUsuario);
}