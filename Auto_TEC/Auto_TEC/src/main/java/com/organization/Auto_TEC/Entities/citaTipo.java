package com.organization.Auto_TEC.Entities;

public enum citaTipo {
    VENTA("Venta de Vehículo"),
    SERVICIO("Servicio Técnico"),
    FINANCIAMIENTO("Financiamiento"),
    GENERAL("Consulta General"),
    PRUEBA_DE_MANEJO("Prueba de Manejo");

    private final String descripcion;

    citaTipo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getName() {
        return this.name();
    }
}
