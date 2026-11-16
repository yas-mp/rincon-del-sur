package com.example.tiendaretailsur.model

data class FormularioConteoUiState(
    val puertaAcceso: String = "A1",
    val cantidadTransito: Int = 0,
    val cantidadClientes: Int = 0,
    val rangoEtario: String = "",
    val compania: String = "",
    val observador: String = "",
    val hora: String = "",
    val envioExitoso: Boolean = false,
    val mensajeFeedback: String? = null,
    val puedeRestarTransito: Boolean = false,
    val puedeRestarCliente: Boolean = false
)