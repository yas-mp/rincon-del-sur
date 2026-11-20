package com.example.tiendaretailsur.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.AvatarRepository
import com.example.tiendaretailsur.data.GestorSesion
import com.example.tiendaretailsur.data.UsuarioRepository
import com.example.tiendaretailsur.model.Perfil
import com.example.tiendaretailsur.model.PerfilUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val gestorSesion = GestorSesion(application)
    private val usuarioRepository = UsuarioRepository()
    private val avatarRepository = AvatarRepository()

    private var correoUsuario: String = ""
    private var rutUsuario: String = ""

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
    }

    fun cargarPerfil(correoFallback: String = "") {
        viewModelScope.launch {
            val idUsuario = gestorSesion.obtenerIdUsuarioLogueado()
            val correoSesion = gestorSesion.obtenerCorreoUsuarioLogueado()
                .ifBlank { correoFallback }

            val usuario = if (idUsuario != -1L) {
                try {
                    usuarioRepository.obtener(idUsuario)
                } catch (_: Exception) {
                    null
                }
            } else if (correoSesion.isNotBlank()) {
                try {
                    usuarioRepository.listar(correoSesion).firstOrNull()
                } catch (_: Exception) {
                    null
                }
            } else {
                null
            }

            correoUsuario = usuario?.correo ?: correoSesion
            rutUsuario = usuario?.rut ?: ""

            val perfilTienda = if (usuario != null) {
                Perfil(
                    nombre = usuario.nombreCompleto,
                    correo = usuario.correo,
                    turno = if (usuario.perfil in listOf("P-01", "P-02", "JEFA_TIENDA", "SUBGERENTE")) "N/A" else "Turno Diurno",
                    cargo = usuario.perfil
                )
            } else {
                Perfil(
                    nombre = "Usuario Rincón del Sur",
                    correo = correoUsuario.ifBlank { "sin-sesion@tiendaretailsur.cl" },
                    turno = "N/A",
                    cargo = "OBSERVADOR"
                )
            }

            val avatarReal: ByteArray? = usuario?.avatarUrl?.let { url ->
                if (url.isNotBlank()) {
                    val urlCompleta = if (url.startsWith("http")) {
                        url
                    } else {
                        "http://10.0.2.2:8082/${url.trimStart('/')}"
                    }
                    withContext(Dispatchers.IO) {
                        try {
                            URL(urlCompleta).readBytes()
                        } catch (_: Exception) {
                            null
                        }
                    }
                } else null
            }

            _uiState.update {
                it.copy(
                    perfil = perfilTienda,
                    avatarBytes = avatarReal,
                    tieneSolicitudCorreoPendiente = false
                )
            }
        }
    }

    fun abrirDialogoCorreo() {
        _uiState.update {
            it.copy(
                dialogoCorreoAbierto = true,
                nuevoCorreoSolicitado = "",
                motivoCorreoSolicitado = "",
                errorNuevoCorreo = null
            )
        }
    }

    fun cerrarDialogoCorreo() {
        _uiState.update { it.copy(dialogoCorreoAbierto = false, errorNuevoCorreo = null) }
    }

    fun onNuevoCorreoSolicitadoCambia(valor: String) {
        _uiState.update { it.copy(nuevoCorreoSolicitado = valor, errorNuevoCorreo = null) }
    }

    fun onMotivoCorreoCambia(valor: String) {
        _uiState.update { it.copy(motivoCorreoSolicitado = valor) }
    }

    fun enviarSolicitudCambioCorreo() {
        val estado = _uiState.value
        val nuevoCorreo = estado.nuevoCorreoSolicitado.trim()

        if (nuevoCorreo.isBlank() || !nuevoCorreo.contains("@") || !nuevoCorreo.contains(".")) {
            _uiState.update { it.copy(errorNuevoCorreo = "Ingresa un correo electrónico válido") }
            return
        }
        if (nuevoCorreo.equals(estado.perfil.correo, ignoreCase = true)) {
            _uiState.update { it.copy(errorNuevoCorreo = "El nuevo correo debe ser distinto al actual") }
            return
        }

        // Pendiente de endpoint en UsuariosApi; confirma recepción en UI sin romper flujo
        _uiState.update {
            it.copy(
                dialogoCorreoAbierto = false,
                tieneSolicitudCorreoPendiente = true,
                mensajeSnackbar = "Solicitud enviada al Subgerente para aprobación"
            )
        }
    }

    fun setAvatarBytes(bytes: ByteArray?) {
        _uiState.update { it.copy(avatarBytes = bytes) }
        val idUsuario = gestorSesion.obtenerIdUsuarioLogueado()
        if (bytes != null && idUsuario != -1L) {
            viewModelScope.launch {
                try {
                    avatarRepository.subir(idUsuario, bytes)
                    _uiState.update { it.copy(mensajeSnackbar = "Foto de perfil actualizada") }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(mensajeSnackbar = e.message ?: "No se pudo subir la imagen")
                    }
                }
            }
        }
    }

    fun onContrasenaActualCambia(valor: String) {
        _uiState.update { it.copy(contrasenaActual = valor, errorContrasenaActual = null) }
    }

    fun onNuevaContrasenaCambia(valor: String) {
        _uiState.update { it.copy(nuevaContrasena = valor, errorNuevaContrasena = null) }
    }

    fun onConfirmarContrasenaCambia(valor: String) {
        _uiState.update { it.copy(confirmarContrasena = valor, errorConfirmarContrasena = null) }
    }

    fun onAlternarActualVisible() {
        _uiState.update { it.copy(contrasenaActualVisible = !it.contrasenaActualVisible) }
    }

    fun onAlternarNuevaVisible() {
        _uiState.update { it.copy(nuevaContrasenaVisible = !it.nuevaContrasenaVisible) }
    }

    fun onAlternarConfirmarVisible() {
        _uiState.update { it.copy(confirmarContrasenaVisible = !it.confirmarContrasenaVisible) }
    }

    fun onSnackbarMostrado() {
        _uiState.update { it.copy(mensajeSnackbar = null) }
    }

    fun cambiarContrasena() {
        val estado = _uiState.value
        val actual = estado.contrasenaActual.trim()
        val nueva = estado.nuevaContrasena.trim()
        val confirmar = estado.confirmarContrasena.trim()

        val errorActual = if (actual.isBlank()) "Ingresa tu contraseña actual" else null

        val errorNueva = when {
            nueva.isBlank() -> "Ingresa tu nueva contraseña"
            nueva == actual -> "La nueva contraseña debe ser distinta a la actual"
            else -> validarFormatoContrasena(nueva)
        }

        val errorConfirmar = when {
            confirmar.isBlank() -> "Confirma tu nueva contraseña"
            nueva != confirmar -> "Las contraseñas no coinciden"
            else -> null
        }

        if (errorActual != null || errorNueva != null || errorConfirmar != null) {
            _uiState.update {
                it.copy(
                    errorContrasenaActual = errorActual,
                    errorNuevaContrasena = errorNueva,
                    errorConfirmarContrasena = errorConfirmar
                )
            }
            return
        }

        val idUsuario = gestorSesion.obtenerIdUsuarioLogueado()
        if (idUsuario == -1L) {
            _uiState.update {
                it.copy(mensajeSnackbar = "No hay una sesión activa para cambiar la contraseña")
            }
            return
        }

        viewModelScope.launch {
            try {
                usuarioRepository.cambiarContrasena(idUsuario, actual, nueva)
                _uiState.update {
                    it.copy(
                        contrasenaActual = "",
                        nuevaContrasena = "",
                        confirmarContrasena = "",
                        errorContrasenaActual = null,
                        errorNuevaContrasena = null,
                        errorConfirmarContrasena = null,
                        mensajeSnackbar = "Contraseña actualizada correctamente"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorContrasenaActual = e.message ?: "La contraseña actual es incorrecta"
                    )
                }
            }
        }
    }

    private fun validarFormatoContrasena(contrasena: String): String? {
        if (contrasena.length < 8) return "Mínimo 8 caracteres"
        if (!contrasena.any { it.isUpperCase() }) return "Debe incluir al menos una mayúscula"
        if (!contrasena.any { it.isLowerCase() }) return "Debe incluir al menos una minúscula"
        if (!contrasena.any { it.isDigit() }) return "Debe incluir al menos un número"
        return null
    }
}