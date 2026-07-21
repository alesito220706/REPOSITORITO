package com.organization.Auto_TEC.Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;

@Entity
@Table(name = "diagnosticos")
public class DiagnosticoEntitie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false)
    private citaEntitie cita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mecanico_id", nullable = false)
    private usuarioEntitie mecanico;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fallas_encontradas", columnDefinition = "TEXT")
    private String fallasEncontradas;

    @Column(columnDefinition = "TEXT")
    private String recomendaciones;

    @Column(name = "acciones_seguir", columnDefinition = "TEXT")
    private String accionesSeguir;

    @Column(length = 20, nullable = false)
    private String estado = "BORRADOR";

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "diagnostico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RepuestoAsignadoEntitie> repuestosAsignados = new ArrayList<>();

    public DiagnosticoEntitie() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public citaEntitie getCita() { return cita; }
    public void setCita(citaEntitie cita) { this.cita = cita; }
    public usuarioEntitie getMecanico() { return mecanico; }
    public void setMecanico(usuarioEntitie mecanico) { this.mecanico = mecanico; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getFallasEncontradas() { return fallasEncontradas; }
    public void setFallasEncontradas(String fallasEncontradas) { this.fallasEncontradas = fallasEncontradas; }
    public String getRecomendaciones() { return recomendaciones; }
    public void setRecomendaciones(String recomendaciones) { this.recomendaciones = recomendaciones; }
    public String getAccionesSeguir() { return accionesSeguir; }
    public void setAccionesSeguir(String accionesSeguir) { this.accionesSeguir = accionesSeguir; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<RepuestoAsignadoEntitie> getRepuestosAsignados() { return repuestosAsignados; }
    public void setRepuestosAsignados(List<RepuestoAsignadoEntitie> repuestosAsignados) { this.repuestosAsignados = repuestosAsignados; }
}