package com.example.tiendaretailsur.model

data class Perfil(
    val nombre: String = "fulanito",
    val correo: String = "fulanito@tiendaretailsur.cl",
    val turno: String = "Turno Diurno",
    val cargo: String = "Subgerente Comercial"
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
    val mensajeSnackbar: String? = null
)