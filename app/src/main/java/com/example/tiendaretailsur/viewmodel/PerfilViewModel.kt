package com.example.tiendaretailsur.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tiendaretailsur.data.UsuarioDbHelper
import com.example.tiendaretailsur.model.Perfil
import com.example.tiendaretailsur.model.PerfilUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PerfilViewModel(
    private val dbHelper: UsuarioDbHelper
) : ViewModel() {
    private var correoUsuario: String = "admin@rincondelsur.cl"
    private var rutUsuario: String = ""
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        cargarPerfil("admin@rincondelsur.cl")
    }

    fun cargarPerfil(correo: String) {
        if (correo.isBlank()) return
        correoUsuario = correo
        val usuario = try {
            dbHelper.obtenerUsuarioPorCorreo(correo)
        } catch (e: Exception) {
            null
        }
        rutUsuario = usuario?.rut ?: ""

        val tienePendiente = try {
            if (rutUsuario.isNotBlank()) {
                dbHelper.listarSolicitudesCorreoPendientes().any { it.rutUsuario == rutUsuario }
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }

        val perfilTienda = if (usuario != null) {
            Perfil(
                nombre = usuario.nombreCompleto,
                correo = usuario.correo,
                turno = if (usuario.perfil in listOf("P-01", "P-02", "JEFA_TIENDA", "SUBGERENTE")) "N/A" else "Turno Diurno",
                cargo = usuario.perfil
            )
        } else {
            Perfil(
                nombre = "Gabriel Gómez",
                correo = correo,
                turno = "N/A",
                cargo = "SUBGERENTE"
            )
        }
        _uiState.update {
            it.copy(
                perfil = perfilTienda,
                avatarBytes = usuario?.avatar,
                tieneSolicitudCorreoPendiente = tienePendiente
            )
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
        val motivo = estado.motivoCorreoSolicitado.trim()

        if (nuevoCorreo.isBlank() || !nuevoCorreo.contains("@") || !nuevoCorreo.contains(".")) {
            _uiState.update { it.copy(errorNuevoCorreo = "Ingresa un correo electrónico válido") }
            return
        }
        if (nuevoCorreo.equals(estado.perfil.correo, ignoreCase = true)) {
            _uiState.update { it.copy(errorNuevoCorreo = "El nuevo correo debe ser distinto al actual") }
            return
        }

        val ok = try {
            dbHelper.crearSolicitudCorreo(
                rutUsuario = rutUsuario.ifBlank { estado.perfil.correo },
                nombreUsuario = estado.perfil.nombre,
                correoActual = estado.perfil.correo,
                correoNuevo = nuevoCorreo,
                motivo = motivo
            ) > 0
        } catch (e: Exception) {
            false
        }

        if (ok) {
            _uiState.update {
                it.copy(
                    dialogoCorreoAbierto = false,
                    tieneSolicitudCorreoPendiente = true,
                    mensajeSnackbar = "Solicitud enviada al Subgerente para aprobación"
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    dialogoCorreoAbierto = false,
                    mensajeSnackbar = "No se pudo registrar la solicitud de correo"
                )
            }
        }
    }

    fun setAvatarBytes(bytes: ByteArray?) {
        _uiState.update { it.copy(avatarBytes = bytes) }
        if (bytes != null) {
            dbHelper.actualizarAvatarPorCorreo(correoUsuario, bytes)
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

        val errorActual = when {
            actual.isBlank() -> "Ingresa tu contraseña actual"
            dbHelper.autenticarUsuario(correoUsuario, actual) == null -> "La contraseña actual es incorrecta"
            else -> null
        }

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

        val filasAfectadas = dbHelper.actualizarContrasena(correoUsuario, nueva)
        if (filasAfectadas > 0) {
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
        } else {
            _uiState.update {
                it.copy(mensajeSnackbar = "No se pudo actualizar la contraseña")
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