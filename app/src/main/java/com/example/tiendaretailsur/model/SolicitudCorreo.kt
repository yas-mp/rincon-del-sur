package com.example.tiendaretailsur.model

data class SolicitudCorreo(
    val id: Int,
    val rutUsuario: String,
    val nombreUsuario: String,
    val correoActual: String,
    val correoNuevo: String,
    val motivo: String,
    val estado: String
) {
}