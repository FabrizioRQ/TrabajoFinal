package com.example.api.trabajofinal.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "avatar")
public class Avatar {
    // Identificador único del avatar
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avatar", nullable = false)
    private Long id;
    // Nombre o título del avatar
    @Size(max = 100)
    @NotNull
    @Column(name = "nombre_avatar", nullable = false, length = 100)
    private String nombreAvatar;
    // Descripción o configuración visual del avatar
    //Puede almacenar datos en formato texto o JSON que describen su apariencia
    @NotNull
    @Column(name = "apariencia", nullable = false, length = Integer.MAX_VALUE)
    private String apariencia;

}
