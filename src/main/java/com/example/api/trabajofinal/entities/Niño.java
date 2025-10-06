package com.example.api.trabajofinal.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"niño\"")
public class Niño {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id_niño\"", nullable = false)
    private Long id;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

    @NotNull
    // Relación muchos-a-uno con la entidad
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_avatar", nullable = false)
    private Avatar idAvatar;

    @NotNull
    // Relación muchos-a-uno con la entidad
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_psicologo", nullable = false)
    private Psicologo idPsicologo;

    @NotNull
    // Relación muchos-a-uno con la entidad
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_padre", nullable = false)
    private Padre idPadre;

}
