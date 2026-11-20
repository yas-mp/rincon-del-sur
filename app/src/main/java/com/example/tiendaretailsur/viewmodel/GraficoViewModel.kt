package com.example.tiendaretailsur.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.ConteoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GraficosViewModel(
    private val conteoRepository: ConteoRepository = ConteoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GraficosUiState())
    val uiState: StateFlow<GraficosUiState> = _uiState.asStateFlow()

    init {
        cargarDatosGrafico()
    }

    fun cargarDatosGrafico() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }
            try {
                val flujoHora = conteoRepository.obtenerFlujoPorHora()
                val segmentacionData = conteoRepository.obtenerSegmentacion()

                _uiState.update {
                    it.copy(
                        flujoPorHora = flujoHora,
                        segmentacion = segmentacionData,
                        estaCargando = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(estaCargando = false) }
            }
        }
    }

    fun recargarDatos() {
        cargarDatosGrafico()
    }
}
