package com.example.api.trabajofinal.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "actividad_juego")
public class ActividadJuego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad", nullable = false)
    private Long id;
    // Nombre de la actividad o juego realizado
    @Size(max = 200)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;
    // Emoción registrada durante la actividad (por ejemplo: alegría, sorpresa, tristeza)
    @Size(max = 100)
    @NotNull
    @Column(name = "emocion", nullable = false, length = 100)
    private String emocion;
    // Puntos ganados por el niño durante la actividad
    @NotNull
    @Column(name = "puntos_ganados", nullable = false)
    private Integer puntosGanados;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"id_niño\"", nullable = false)
    private Niño idNiño;

}
