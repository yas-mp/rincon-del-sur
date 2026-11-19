package com.example.tiendaretailsur.model

data class PrimerIngresoUiState(
    val nuevaContrasena: String = "",
    val confirmarContrasena: String = "",
    val preguntaSeleccionada: String = "",
    val respuestaSeguridad: String = "",

    val nuevaContrasenaVisible: Boolean = false,
    val confirmarContrasenaVisible: Boolean = false,

    val errorNuevaContrasena: String? = null,
    val errorConfirmarContrasena: String? = null,
    val errorPregunta: String? = null,
    val errorRespuesta: String? = null,
    val errorGeneral: String? = null,

    val configuracionExitosa: Boolean = false
)