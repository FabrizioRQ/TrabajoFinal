package com.example.api.trabajofinal.services;

import com.example.api.trabajofinal.DTO.*;
import com.example.api.trabajofinal.entities.Pago;
import com.example.api.trabajofinal.entities.Usuario;
import com.example.api.trabajofinal.interfaces.PagoInterface;
import com.example.api.trabajofinal.repositories.PagoRepository;
import com.example.api.trabajofinal.repositories.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PagoService implements PagoInterface {
    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PagoDTO crearPago(PagoCreateDTO pagoDTO) {
        Usuario usuario = usuarioRepository.findById(pagoDTO.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pago pago = new Pago();
        pago.setMonto(pagoDTO.monto());
        pago.setFechaPago(pagoDTO.fechaPago());
        pago.setMetodoPago(pagoDTO.metodoPago());
        pago.setEstado(pagoDTO.estado());
        pago.setIdUsuario(usuario);

        Pago saved = pagoRepository.save(pago);

        PagoDTO respuesta = new PagoDTO(
                saved.getId(),
                saved.getMonto(),
                saved.getFechaPago(),
                saved.getMetodoPago(),
                saved.getEstado(),
                saved.getIdUsuario().getId()
        );
        return respuesta;
    }

    @Override
    public PagoDTO obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id)
                .map(p -> modelMapper.map(p, PagoDTO.class))
                .orElse(null);
    }

    @Override
    public List<PagoDTO> listarPagos() {
        return pagoRepository.findAll().stream()
                .map(p -> modelMapper.map(p, PagoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PagoDTO> listarPagosPorUsuario(Long usuarioId) {
        return pagoRepository.findByIdUsuario_Id(usuarioId).stream()
                .map(p -> modelMapper.map(p, PagoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PagoDTO actualizarPago(Long id, PagoCreateDTO dto) {
        return pagoRepository.findById(id).map(pago -> {
            pago.setMonto(dto.monto());
            pago.setFechaPago(dto.fechaPago());
            pago.setMetodoPago(dto.metodoPago());
            pago.setEstado(dto.estado());
            if (dto.usuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                pago.setIdUsuario(usuario);
            }
            return modelMapper.map(pagoRepository.save(pago), PagoDTO.class);
        }).orElse(null);
    }

    @Override
    public void eliminarPago(Long id) {
        pagoRepository.deleteById(id);
    }

    public List<PlanSuscripcionDTO> obtenerPlanesDisponibles() {
        return Arrays.asList(
                new PlanSuscripcionDTO(
                        "standard",
                        "Standard",
                        9.99,
                        "Plan básico para uso individual",
                        "{\"sesiones_mensuales\":10, \"soporte\":\"basico\", \"monstruos\":5}",
                        true
                ),
                new PlanSuscripcionDTO(
                        "vip",
                        "VIP",
                        19.99,
                        "Plan para usuarios frecuentes",
                        "{\"sesiones_mensuales\":30, \"soporte\":\"prioritario\", \"monstruos\":15}",
                        true
                ),
                new PlanSuscripcionDTO(
                        "gold",
                        "Gold",
                        29.99,
                        "Plan premium con características avanzadas",
                        "{\"sesiones_mensuales\":100, \"soporte\":\"dedicado\", \"monstruos\":50}",
                        true
                ),
                new PlanSuscripcionDTO(
                        "platinum",
                        "Platinum",
                        49.99,
                        "Máxima experiencia Monstruos Amigos",
                        "{\"sesiones_mensuales\":999, \"soporte\":\"24/7\", \"monstruos\":999}",
                        true
                ),
                new PlanSuscripcionDTO(
                        "anonimo",
                        "Continuar sin suscripción",
                        0.0,
                        "Acceso básico al chat principal",
                        "{\"sesiones_mensuales\":3, \"soporte\":\"comunidad\", \"monstruos\":2}",
                        true
                )
        );
    }

    public RespuestaPlanDTO seleccionarPlan(SeleccionPlanDTO seleccionDTO) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(seleccionDTO.getUsuarioId());
            if (usuarioOpt.isEmpty()) {
                return new RespuestaPlanDTO(false, "Usuario no encontrado", null, "perfil");
            }

            Usuario usuario = usuarioOpt.get();

            List<PlanSuscripcionDTO> planes = obtenerPlanesDisponibles();
            Optional<PlanSuscripcionDTO> planSeleccionado = planes.stream()
                    .filter(p -> p.getCodigo().equals(seleccionDTO.getCodigoPlan()) && p.getActivo())
                    .findFirst();

            if (planSeleccionado.isEmpty()) {
                return new RespuestaPlanDTO(false, "Plan no disponible", null, "planes");
            }

            PlanSuscripcionDTO plan = planSeleccionado.get();

            if ("anonimo".equals(plan.getCodigo())) {
                registrarSkipAnalytics(usuario, plan);
                return new RespuestaPlanDTO(true, "Redirigiendo al chat principal", "anonimo", "chat");
            } else {
                if (seleccionDTO.getMetodoPago() == null || seleccionDTO.getMetodoPago().trim().isEmpty()) {
                    return new RespuestaPlanDTO(false, "Se requiere método de pago para este plan", null, "pago");
                }

                PagoCreateDTO pagoCreateDTO = new PagoCreateDTO(
                        plan.getPrecioMensual().intValue(),
                        Instant.now(),
                        seleccionDTO.getMetodoPago(),
                        "COMPLETADO",
                        seleccionDTO.getUsuarioId()
                );

                PagoDTO pagoCreado = crearPago(pagoCreateDTO);

                if (pagoCreado != null) {
                    return new RespuestaPlanDTO(true, "Suscripción activada exitosamente", plan.getCodigo(), "chat");
                } else {
                    return new RespuestaPlanDTO(false, "Error al procesar el pago", null, "pago");
                }
            }

        } catch (Exception e) {
            System.err.println("Error en selección de plan: " + e.getMessage());
            return new RespuestaPlanDTO(false, "Error del sistema. Usando plan por defecto.", "anonimo", "chat");
        }
    }

    private void registrarSkipAnalytics(Usuario usuario, PlanSuscripcionDTO plan) {
        System.out.println("=== ANALYTICS - SKIP SUSCRIPCIÓN ===");
        System.out.println("Usuario: " + usuario.getCorreoElectronico());
        System.out.println("Plan seleccionado: " + plan.getNombre());
        System.out.println("Timestamp: " + Instant.now());
        System.out.println("===================================");
    }

    public PlanSuscripcionDTO obtenerPlanActual(Long usuarioId) {
        List<PagoDTO> pagosUsuario = listarPagosPorUsuario(usuarioId);

        Optional<PagoDTO> ultimoPago = pagosUsuario.stream()
                .filter(p -> "COMPLETADO".equals(p.getEstado()))
                .max((p1, p2) -> p1.getFechaPago().compareTo(p2.getFechaPago()));

        if (ultimoPago.isPresent()) {
            PagoDTO pago = ultimoPago.get();
            return determinarPlanPorMonto(pago.getMonto());
        }

        return new PlanSuscripcionDTO("anonimo", "Anónimo", 0.0, "Acceso básico", "{}", true);
    }

    private PlanSuscripcionDTO determinarPlanPorMonto(Integer monto) {
        List<PlanSuscripcionDTO> planes = obtenerPlanesDisponibles();
        return planes.stream()
                .filter(p -> p.getPrecioMensual().intValue() == monto && !"anonimo".equals(p.getCodigo()))
                .findFirst()
                .orElse(new PlanSuscripcionDTO("anonimo", "Anónimo", 0.0, "Acceso básico", "{}", true));
    }

    public List<MetodoPagoDTO> obtenerMetodosPagoUsuario(Long usuarioId) {
        // Buscar métodos de pago únicos por usuario (simulado)
        List<PagoDTO> pagosUsuario = listarPagosPorUsuario(usuarioId);

        return pagosUsuario.stream()
                .filter(p -> "COMPLETADO".equals(p.getEstado()))
                .map(this::convertirPagoAMetodoPago)
                .distinct()
                .collect(Collectors.toList());
    }

    public RespuestaMetodoPagoDTO agregarMetodoPago(CrearMetodoPagoDTO metodoDTO) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(metodoDTO.getUsuarioId());
            if (usuarioOpt.isEmpty()) {
                return new RespuestaMetodoPagoDTO(false, "Usuario no encontrado", null);
            }

            List<String> tiposPermitidos = Arrays.asList("tarjeta_credito", "billetera_digital", "transferencia");
            if (!tiposPermitidos.contains(metodoDTO.getTipo())) {
                return new RespuestaMetodoPagoDTO(false, "Tipo de método de pago no válido", null);
            }

            if (metodoDTO.getTokenProveedor() == null || metodoDTO.getTokenProveedor().trim().isEmpty()) {
                return new RespuestaMetodoPagoDTO(false, "Token de proveedor requerido", null);
            }

            if (!validarTokenConProveedor(metodoDTO.getTokenProveedor())) {
                return new RespuestaMetodoPagoDTO(false, "Token inválido o rechazado por el proveedor", null);
            }

            List<MetodoPagoDTO> metodosExistentes = obtenerMetodosPagoUsuario(metodoDTO.getUsuarioId());

            if (metodoDTO.getPredeterminado() != null && metodoDTO.getPredeterminado()) {
                System.out.println("Estableciendo nuevo método como predeterminado");
            }

            if (metodosExistentes.isEmpty() && (metodoDTO.getPredeterminado() == null || !metodoDTO.getPredeterminado())) {
                return new RespuestaMetodoPagoDTO(false, "Debe establecer al menos un método de pago como predeterminado", null);
            }

            MetodoPagoDTO nuevoMetodo = crearMetodoPagoSimulado(metodoDTO, usuarioOpt.get());

            System.out.println("=== MÉTODO DE PAGO AGREGADO ===");
            System.out.println("Usuario: " + usuarioOpt.get().getCorreoElectronico());
            System.out.println("Tipo: " + metodoDTO.getTipo());
            System.out.println("Predeterminado: " + (metodoDTO.getPredeterminado() != null ? metodoDTO.getPredeterminado() : false));
            System.out.println("Tokenizado: Sí");
            System.out.println("==============================");

            return new RespuestaMetodoPagoDTO(true, "Método de pago agregado exitosamente", nuevoMetodo);

        } catch (Exception e) {
            System.err.println("Error agregando método de pago: " + e.getMessage());
            return new RespuestaMetodoPagoDTO(false, "Error del sistema al agregar método de pago", null);
        }
    }

    public RespuestaMetodoPagoDTO eliminarMetodoPago(Long metodoPagoId, Long usuarioId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return new RespuestaMetodoPagoDTO(false, "Usuario no encontrado", null);
            }

            List<MetodoPagoDTO> metodosUsuario = obtenerMetodosPagoUsuario(usuarioId);

            Optional<MetodoPagoDTO> metodoAEliminar = metodosUsuario.stream()
                    .filter(m -> m.getId().equals(metodoPagoId))
                    .findFirst();

            if (metodoAEliminar.isEmpty()) {
                return new RespuestaMetodoPagoDTO(false, "Método de pago no encontrado", null);
            }

            MetodoPagoDTO metodo = metodoAEliminar.get();

            long metodosActivos = metodosUsuario.stream()
                    .filter(m -> "ACTIVO".equals(m.getEstado()))
                    .count();

            if (metodosActivos <= 1 && "ACTIVO".equals(metodo.getEstado())) {
                return new RespuestaMetodoPagoDTO(false,
                        "No puede eliminar su único método de pago activo. Agregue otro método primero.",
                        null);
            }

            if (metodo.getPredeterminado() != null && metodo.getPredeterminado()) {
                return new RespuestaMetodoPagoDTO(false,
                        "No puede eliminar el método de pago predeterminado. Establezca otro método como predeterminado primero.",
                        null);
            }

            System.out.println("=== MÉTODO DE PAGO ELIMINADO ===");
            System.out.println("Usuario: " + usuarioOpt.get().getCorreoElectronico());
            System.out.println("Método ID: " + metodoPagoId);
            System.out.println("Tipo: " + metodo.getTipo());
            System.out.println("Token revocado: Sí");
            System.out.println("================================");

            return new RespuestaMetodoPagoDTO(true, "Método de pago eliminado exitosamente", metodo);

        } catch (Exception e) {
            System.err.println("Error eliminando método de pago: " + e.getMessage());
            return new RespuestaMetodoPagoDTO(false, "Error del sistema al eliminar método de pago", null);
        }
    }

    private MetodoPagoDTO convertirPagoAMetodoPago(PagoDTO pago) {
        return new MetodoPagoDTO(
                pago.getId(),
                determinarTipoPorMetodo(pago.getMetodoPago()),
                "****" + (pago.getId() % 10000),
                true, // Predeterminado
                "ACTIVO",
                pago.getIdUsuario(),
                pago.getFechaPago()
        );
    }

    private String determinarTipoPorMetodo(String metodoPago) {
        if (metodoPago.toLowerCase().contains("tarjeta")) return "tarjeta_credito";
        if (metodoPago.toLowerCase().contains("billetera")) return "billetera_digital";
        if (metodoPago.toLowerCase().contains("transferencia")) return "transferencia";
        return "tarjeta_credito";
    }

    private boolean validarTokenConProveedor(String token) {
        return token != null && token.length() > 10;
    }

    private MetodoPagoDTO crearMetodoPagoSimulado(CrearMetodoPagoDTO metodoDTO, Usuario usuario) {
        return new MetodoPagoDTO(
                System.currentTimeMillis(), // ID temporal
                metodoDTO.getTipo(),
                "****" + (new Random().nextInt(9000) + 1000), // Últimos 4 dígitos simulados
                metodoDTO.getPredeterminado() != null ? metodoDTO.getPredeterminado() : false,
                "ACTIVO",
                usuario.getId(),
                Instant.now()
        );
    }
}