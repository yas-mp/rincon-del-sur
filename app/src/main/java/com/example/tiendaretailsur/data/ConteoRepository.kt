package com.example.tiendaretailsur.data

import com.example.tiendaretailsur.data.remote.ConteoRequest
import com.example.tiendaretailsur.data.remote.ConteoResponse
import com.example.tiendaretailsur.data.remote.ConteosApi
import com.example.tiendaretailsur.data.remote.RetrofitClient
import com.example.tiendaretailsur.model.DatoGrafico
import com.example.tiendaretailsur.model.FormularioConteoUiState

class ConteoRepository (
    private val conteosApi: ConteosApi = RetrofitClient.conteosApi
) {
    suspend fun listarConteos(): List<ConteoResponse> = try {
        val response = conteosApi.listarConteos()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun registrar(
        uiState: FormularioConteoUiState,
        observador: String,
        hora: String,
        usuarioId: Long?
    ): Boolean = try {
        val request = ConteoRequest(
            puertaAcceso = uiState.puertaAcceso,
            cantidadTransito = uiState.cantidadTransito,
            cantidadClientes = uiState.cantidadClientes,
            rangoEtario = uiState.rangoEtario,
            compania = uiState.compania,
            observador = observador,
            hora = hora,
            usuarioId = usuarioId
        )
        val response = conteosApi.registrarConteo(request)
        response.isSuccessful
    } catch (e: Exception) {
        false
    }

    suspend fun obtenerFlujoPorHora(): List<DatoGrafico> = try {
        val response = conteosApi.obtenerFlujoPorHora()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.map { DatoGrafico(it.etiqueta, it.valor) }
        } else emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun obtenerSegmentacion(): List<DatoGrafico> = try {
        val response = conteosApi.obtenerSegmentancion()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.map { DatoGrafico(it.etiqueta, it.valor) }
        } else emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun obtenerFlujoPorTurno(): List<DatoGrafico> = try {
        val response = conteosApi.obtenerFlujoPorTurno()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.map { DatoGrafico(it.etiqueta, it.valor) }
        } else emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}