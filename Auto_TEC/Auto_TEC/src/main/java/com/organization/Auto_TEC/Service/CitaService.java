package com.organization.Auto_TEC.Service;

import com.organization.Auto_TEC.DTO.CitaDTO;
import com.organization.Auto_TEC.Entities.citaEntitie;
import com.organization.Auto_TEC.Entities.citaEstado;
import com.organization.Auto_TEC.Entities.citaTipo;
import com.organization.Auto_TEC.Entities.usuarioEntitie;
import com.organization.Auto_TEC.Repository.CitaRepository;
import com.organization.Auto_TEC.Repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository; // 👈 NECESARIO

    @Autowired
    public CitaService(CitaRepository citaRepository, UsuarioRepository usuarioRepository) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ✅ Obtener todas las citas
    public List<citaEntitie> obtenerTodas() {
        return citaRepository.findAll();
    }

    // ✅ Obtener cita por ID (usa Long, no Integer)
    public Optional<citaEntitie> obtenerPorId(Long id) {
        return citaRepository.findById(id);
    }

    // ✅ Obtener citas por estado (enum)
    public List<citaEntitie> obtenerPorEstado(citaEstado estado) {
        return citaRepository.findByEstado(estado);
    }

    // ✅ Obtener citas por usuario
    public List<citaEntitie> obtenerPorUsuario(Long usuarioId) {
        return citaRepository.findByUsuarioId(usuarioId);
    }

    // ✅ Obtener citas por tipo (enum)
    public List<citaEntitie> obtenerPorTipo(citaTipo tipo) {
        return citaRepository.findByTipoCita(tipo);
    }

    // ✅ Obtener citas personalizadas
    public List<citaEntitie> obtenerPendientes() {
        return citaRepository.findByEstado(citaEstado.PENDIENTE);
    }

    public List<citaEntitie> obtenerConfirmadas() {
        return citaRepository.findByEstado(citaEstado.CONFIRMADA);
    }

    public List<citaEntitie> obtenerCompletadas() {
        return citaRepository.findByEstado(citaEstado.COMPLETADA);
    }

    public List<citaEntitie> obtenerCanceladas() {
        return citaRepository.findByEstado(citaEstado.CANCELADA);
    }

    // ✅ Guardar o actualizar cita
    public citaEntitie guardar(citaEntitie cita) {
        if (cita.getId() == null) {
            cita.setFechaCreacion(LocalDateTime.now());
        }
        return citaRepository.save(cita);
    }

    // ✅ Crear nueva cita
    public citaEntitie crearCita(citaEntitie cita) {
        cita.setFechaCreacion(LocalDateTime.now());
        cita.setEstado(citaEstado.PENDIENTE);
        return citaRepository.save(cita);
    }

    // ✅ Eliminar cita
    public void eliminar(Long id) {
        citaRepository.deleteById(id);
    }

    // ✅ Cambiar estado de cita
    public void cambiarEstado(Long id, citaEstado nuevoEstado) {
        Optional<citaEntitie> citaOpt = citaRepository.findById(id);
        if (citaOpt.isPresent()) {
            citaEntitie cita = citaOpt.get();
            cita.setEstado(nuevoEstado);
            citaRepository.save(cita);
        }
    }

    // ✅ Atajos para cambiar estado
    public void confirmarCita(Long id) {
        cambiarEstado(id, citaEstado.CONFIRMADA);
    }

    public void completarCita(Long id) {
        cambiarEstado(id, citaEstado.COMPLETADA);
    }

    public void cancelarCita(Long id) {
        cambiarEstado(id, citaEstado.CANCELADA);
    }

    // ✅ Contadores
    public long contarPorEstado(citaEstado estado) {
        return citaRepository.findByEstado(estado).size();
    }

    public long contarTodas() {
        return citaRepository.count();
    }

    public long contarPorUsuario(Long usuarioId) {
        return citaRepository.findByUsuarioId(usuarioId).size();
    }

    @Transactional
    public citaEntitie agendarCita(CitaDTO dto, String usernameOrEmail) {
        // 1. Encontrar el usuario autenticado (Cliente)
        usuarioEntitie usuario = usuarioRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail));

        // 2. Crear el objeto LocalDateTime a partir de los campos del DTO (fecha y
        // hora)
        LocalDateTime fechaHoraCita = dto.getFechaFormulario().atTime(dto.getHoraCita());

        // 3. Crear la entidad
        citaEntitie nuevaCita = new citaEntitie();
        nuevaCita.setUsuario(usuario);
        nuevaCita.setTipoCita(dto.getTipoCita());
        nuevaCita.setFechaCita(fechaHoraCita);
        nuevaCita.setNotas(dto.getNotas());

        // Asignar valores por defecto (aunque ya están en la Entidad, es buena
        // práctica)
        nuevaCita.setEstado(citaEstado.PENDIENTE);
        nuevaCita.setDuracionEstimada(60); // O usa el valor del DTO si lo incluyes
        nuevaCita.setFechaCreacion(LocalDateTime.now());

        // 4. Guardar la cita
        return citaRepository.save(nuevaCita);
    }

    // ✅ Estadísticas de citas
    public CitaEstadisticas obtenerEstadisticas() {
        return new CitaEstadisticas(
                contarTodas(),
                contarPorEstado(citaEstado.PENDIENTE),
                contarPorEstado(citaEstado.CONFIRMADA),
                contarPorEstado(citaEstado.COMPLETADA),
                contarPorEstado(citaEstado.CANCELADA));
    }

    // ✅ Clase interna para estadísticas
    public static class CitaEstadisticas {
        private final long total;
        private final long pendientes;
        private final long confirmadas;
        private final long completadas;
        private final long canceladas;

        public CitaEstadisticas(long total, long pendientes, long confirmadas, long completadas, long canceladas) {
            this.total = total;
            this.pendientes = pendientes;
            this.confirmadas = confirmadas;
            this.completadas = completadas;
            this.canceladas = canceladas;
        }

        public long getTotal() {
            return total;
        }

        public long getPendientes() {
            return pendientes;
        }

        public long getConfirmadas() {
            return confirmadas;
        }

        public long getCompletadas() {
            return completadas;
        }

        public long getCanceladas() {
            return canceladas;
        }
    }

    public List<citaEntitie> obtenerCitasHoy() {
        LocalDateTime inicioDelDia = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime finDelDia = inicioDelDia.plusDays(1);
        return citaRepository.findByFechaCitaBetween(inicioDelDia, finDelDia);
    }
}
