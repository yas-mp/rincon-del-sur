package com.example.tiendaretailsur.model

data class Perfil(
    val nombre: String = "",
    val correo: String = "",
    val turno: String = "",
    val cargo: String = ""
)

data class PerfilUiState(
    val perfil: Perfil = Perfil(),
    val avatarBytes: ByteArray? = null,
    val contrasenaActual: String = "",
    val nuevaContrasena: String = "",
    val confirmarContrasena: String = "",
    val contrasenaActualVisible: Boolean = false,
    val nuevaContrasenaVisible: Boolean = false,
    val confirmarContrasenaVisible: Boolean = false,
    val errorContrasenaActual: String? = null,
    val errorNuevaContrasena: String? = null,
    val errorConfirmarContrasena: String? = null,
    val mensajeSnackbar: String? = null,
    val dialogoCorreoAbierto: Boolean = false,
    val nuevoCorreoSolicitado: String = "",
    val motivoCorreoSolicitado: String = "",
    val errorNuevoCorreo: String? = null,
    val tieneSolicitudCorreoPendiente: Boolean = false
)