package com.example.tiendaretailsur.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.UsuarioRepository
import com.example.tiendaretailsur.model.FormularioRegistroUiState
import com.example.tiendaretailsur.model.RolUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FormularioRegistroViewModel : ViewModel() {

    private val usuarioRepository = UsuarioRepository()

    private val _uiState = MutableStateFlow(FormularioRegistroUiState())
    val uiState: StateFlow<FormularioRegistroUiState> = _uiState.asStateFlow()

    fun onRutCambia(nuevoRut: String) {
        val rutLimpio = nuevoRut.replace("[^0-9kK]".toRegex(), "").uppercase().take(9)
        _uiState.update {
            it.copy(rut = rutLimpio, errorFormatoRut = null, registroExitoso = false)
        }
    }

    fun onNombreCambia(valor: String) {
        _uiState.update {
            it.copy(nombre = valor, errorNombre = null, registroExitoso = false)
        }
    }

    fun onCorreoCambia(valor: String) {
        _uiState.update {
            it.copy(correo = valor, errorFormatoCorreo = null, registroExitoso = false)
        }
    }

    fun onTelefonoCambia(valor: String) {
        val soloNumeros = valor.filter { it.isDigit() }.take(9)
        _uiState.update {
            it.copy(telefono = soloNumeros, errorTelefono = null, registroExitoso = false)
        }
    }

    fun onSeleccionRol(rol: RolUsuario) {
        _uiState.update { it.copy(rolSeleccionado = rol, registroExitoso = false) }
    }

    fun onLimpiarFormulario() {
        _uiState.value = FormularioRegistroUiState()
    }

    fun onTerminosCambia(valor: Boolean) {
        _uiState.update { it.copy(aceptarTerminos = valor, errorTerminos = null) }
    }

    fun onRegistrar() {
        val estado = _uiState.value

        val errorRut = if (!validarFormatoRut(estado.rut)) "El RUT no tiene un formato válido" else null
        val errorNombre = when {
            estado.nombre.isBlank() -> "El nombre es obligatorio"
            estado.nombre.trim().length < 3 -> "Ingresa tu nombre completo"
            estado.nombre.length > 30 -> "El nombre no puede superar los 30 caracteres"
            !estado.nombre.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) -> "El nombre solo puede contener letras y espacios"
            else -> null
        }
        val errorCorreo = if (!Patterns.EMAIL_ADDRESS.matcher(estado.correo).matches()) {
            "Ingrese un correo válido"
        } else null
        val errorTelefono = when {
            estado.telefono.length < 9 -> "El teléfono debe tener 9 dígitos"
            else -> null
        }
        val errorTerminos = if (!estado.aceptarTerminos) "Debes aceptar los términos" else null

        val siHayErrores = listOf(
            errorRut,
            errorNombre,
            errorCorreo,
            errorTelefono,
            errorTerminos,
        ).any { it != null }

        if (siHayErrores) {
            _uiState.update {
                it.copy(
                    errorFormatoRut = errorRut,
                    errorNombre = errorNombre,
                    errorFormatoCorreo = errorCorreo,
                    errorTelefono = errorTelefono,
                    errorTerminos = errorTerminos,
                    registroExitoso = false
                )
            }
            return
        }

        val rutFormateado = autoCompletadoRut(estado.rut)

        viewModelScope.launch {
            try {
                usuarioRepository.registrar(
                    rut = rutFormateado,
                    nombre = estado.nombre.trim(),
                    correo = estado.correo.trim(),
                    telefono = estado.telefono,
                    contrasena = "",
                    cargo = estado.rolSeleccionado.name,
                    turno = "Turno Diurno",
                    rolId = estado.rolSeleccionado.id
                )

                _uiState.update {
                    it.copy(
                        errorFormatoRut = null,
                        errorNombre = null,
                        errorFormatoCorreo = null,
                        errorTelefono = null,
                        errorTerminos = null,
                        contrasenaTemporalGenerada = "",
                        registroExitoso = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorFormatoRut = e.message ?: "No se pudo registrar el colaborador",
                        contrasenaTemporalGenerada = "",
                        registroExitoso = false
                    )
                }
            }
        }
    }

    fun validarFormatoRut(rut: String): Boolean {
        val limpiarRut = rut.replace(".", "")
            .replace("-", "")
            .trim()
            .uppercase()

        if (limpiarRut.length < 8) return false

        val cuerpoRut = limpiarRut.substring(0, limpiarRut.length - 1)
        val dvRut = limpiarRut.last()
        cuerpoRut.toIntOrNull() ?: return false

        var suma = 0
        var multiplicador = 2

        for (i in cuerpoRut.length - 1 downTo 0) {
            suma += (cuerpoRut[i].toString().toInt()) * multiplicador
            multiplicador = if (multiplicador < 7) multiplicador + 1 else 2
        }

        val resto = suma % 11
        val dvRutCalculado = when (val resultado = 11 - resto) {
            11 -> '0'
            10 -> 'K'
            else -> resultado.toString()[0]
        }
        return dvRut == dvRutCalculado
    }

    fun autoCompletadoRut(rut: String): String {
        val limpiar = rut.replace("[^0-9kK]".toRegex(), "").uppercase().take(9)
        if (limpiar.isEmpty()) return ""

        val dv = limpiar.last()
        val cuerpo = limpiar.dropLast(1)

        if (cuerpo.isEmpty()) return dv.toString()
        val cuerpoFormateado = cuerpo.reversed().chunked(3).joinToString(".").reversed()
        return "$cuerpoFormateado-$dv"
    }
}