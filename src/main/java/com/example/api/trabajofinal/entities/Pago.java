package com.example.api.trabajofinal.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago", nullable = false)
    private Long id;
    // Monto del pago realizado (en la moneda definida por el sistema)
    @NotNull
    @Column(name = "monto", nullable = false)
    private Integer monto;
    // Fecha y hora exacta en que se efectuó el pago
    @NotNull
    @Column(name = "fecha_pago", nullable = false)
    private Instant fechaPago;
    // Método de pago utilizado (por ejemplo: “Tarjeta”, “Efectivo”, “Transferencia”)
    @Size(max = 20)
    @NotNull
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private String metodoPago;
    // Estado actual del pago (por ejemplo: “Completado”, “Pendiente”, “Fallido”)
    @Size(max = 20)
    @NotNull
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

}
