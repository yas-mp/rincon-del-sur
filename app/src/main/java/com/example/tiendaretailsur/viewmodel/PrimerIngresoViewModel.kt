package com.example.tiendaretailsur.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tiendaretailsur.data.UsuarioDbHelper
import com.example.tiendaretailsur.model.PrimerIngresoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PrimerIngresoViewModel(
    private val dbHelper: UsuarioDbHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrimerIngresoUiState())
    val uiState: StateFlow<PrimerIngresoUiState> = _uiState.asStateFlow()

    val listaPreguntasSeg = listOf(
        "¿Cuál es el nombre de tu primera mascota?",
        "¿En qué ciudad nacieron tus padres?",
        "¿Cuál es tu deporte o pasatiempo favorito?",
        "¿Cuál fue el nombre de tu primera escuela?"
    )

    fun onNuevaContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(
                nuevaContrasena = valor,
                errorNuevaContrasena = null,
                errorConfirmarContrasena = null,
                errorGeneral = null
            )
        }
    }

    fun onConfirmarContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(
                confirmarContrasena = valor,
                errorConfirmarContrasena = null,
                errorGeneral = null
            )
        }
    }

    fun onAlternarNuevaContrasenaVisible() {
        _uiState.update { it.copy(nuevaContrasenaVisible = !it.nuevaContrasenaVisible) }
    }

    fun onAlternarConfirmarContrasenaVisible() {
        _uiState.update { it.copy(confirmarContrasenaVisible = !it.confirmarContrasenaVisible) }
    }

    fun onPreguntaSeleccionada(pregunta: String) {
        _uiState.update {
            it.copy(preguntaSeleccionada = pregunta, errorPregunta = null, errorGeneral = null)
        }
    }

    fun onRespuestaCambia(respuesta: String) {
        _uiState.update {
            it.copy(respuestaSeguridad = respuesta, errorRespuesta = null, errorGeneral = null)
        }
    }

    fun onGuardarConfiguracion(rutOCorreoUsuario: String) {
        val estado = _uiState.value

        val errorNueva = validarContrasena(estado.nuevaContrasena)
        val errorConfirmar = when {
            estado.confirmarContrasena.isBlank() -> "Debes confirmar tu nueva contraseña"
            estado.confirmarContrasena != estado.nuevaContrasena -> "Las contraseñas no coinciden"
            else -> null
        }
        val errorPregunta = if (estado.preguntaSeleccionada.isBlank()) {
            "Selecciona una pregunta de seguridad"
        } else null
        val errorRespuesta = if (estado.respuestaSeguridad.isBlank()) {
            "Ingresa tu respuesta de seguridad"
        } else null

        val hayErrores = listOf(errorNueva, errorConfirmar, errorPregunta, errorRespuesta).any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorNuevaContrasena = errorNueva,
                    errorConfirmarContrasena = errorConfirmar,
                    errorPregunta = errorPregunta,
                    errorRespuesta = errorRespuesta
                )
            }
            return
        }

        val filasActualizadas = dbHelper.completarPrimerIngreso(
            rutOCorreo = rutOCorreoUsuario,
            nuevaContrasena = estado.nuevaContrasena,
            preguntaSeguridad = estado.preguntaSeleccionada,
            respuestaSeguridad = estado.respuestaSeguridad
        )

        if (filasActualizadas > 0) {
            _uiState.update { it.copy(configuracionExitosa = true) }
        } else {
            _uiState.update {
                it.copy(errorGeneral = "No se pudo actualizar la cuenta. Intenta nuevamente.")
            }
        }
    }

    private fun validarContrasena(contrasena: String): String? {
        if (contrasena.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        if (!contrasena.any { it.isUpperCase() }) return "La contraseña debe tener al menos una mayúscula"
        if (!contrasena.any { it.isLowerCase() }) return "La contraseña debe tener al menos una minúscula"
        if (!contrasena.any { it.isDigit() }) return "La contraseña debe tener al menos un número"
        return null
    }
}