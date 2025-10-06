package com.example.api.trabajofinal.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "diario_emocional")
public class DiarioEmocional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diario", nullable = false)
    private Long id;
    // Fecha en la que se registró la emoción
    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    // Emoción registrada en el día (por ejemplo: alegría, miedo, sorpresa, etc.)
    @Size(max = 100)
    @NotNull
    @Column(name = "emocion_registrada", nullable = false, length = 100)
    private String emocionRegistrada;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"id_niño\"", nullable = false)
    private Niño idNiño;

}
