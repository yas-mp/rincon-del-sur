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
    private val boletasEscaneadas = mutableSetOf<String>()

    init {
        cargarPuertasHabilitadas()
    }

    fun cargarPuertasHabilitadas() {
        viewModelScope.launch {
            try {
                val accesos = accesoRepository.listar()
                _puertasHabilitadas = accesos.filter { it.estaHabilitado }

                _uiState.update { estado ->
                    val puertaValida = if (_puertasHabilitadas.any {
                            it.nombre.equals(estado.puertaAcceso, ignoreCase = true) ||
                                    it.id.equals(estado.puertaAcceso, ignoreCase = true)
                        }) {
                        estado.puertaAcceso
                    } else {
                        _puertasHabilitadas.firstOrNull()?.id
                            ?: _puertasHabilitadas.firstOrNull()?.nombre
                            ?: ""
                    }
                    estado.copy(puertaAcceso = puertaValida)
                }
            } catch (_: Exception) {
                _puertasHabilitadas = emptyList()
            }
        }
    }

    fun obtenerPuertasHabilitadas(): List<AccesoTienda> = _puertasHabilitadas

    fun estaPuertaActualBloqueada(): Boolean {
        val puertaActual = _uiState.value.puertaAcceso
        if (puertaActual.isBlank()) return false

        val puertaEncontrada = _puertasHabilitadas.find {
            it.id.equals(puertaActual, ignoreCase = true) ||
                    it.nombre.equals(puertaActual, ignoreCase = true)
        }
        return puertaEncontrada != null && !puertaEncontrada.estaHabilitado
    }

    fun setPuerta(puerta: String) {
        _uiState.update {
            it.copy(
                puertaAcceso = puerta,
                envioExitoso = false,
                mensajeFeedback = null
            )
        }
    }

    fun seleccionarRangoEtario(rango: String) {
        if (estaPuertaActualBloqueada()) return
        _uiState.update {
            it.copy(
                rangoEtario = rango,
                envioExitoso = false,
                mensajeFeedback = null
            )
        }
    }

    fun seleccionarCompania(comp: String) {
        if (estaPuertaActualBloqueada()) return
        _uiState.update {
            it.copy(
                compania = comp,
                envioExitoso = false,
                mensajeFeedback = null
            )
        }
    }

    fun puedeRegistrarSalida(): Boolean {
        val estado = _uiState.value
        return !estaPuertaActualBloqueada() &&
                estado.rangoEtario.isNotBlank() &&
                estado.compania.isNotBlank()
    }

    private fun esFormatoBoletaValido(codigo: String): Boolean {
        return codigo.matches(Regex("^RDS-BOL-2026-\\d{6}$"))
    }

    fun procesarEscaneoBoleta(codigoLeido: String) {
        if (!puedeRegistrarSalida()) return

        val codigoLimpio = codigoLeido.trim().uppercase()

        if (!esFormatoBoletaValido(codigoLimpio)) {
            _uiState.update {
                it.copy(
                    cantidadTransito = it.cantidadTransito + 1,
                    rangoEtario = "",
                    compania = "",
                    envioExitoso = false,
                    mensajeFeedback = "Formato de boleta no válido -> +1 Tránsito"
                )
            }
            return
        }

        if (boletasEscaneadas.contains(codigoLimpio)) {
            _uiState.update {
                it.copy(
                    rangoEtario = "",
                    compania = "",
                    envioExitoso = false,
                    mensajeFeedback = "La boleta $codigoLimpio ya fue escaneada"
                )
            }
            return
        }

        boletasEscaneadas.add(codigoLimpio)
        _uiState.update {
            it.copy(
                cantidadClientes = it.cantidadClientes + 1,
                rangoEtario = "",
                compania = "",
                envioExitoso = false,
                mensajeFeedback = "Boleta válida ($codigoLimpio) -> +1 Cliente"
            )
        }
    }

    fun registrarSinBoleta() {
        if (!puedeRegistrarSalida()) return

        _uiState.update {
            it.copy(
                cantidadTransito = it.cantidadTransito + 1,
                rangoEtario = "",
                compania = "",
                envioExitoso = false,
                mensajeFeedback = "Salida sin boleta (+1 Tránsito)"
            )
        }
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

                var nombreObservador = correo.ifBlank { "Usuario Sistema" }
                if (idUsuario != -1L) {
                    try {
                        val usuario = usuarioRepository.obtener(idUsuario)
                        if (usuario.nombreCompleto.isNotBlank()) {
                            nombreObservador = usuario.nombreCompleto
                        }
                    } catch (_: Exception) {

                    }
                }

                val exito = conteoRepository.registrar(
                    uiState = estado,
                    observador = nombreObservador,
                    hora = "12h",
                    usuarioId = if (idUsuario != -1L) idUsuario else null
                )
                if (exito) {
                    _uiState.update {
                        FormularioConteoUiState(
                            puertaAcceso = it.puertaAcceso,
                            envioExitoso = true,
                            mensajeFeedback = "Registro de aforo enviado correctamente"
                        )
                    }
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(mensajeFeedback = "Error al enviar el registro")
                }
            }
        }
    }

    fun limpiarExitoso() {
        _uiState.update {
            it.copy(
                envioExitoso = false,
                mensajeFeedback = null
            )
        }
    }
}