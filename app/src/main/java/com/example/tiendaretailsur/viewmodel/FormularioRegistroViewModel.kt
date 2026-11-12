package com.example.tiendaretailsur.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.tiendaretailsur.data.UsuarioDbHelper
import com.example.tiendaretailsur.model.FormularioRegistroUiState
import com.example.tiendaretailsur.model.RolUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FormularioRegistroViewModel(
    private val dbHelper: UsuarioDbHelper
) : ViewModel() {
    private val _uiState = MutableStateFlow(FormularioRegistroUiState())
    val uiState: StateFlow<FormularioRegistroUiState> = _uiState.asStateFlow()

    private fun generarContrasena(): String {
        val mayusculas = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val minusculas = "abcdefghijkmnopqrstuvwxyz"
        val numeros = "23456789"
        return buildString {
            append(mayusculas.random())
            append(minusculas.random())
            repeat(4) { append(numeros.random()) }
            append(mayusculas.random())
            append(minusculas.random())
        }
    }

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

        var errorRut = if (!validarFormatoRut(estado.rut)) "El RUT no tiene un formato válido" else null
        val errorNombre = when {
            estado.nombre.isBlank() -> "El nombre es obligatorio"
            estado.nombre.trim().length < 3 -> "Ingresa tu nombre completo"
            estado.nombre.length > 30 -> "El nombre no puede superar los 30 caracteres"
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

        var guardar = false
        var claveGenerada = ""

        if (!siHayErrores) {
            claveGenerada = generarContrasena()
            val estadoParaGuardar = estado.copy(rut = autoCompletadoRut(estado.rut))
            val resultado = dbHelper.registroUsuarioFormularioRegistro(estadoParaGuardar, claveGenerada)
            if (resultado != -1L) {
                guardar = true
            } else {
                errorRut = "El RUT ya se encuentra registrado"
                claveGenerada = ""
            }
        }

        _uiState.update {
            it.copy(
                errorFormatoRut = errorRut,
                errorNombre = errorNombre,
                errorFormatoCorreo = errorCorreo,
                errorTelefono = errorTelefono,
                errorTerminos = errorTerminos,
                contrasenaTemporalGenerada = claveGenerada,
                registroExitoso = guardar
            )
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