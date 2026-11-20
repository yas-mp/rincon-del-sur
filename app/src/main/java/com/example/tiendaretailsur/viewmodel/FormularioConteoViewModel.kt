package com.example.tiendaretailsur.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.AccesoRepository
import com.example.tiendaretailsur.data.ConteoRepository
import com.example.tiendaretailsur.data.GestorSesion
import com.example.tiendaretailsur.data.UsuarioRepository
import com.example.tiendaretailsur.model.AccesoTienda
import com.example.tiendaretailsur.model.FormularioConteoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FormularioConteoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val conteoRepository = ConteoRepository()
    private val accesoRepository = AccesoRepository()
    private val usuarioRepository = UsuarioRepository()
    private val gestorSesion = GestorSesion(application)

    private val _uiState = MutableStateFlow(FormularioConteoUiState())
    val uiState: StateFlow<FormularioConteoUiState> = _uiState.asStateFlow()

    private var _puertasHabilitadas = listOf<AccesoTienda>()

    init {
        cargarPuertasHabilitadas()
    }

    fun cargarPuertasHabilitadas() {
        viewModelScope.launch {
            try {
                val accesos = accesoRepository.listar()
                _puertasHabilitadas = accesos.filter { it.estaHabilitado }

                _uiState.update { estado ->
                    val puertaValida = if (_puertasHabilitadas.any { it.nombre.equals(estado.puertaAcceso, ignoreCase = true) || it.id.equals(estado.puertaAcceso, ignoreCase = true) }) {
                        estado.puertaAcceso
                    } else {
                        _puertasHabilitadas.firstOrNull()?.id ?: _puertasHabilitadas.firstOrNull()?.nombre ?: ""
                    }
                    estado.copy(puertaAcceso = puertaValida)
                }
            } catch (_: Exception) {
                _puertasHabilitadas = emptyList()
            }
        }
    }

    fun obtenerPuertasHabilitadas(): List<AccesoTienda> {
        return _puertasHabilitadas
    }

    fun estaPuertaActualBloqueada(): Boolean {
        val puertaActual = _uiState.value.puertaAcceso
        if (puertaActual.isBlank()) return false

        val puertaEncontrada = _puertasHabilitadas.find {
            it.id.equals(puertaActual, ignoreCase = true) || it.nombre.equals(puertaActual, ignoreCase = true)
        }
        return puertaEncontrada != null && !puertaEncontrada.estaHabilitado
    }

    fun setPuerta(puerta: String) {
        _uiState.update { it.copy(puertaAcceso = puerta, envioExitoso = false) }
    }

    fun incrementarTransito() {
        if (estaPuertaActualBloqueada()) return
        _uiState.update { it.copy(cantidadTransito = it.cantidadTransito + 1, envioExitoso = false) }
    }

    fun decrementarTransito() {
        if (estaPuertaActualBloqueada()) return
        _uiState.update { it.copy(cantidadTransito = maxOf(0, it.cantidadTransito - 1), envioExitoso = false) }
    }

    fun incrementarClientes() {
        if (estaPuertaActualBloqueada()) return
        _uiState.update { it.copy(cantidadClientes = it.cantidadClientes + 1, envioExitoso = false) }
    }

    fun decrementarClientes() {
        if (estaPuertaActualBloqueada()) return
        _uiState.update { it.copy(cantidadClientes = maxOf(0, it.cantidadClientes - 1), envioExitoso = false) }
    }

    fun seleccionarRangoEtario(rango: String) {
        if (estaPuertaActualBloqueada()) return
        _uiState.update { it.copy(rangoEtario = rango, envioExitoso = false) }
    }

    fun seleccionarCompania(comp: String) {
        if (estaPuertaActualBloqueada()) return
        _uiState.update { it.copy(compania = comp, envioExitoso = false) }
    }

    fun enviarFormulario() {
        if (estaPuertaActualBloqueada()) {
            _uiState.update { it.copy(envioExitoso = false) }
            cargarPuertasHabilitadas()
            return
        }

        val estado = _uiState.value

        viewModelScope.launch {
            try {
                val idUsuario = gestorSesion.obtenerIdUsuarioLogueado()
                val correo = gestorSesion.obtenerCorreoUsuarioLogueado()

                // Obtener nombre del observador real desde el repositorio usando la sesión
                var nombreObservador = correo.ifBlank { "Usuario Sistema" }
                if (idUsuario != -1L) {
                    try {
                        val usuario = usuarioRepository.obtener(idUsuario)
                        if (usuario.nombreCompleto.isNotBlank()) {
                            nombreObservador = usuario.nombreCompleto
                        }
                    } catch (_: Exception) {
                        // Fallback
                    }
                }

                val exito = conteoRepository.registrar(
                    uiState = estado,
                    observador = nombreObservador,
                    hora = "12h",
                    usuarioId = if (idUsuario != -1L) idUsuario else null
                )
                if (exito) {
                    _uiState.update { FormularioConteoUiState(puertaAcceso = it.puertaAcceso, envioExitoso = true) }
                }
            } catch (_: Exception) {

            }
        }
    }

    fun limpiarExitoso() {
        _uiState.update { it.copy(envioExitoso = false) }
    }
}
