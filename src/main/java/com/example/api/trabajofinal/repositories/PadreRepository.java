package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.Padre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PadreRepository extends JpaRepository<Padre, Long> {
}