package com.example.tiendaretailsur.model

data class FormularioRegistroUiState(
    val rut: String = "",
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val rolSeleccionado: RolUsuario = RolUsuario.OBSERVADOR,
    val esActivo: Boolean = true,

    val errorFormatoRut: String? = null,
    val errorNombre: String? = null,
    val errorFormatoCorreo: String? = null,
    val errorTelefono: String? = null,

    val contrasenaTemporalGenerada: String = "",
    val registroExitoso: Boolean = false
)