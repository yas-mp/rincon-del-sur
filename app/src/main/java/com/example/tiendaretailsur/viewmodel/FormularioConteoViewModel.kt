package com.example.tiendaretailsur.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tiendaretailsur.data.UsuarioDbHelper
import com.example.tiendaretailsur.model.AccesoTienda
import com.example.tiendaretailsur.model.FormularioConteoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormularioConteoViewModel : AndroidViewModel {
    private val dbHelper: UsuarioDbHelper?
    private var ultimoIncrementoTransito: Int = 0
    private var ultimoIncrementoCliente: Int = 0

    constructor() : super(Application()) {
        dbHelper = null
    }

    constructor(application: Application) : super(application) {
        dbHelper = UsuarioDbHelper(application)
        cargarPuertasHabilitadas()
    }

    private val _uiState = MutableStateFlow(FormularioConteoUiState())
    val uiState: StateFlow<FormularioConteoUiState> = _uiState.asStateFlow()

    fun cargarPuertasHabilitadas() {
        val db = dbHelper ?: return
        val accesosHabilitados = try {
            db.listarPuertas().filter { it.estaHabilitado }
        } catch (e: Exception) {
            emptyList()
        }

        _uiState.update { estado ->
            val puertaValida = if (accesosHabilitados.any {
                    it.nombre.equals(estado.puertaAcceso, ignoreCase = true) ||
                            it.id.equals(estado.puertaAcceso, ignoreCase = true)
                }
            ) {
                estado.puertaAcceso
            } else {
                accesosHabilitados.firstOrNull()?.id ?: accesosHabilitados.firstOrNull()?.nombre ?: ""
            }

            estado.copy(puertaAcceso = puertaValida)
        }
    }

    fun obtenerPuertasHabilitadas(): List<AccesoTienda> {
        return try {
            dbHelper?.listarPuertas()?.filter { it.estaHabilitado } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun estaPuertaActualBloqueada(): Boolean {
        val db = dbHelper ?: return false
        val puertaActual = _uiState.value.puertaAcceso
        if (puertaActual.isBlank()) return false

        val puertas = try { db.listarPuertas() } catch (e: Exception) { emptyList() }
        val puertaEncontrada = puertas.find {
            it.id.equals(puertaActual, ignoreCase = true) || it.nombre.equals(puertaActual, ignoreCase = true)
        }

        return puertaEncontrada != null && !puertaEncontrada.estaHabilitado
    }

    fun setPuerta(puerta: String) {
        ultimoIncrementoTransito = 0
        ultimoIncrementoCliente = 0
        _uiState.update {
            it.copy(
                puertaAcceso = puerta,
                envioExitoso = false,
                mensajeFeedback = null,
                puedeRestarTransito = false,
                puedeRestarCliente = false
            )
        }
    }

    fun incrementarTransito(cantidad: Int = 1) {
        if (estaPuertaActualBloqueada()) return
        ultimoIncrementoTransito = cantidad
        _uiState.update {
            it.copy(
                cantidadTransito = it.cantidadTransito + cantidad,
                envioExitoso = false,
                mensajeFeedback = "+$cantidad en Tránsito registrado",
                puedeRestarTransito = true
            )
        }
    }

    fun decrementarTransito() {
        if (estaPuertaActualBloqueada()) return
        if (ultimoIncrementoTransito <= 0 || _uiState.value.cantidadTransito <= 0) {
            _uiState.update {
                it.copy(mensajeFeedback = "Solo puedes anular el último registro de Tránsito")
            }
            return
        }
        val aRestar = ultimoIncrementoTransito
        ultimoIncrementoTransito = 0
        _uiState.update {
            it.copy(
                cantidadTransito = maxOf(0, it.cantidadTransito - aRestar),
                envioExitoso = false,
                mensajeFeedback = "Último registro de Tránsito (-$aRestar) anulado",
                puedeRestarTransito = false
            )
        }
    }

    fun incrementarClientes(cantidad: Int = 1) {
        if (estaPuertaActualBloqueada()) return
        val estado = _uiState.value

        if (estado.rangoEtario.isBlank() || estado.compania.isBlank()) {
            _uiState.update {
                it.copy(
                    mensajeFeedback = "Selecciona Rango Etario y Tipo de Compañía antes de sumar"
                )
            }
            return
        }

        ultimoIncrementoCliente = cantidad
        val detalle = "${estado.rangoEtario} · ${estado.compania}"
        _uiState.update {
            it.copy(
                cantidadClientes = it.cantidadClientes + cantidad,
                rangoEtario = "",
                compania = "",
                envioExitoso = false,
                mensajeFeedback = "+$cantidad Cliente ($detalle) registrado",
                puedeRestarCliente = true
            )
        }
    }

    fun decrementarClientes() {
        if (estaPuertaActualBloqueada()) return
        if (ultimoIncrementoCliente <= 0 || _uiState.value.cantidadClientes <= 0) {
            _uiState.update {
                it.copy(mensajeFeedback = "Solo puedes anular el último registro de Cliente")
            }
            return
        }
        val aRestar = ultimoIncrementoCliente
        ultimoIncrementoCliente = 0
        _uiState.update {
            it.copy(
                cantidadClientes = maxOf(0, it.cantidadClientes - aRestar),
                envioExitoso = false,
                mensajeFeedback = "Último registro de Cliente (-$aRestar) anulado",
                puedeRestarCliente = false
            )
        }
    }

    fun seleccionarRangoEtario(rango: String) {
        if (estaPuertaActualBloqueada()) return
        _uiState.update {
            it.copy(
                rangoEtario = if (it.rangoEtario == rango) "" else rango,
                envioExitoso = false
            )
        }
    }

    fun seleccionarCompania(comp: String) {
        if (estaPuertaActualBloqueada()) return
        _uiState.update {
            it.copy(
                compania = if (it.compania == comp) "" else comp,
                envioExitoso = false
            )
        }
    }

    fun enviarFormulario(nombreObservador: String) {
        if (estaPuertaActualBloqueada()) {
            _uiState.update { it.copy(envioExitoso = false) }
            cargarPuertasHabilitadas()
            return
        }

        val estado = _uiState.value
        if (estado.cantidadTransito == 0 && estado.cantidadClientes == 0) {
            _uiState.update {
                it.copy(mensajeFeedback = "Registra al menos 1 persona antes de finalizar la sesión")
            }
            return
        }

        val db = dbHelper ?: return
        val observadorReal = nombreObservador.ifBlank { "Observador en turno" }
        val horaActual = SimpleDateFormat("HH'h'", Locale.getDefault()).format(Date())

        val estadoAGuardar = estado.copy(
            rangoEtario = estado.rangoEtario.ifBlank { "Mixto" },
            compania = estado.compania.ifBlank { "Varios" }
        )

        db.registrarFormularioConteo(estadoAGuardar, observadorReal, horaActual)
        ultimoIncrementoTransito = 0
        ultimoIncrementoCliente = 0

        _uiState.update {
            FormularioConteoUiState(
                puertaAcceso = it.puertaAcceso,
                envioExitoso = true,
                mensajeFeedback = "Sesión guardada en el historial de $observadorReal"
            )
        }
    }

    fun obtenerHistorialSesiones(nombreObservador: String, verTodos: Boolean): List<FormularioConteoUiState> {
        val db = dbHelper ?: return emptyList()
        return try {
            if (verTodos || nombreObservador.isBlank()) {
                db.listarConteos()
            } else {
                db.listarConteosPorObservador(nombreObservador)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun limpiarMensajeFeedback() {
        _uiState.update { it.copy(mensajeFeedback = null) }
    }

    fun limpiarExitoso() {
        _uiState.update { it.copy(envioExitoso = false) }
    }
}