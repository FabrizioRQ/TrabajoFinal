package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.Niño;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NiñoRepository extends JpaRepository<Niño, Long> {
    @Query("SELECT COUNT(n) > 0 FROM Niño n WHERE n.idUsuario.id = :idUsuario")
    boolean existsByIdUsuario(@Param("idUsuario") Long idUsuario);

    @Query("SELECT COUNT(n) FROM Niño n WHERE n.idPsicologo.id = :idPsicologo")
    long countByIdPsicologo(@Param("idPsicologo") Long idPsicologo);

    @Query("SELECT n FROM Niño n WHERE n.idUsuario.id = :usuarioId")
    Optional<Niño> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}