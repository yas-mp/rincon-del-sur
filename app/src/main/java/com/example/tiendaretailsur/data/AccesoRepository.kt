package com.example.tiendaretailsur.data

import com.example.tiendaretailsur.data.remote.AccesosApi
import com.example.tiendaretailsur.data.remote.AccesosResponseDTO
import com.example.tiendaretailsur.data.remote.RetrofitClient
import com.example.tiendaretailsur.model.AccesoTienda

class AccesoRepository(
    private val accesosApi: AccesosApi = RetrofitClient.accesosApi
) {

    suspend fun listar(): List<AccesoTienda> = try {
        val response = accesosApi.listarAccesos()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.map { it.aAcceso() }
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun cambiarEstado(idAcceso: Long, habilitado: Boolean): Boolean = try {
        val response = accesosApi.modificarEstadoPorId(idAcceso, habilitado)
        response.isSuccessful
    } catch (e: Exception) {
        false
    }
}

//  para mapear el DTO
private fun AccesosResponseDTO.aAcceso(): AccesoTienda = AccesoTienda(
    id = id.toString(),
    nombre = nombre,
    descripcion = descripcion,
    estaHabilitado = estaHabilitado
)
