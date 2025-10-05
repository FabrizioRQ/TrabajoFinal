package com.example.api.trabajofinal.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String tipo = "Bearer";
    private String correoElectronico;
    private String tipoUsuario;

    public JwtResponse(String token, String correoElectronico, String tipoUsuario) {
        this.token = token;
        this.correoElectronico = correoElectronico;
        this.tipoUsuario = tipoUsuario;
    }
}