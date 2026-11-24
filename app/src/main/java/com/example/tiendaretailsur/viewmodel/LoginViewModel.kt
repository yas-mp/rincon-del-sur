package com.example.tiendaretailsur.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.GestorSesion
import com.example.tiendaretailsur.data.UsuarioRepository
import com.example.tiendaretailsur.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val usuarioRepository: UsuarioRepository = UsuarioRepository(),
    private val gestorSesion: GestorSesion? = GestorSesion(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChanged(correo: String) {
        _uiState.update {
            it.copy(correo = correo, errorCorreo = null, mensajeExitoLogin = null)
        }
    }

    fun onContrasenaChanged(contrasena: String) {
        _uiState.update {
            it.copy(contrasena = contrasena, errorContrasena = null, mensajeExitoLogin = null)
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        val currentState = _uiState.value
        val correoOIdentificador = currentState.correo.trim()
        val contrasena = currentState.contrasena.trim()

        if (correoOIdentificador.isBlank()) {
            _uiState.update { it.copy(errorCorreo = "Ingresa tu correo o RUT") }
            return
        }
        if (contrasena.isBlank()) {
            _uiState.update { it.copy(errorContrasena = "La contraseña es requerida") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorCorreo = null, errorContrasena = null) }
            try {
                val usuario = usuarioRepository.iniciarSesion(correoOIdentificador, contrasena)

                if (!usuario.activo) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginExitoso = false,
                            usuarioAutenticado = null,
                            errorContrasena = "Tu cuenta se encuentra deshabilitada. Contacta a Administración."
                        )
                    }
                    return@launch
                }

                val idNumerico = usuario.codigo.toLongOrNull() ?: -1L
                val esAdmin = usuario.perfil.uppercase() in listOf(
                    "P-01", "JEFA_TIENDA", "JEFA DE TIENDA", "P-02", "SUBGERENTE", "P-03", "SUPERVISOR"
                )
                gestorSesion?.guardarSesion(
                    idUsuario = idNumerico,
                    correo = usuario.correo,
                    nombre = usuario.nombreCompleto,
                    perfil = usuario.perfil,
                    isAdmin = esAdmin
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginExitoso = true,
                        usuarioAutenticado = usuario,
                        errorCorreo = null,
                        errorContrasena = null,
                        mensajeExitoLogin = "Bienvenido/a ${usuario.nombreCompleto} (${usuario.perfil})"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginExitoso = false,
                        errorContrasena = e.localizedMessage ?: "Credenciales incorrectas o usuario no encontrado"
                    )
                }
            }
        }
    }
}