package com.example.tiendaretailsur.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.UsuarioRepository
import com.example.tiendaretailsur.model.PasoRecuperacion
import com.example.tiendaretailsur.model.RecuperarContrasenaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecuperarContrasenaViewModel : ViewModel() {

    private val usuarioRepository = UsuarioRepository()

    private val _uiState = MutableStateFlow(RecuperarContrasenaUiState())
    val uiState: StateFlow<RecuperarContrasenaUiState> = _uiState.asStateFlow()

    fun onIdentificadorCambia(valor: String) {
        _uiState.update { it.copy(identificadorUsuario = valor, errorUsuario = null) }
    }

    fun onPregunta1Cambia(valor: String) {
        _uiState.update { it.copy(pregunta1 = valor, errorPregunta1 = null) }
    }

    fun onRespuesta1Cambia(valor: String) {
        _uiState.update { it.copy(respuesta1 = valor, errorRespuesta1 = null) }
    }

    fun onNuevaContrasenaCambia(valor: String) {
        _uiState.update { it.copy(nuevaContrasena = valor, errorNuevaContrasena = null) }
    }

    fun onConfirmarContrasenaCambia(valor: String) {
        _uiState.update { it.copy(confirmarContrasena = valor, errorConfirmarContrasena = null) }
    }

    fun onAlternarNuevaVisible() {
        _uiState.update { it.copy(nuevaContrasenaVisible = !it.nuevaContrasenaVisible) }
    }

    fun onAlternarConfirmarVisible() {
        _uiState.update { it.copy(confirmarContrasenaVisible = !it.confirmarContrasenaVisible) }
    }

    fun buscarUsuario() {
        val identificador = _uiState.value.identificadorUsuario.trim()
        if (identificador.isBlank()) {
            _uiState.update { it.copy(errorUsuario = "Ingresa tu correo electrónico o RUT") }
            return
        }

        viewModelScope.launch {
            try {
                // Valida que el colaborador exista y tenga pregunta configurada
                usuarioRepository.obtenerPreguntaSeguridad(identificador)
                // Carga el catálogo completo de preguntas desde el backend
                val preguntasDisponibles = usuarioRepository.listarPreguntasSeguridad()

                _uiState.update {
                    it.copy(
                        pasoActual = PasoRecuperacion.PREGUNTAS_SEGURIDAD,
                        correoUsuarioEncontrado = identificador,
                        listaPreguntas = preguntasDisponibles,
                        pregunta1 = "", // Se deja vacía para que el usuario elija la suya
                        errorPregunta1 = null,
                        errorUsuario = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorUsuario = e.message ?: "No se encontró ningún colaborador con ese correo o RUT"
                    )
                }
            }
        }
    }

    fun verificarRespuestas() {
        val estado = _uiState.value
        val identificador = estado.correoUsuarioEncontrado.trim()
        val preguntaElegida = estado.pregunta1.trim()
        val respuestaIngresada = estado.respuesta1.trim()

        if (preguntaElegida.isBlank()) {
            _uiState.update { it.copy(errorPregunta1 = "Selecciona tu pregunta de seguridad") }
            return
        }

        if (respuestaIngresada.isBlank()) {
            _uiState.update { it.copy(errorRespuesta1 = "Ingresa tu respuesta de seguridad") }
            return
        }

        viewModelScope.launch {
            try {
                val esCorrecta = usuarioRepository.verificarRespuesta(
                    rutOCorreo = identificador,
                    pregunta = preguntaElegida,
                    respuesta = respuestaIngresada
                )
                if (esCorrecta) {
                    _uiState.update {
                        it.copy(
                            pasoActual = PasoRecuperacion.CAMBIAR_CONTRASENA,
                            errorPregunta1 = null,
                            errorRespuesta1 = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(errorRespuesta1 = "La pregunta o la respuesta de seguridad son incorrectas")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorRespuesta1 = e.message ?: "Error al verificar la respuesta")
                }
            }
        }
    }

    fun guardarNuevaContrasena() {
        val estado = _uiState.value
        val errorNueva = validarFormatoContrasena(estado.nuevaContrasena)
        val errorConfirmar = when {
            estado.confirmarContrasena.isBlank() -> "Debes confirmar la contraseña"
            estado.nuevaContrasena != estado.confirmarContrasena -> "Las contraseñas no coinciden"
            else -> null
        }

        if (errorNueva != null || errorConfirmar != null) {
            _uiState.update {
                it.copy(
                    errorNuevaContrasena = errorNueva,
                    errorConfirmarContrasena = errorConfirmar
                )
            }
            return
        }

        val identificador = estado.correoUsuarioEncontrado.trim()
        val nuevaClave = estado.nuevaContrasena.trim()

        viewModelScope.launch {
            try {
                usuarioRepository.restablecerContrasena(identificador, nuevaClave)
                _uiState.update {
                    it.copy(pasoActual = PasoRecuperacion.COMPLETADO)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorNuevaContrasena = e.message ?: "Error al actualizar la contraseña")
                }
            }
        }
    }
    fun enviarClaveAlCorreo() {
        val identificador = _uiState.value.identificadorUsuario.trim()
        if (identificador.isBlank()) {
            _uiState.update { it.copy(errorUsuario = "Ingresa tu correo electrónico o RUT para enviarte la clave") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargandoCorreo = true, errorUsuario = null) }
            try {
                usuarioRepository.recuperarPorCorreo(identificador)
                _uiState.update {
                    it.copy(
                        cargandoCorreo = false,
                        pasoActual = PasoRecuperacion.CORREO_ENVIADO,
                        correoUsuarioEncontrado = identificador,
                        errorUsuario = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        cargandoCorreo = false,
                        errorUsuario = e.message ?: "No se pudo enviar la clave temporal al correo"
                    )
                }
            }
        }
    }

    private fun validarFormatoContrasena(contrasena: String): String? {
        if (contrasena.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        if (!contrasena.any { it.isUpperCase() }) return "Debe tener al menos una mayúscula"
        if (!contrasena.any { it.isLowerCase() }) return "Debe tener al menos una minúscula"
        if (!contrasena.any { it.isDigit() }) return "Debe tener al menos un número"
        return null
    }
}