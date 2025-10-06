package com.example.api.trabajofinal.controllers;

import com.example.api.trabajofinal.DTO.UsuarioDTO;
import com.example.api.trabajofinal.security.entities.PasswordResetToken;
import com.example.api.trabajofinal.entities.Usuario;
import com.example.api.trabajofinal.security.repositories.PasswordResetTokenRepository;
import com.example.api.trabajofinal.repositories.UsuarioRepository;
import com.example.api.trabajofinal.security.service.RateLimiterService;
import com.example.api.trabajofinal.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService; // Servicio principal para lógica de negocio de usuarios

    @Autowired
    private RateLimiterService rateLimiterService; // Servicio para limitar solicitudes y evitar abusos (rate limiting)

    @Autowired
    private UsuarioRepository usuarioRepositorie; // Acceso directo al repositorio de usuarios

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository; // Repositorio para manejar tokens de recuperación

    // Obtiene el perfil de un usuario específico (solo accesible por administradores)
    @GetMapping("/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Long idUsuario) {
        try {
            System.out.println("SOLICITUD PERFIL ID: " + idUsuario);
            UsuarioDTO usuario = usuarioService.obtenerUsuario(idUsuario);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            System.out.println("ERROR OBTENIENDO PERFIL: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error al obtener el perfil");
        }
    }

    // Verifica si un correo electrónico ya está registrado
    @GetMapping("/existe/{correo}")
    public ResponseEntity<?> verificarCorreoExistente(@PathVariable String correo) {
        try {
            System.out.println("VERIFICANDO EXISTENCIA DE: " + correo);
            boolean existe = usuarioService.existeUsuarioPorCorreo(correo);
            Map<String, Object> response = new HashMap<>();
            response.put("correo", correo);
            response.put("existe", existe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR VERIFICANDO CORREO: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error al verificar el correo");
        }
    }

    //Inicia el proceso de recuperación de contraseña
    // Se valida el correo y se simula el envío de un enlace con token temporal
    @PostMapping("/recuperacion/{correo}")
    public ResponseEntity<?> solicitarRecuperacionRealista(@PathVariable String correo) {
        try {
            System.out.println("SOLICITUD RECUPERACIÓN REALISTA PARA: " + correo);
            // Validación básica del correo
            if (correo == null || !correo.contains("@") || correo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Correo electrónico no válido");
            }
             // Verificación de límites de solicitudes
            String rateLimitKey = "recovery:" + correo;
            if (rateLimiterService.isBlocked(rateLimitKey)) {
                return ResponseEntity.badRequest().body("Demasiadas solicitudes. Intente más tarde.");
            }
            // Búsqueda del usuario activo
            Usuario usuario = usuarioRepositorie.findByCorreoElectronico(correo);
            if (usuario == null || !"ACTIVE".equals(usuario.getEstado())) {
                System.out.println("Usuario no encontrado o inactivo: " + correo);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Correo no registrado o inactivo.");
                response.put("simulacion", "Registre o ingrese su correo de nuevo");
                return ResponseEntity.ok(response);
            }
            
            // Creación de token de recuperación
            PasswordResetToken token = usuarioService.crearTokenParaUsuario(usuario);

            String enlaceRecuperacion = "http://localhost:8080/api/usuarios/restablecer-con-token?token=" + token.getToken();
            
            // Simulación del envío del correo electrónico
            System.out.println("===  SIMULACIÓN DE EMAIL ===");
            System.out.println("Para: " + correo);
            System.out.println("Asunto: Restablecimiento de Contraseña");
            System.out.println("Mensaje: Para restablecer tu contraseña haz clic en el siguiente enlace:");
            System.out.println("Enlace: " + enlaceRecuperacion);
            System.out.println("Token (solo para pruebas): " + token.getToken());
            System.out.println("Este enlace expira en 24 horas.");
            
            // Registrar el intento en el rate limiter
            rateLimiterService.recordFailedAttempt(rateLimitKey);

            // Respuesta final simulada
            Map<String, String> response = new HashMap<>();
            response.put("message", "Si el correo está registrado, recibirás un enlace de recuperación.");
            response.put("simulacion_email", "Email registrado correctamente");
            response.put("enlace_recuperacion", enlaceRecuperacion);
            response.put("instrucciones", "Ingrese o copie el enlace para restablecer su contraseña");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error al procesar la solicitud");
        }
    }

    @PostMapping("/restablecer-con-token")
    public ResponseEntity<?> restablecerConTokenEnURL(
            @RequestParam("token") String token,
            @RequestBody Map<String, String> request) {

        try {
            System.out.println("RESTABLECER CON TOKEN DESDE URL");
            System.out.println("Token recibido: " + token);

            String nuevaContraseña = request.get("nuevaContraseña");

            if (nuevaContraseña == null || nuevaContraseña.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("La nueva contraseña es requerida");
            }

            boolean resultado = usuarioService.restablecerConToken(token, nuevaContraseña);

            Map<String, Object> response = new HashMap<>();
            response.put("success", resultado);
            response.put("message", resultado ?
                    "Contraseña restablecida exitosamente" :
                    "No se pudo restablecer la contraseña. Token inválido o expirado.");

            return resultado ?
                    ResponseEntity.ok(response) :
                    ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    @GetMapping("/mi-perfil")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> obtenerMiPerfil(Authentication authentication) {
        try {
            String correoUsuarioAutenticado = authentication.getName();
            System.out.println("USUARIO SOLICITUD SU PERFIL: " + correoUsuarioAutenticado);

            UsuarioDTO usuario = usuarioService.obtenerMiPerfil(correoUsuarioAutenticado);
            return ResponseEntity.ok(usuario);

        } catch (Exception e) {
            System.out.println("ERROR USUARIO OBTENIENDO SU PERFIL: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/mi-perfil")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> actualizarMiPerfil(@Valid @RequestBody UsuarioDTO usuarioDTO,
                                                Authentication authentication) {
        try {
            String correoUsuarioAutenticado = authentication.getName();
            System.out.println("USUARIO ACTUALIZANDO SU PERFIL: " + correoUsuarioAutenticado);

            UsuarioDTO usuarioActualizado = usuarioService.actualizarMiPerfil(correoUsuarioAutenticado, usuarioDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Perfil actualizado exitosamente");
            response.put("usuario", usuarioActualizado);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR VALIDACIÓN PERFIL: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR ACTUALIZANDO PERFIL: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error al actualizar el perfil");
        }
    }
}
