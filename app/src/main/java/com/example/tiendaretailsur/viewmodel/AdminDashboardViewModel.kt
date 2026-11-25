package com.example.tiendaretailsur.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.ConteoRepository
import com.example.tiendaretailsur.model.AdminDashboardUiState
import com.example.tiendaretailsur.model.DatoGrafico
import com.example.tiendaretailsur.model.SesionResumen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminDashboardViewModel(
    private val conteoRepository: ConteoRepository = ConteoRepository()
) : ViewModel(){

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        cargarDatosDashboard()
    }

    private fun extraerClaveHora(horaRaw: String): Pair<Int, String> {
        val limpia = horaRaw.trim()
        if (limpia.isBlank()) return 99 to "Sin hora"

        val digitos = limpia.takeWhile { it.isDigit() }
        val horaNum = digitos.toIntOrNull()

        return if (horaNum != null && horaNum in 0..23) {
            val etiqueta = "${horaNum.toString().padStart(2, '0')}:00"
            horaNum to etiqueta
        } else {
            99 to limpia
        }
    }

    fun cargarDatosDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }

            val listaConteos = try {
                conteoRepository.listarConteos()
            } catch (_: Exception) {
                emptyList()
            }

            val listaSesiones = listaConteos.mapIndexed { index, conteo ->
                SesionResumen(
                    id = (index + 1).toLong(),
                    observador = conteo.observador,
                    hora = conteo.hora,
                    conteo = conteo.cantidadClientes + conteo.cantidadTransito,
                    estado = 1
                )
            }

            val flujoPorHora = listaConteos
                .groupBy { extraerClaveHora(it.hora) }
                .toSortedMap(compareBy { it.first })
                .map { (_, conteosHora) ->
                    val etiqueta = extraerClaveHora(conteosHora.first().hora).second
                    val totalHora = conteosHora.sumOf { it.cantidadClientes + it.cantidadTransito }
                    DatoGrafico(etiqueta = etiqueta, valor = totalHora)
                }

            val totalClientes = listaConteos.sumOf { it.cantidadClientes }
            val totalTransito = listaConteos.sumOf { it.cantidadTransito }
            val segmentacion = if (totalClientes > 0 || totalTransito > 0) {
                listOf(
                    DatoGrafico(etiqueta = "Clientes", valor = totalClientes),
                    DatoGrafico(etiqueta = "Tránsito", valor = totalTransito)
                )
            } else {
                emptyList()
            }

            _uiState.update { estadoActual ->
                estadoActual.copy(
                    totalVisitas = listaSesiones.sumOf { it.conteo },
                    observadoresActivos = listaConteos.map { it.observador }.distinct().size,
                    listaSesiones = listaSesiones,
                    flujoPorHora = flujoPorHora,
                    segmentacion = segmentacion,
                    estaCargando = false
                )
            }
        }
    }
}