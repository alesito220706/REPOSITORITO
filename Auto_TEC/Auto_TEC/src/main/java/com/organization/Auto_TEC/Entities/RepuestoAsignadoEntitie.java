package com.organization.Auto_TEC.Entities;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "repuestos_asignados")
public class RepuestoAsignadoEntitie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnostico_id", nullable = false)
    private DiagnosticoEntitie diagnostico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "repuesto_id", nullable = false)
    private RepuestosEntitie repuesto;

    @Column(nullable = false)
    private int cantidad = 1;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    public RepuestoAsignadoEntitie() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DiagnosticoEntitie getDiagnostico() { return diagnostico; }
    public void setDiagnostico(DiagnosticoEntitie diagnostico) { this.diagnostico = diagnostico; }
    public RepuestosEntitie getRepuesto() { return repuesto; }
    public void setRepuesto(RepuestosEntitie repuesto) { this.repuesto = repuesto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() {
        if (precioUnitario == null) return BigDecimal.ZERO;
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}