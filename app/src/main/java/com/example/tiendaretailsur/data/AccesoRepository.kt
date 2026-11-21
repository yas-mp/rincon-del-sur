package com.example.tiendaretailsur.data

import com.example.tiendaretailsur.data.remote.AccesosApi
import com.example.tiendaretailsur.data.remote.AccesosRequestDTO
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
    suspend fun crear(codigo: String, nombre: String, descripcion: String, habilitado: Boolean): Boolean = try {
        val response = accesosApi.crearAcceso(
            AccesosRequestDTO(
                codigo = codigo,
                nombre = nombre,
                descripcion = descripcion,
                estaHabilitado = habilitado
            )
        )
        response.isSuccessful
    } catch (e: Exception) {
        false
    }

    suspend fun actualizar(idAcceso: Long, codigo: String, nombre: String, descripcion: String, habilitado: Boolean): Boolean = try {
        val response = accesosApi.cambiarAccesoPorId(
            id = idAcceso,
            requestDTO = AccesosRequestDTO(
                codigo = codigo,
                nombre = nombre,
                descripcion = descripcion,
                estaHabilitado = habilitado
            )
        )
        response.isSuccessful
    } catch (e: Exception) {
        false
    }
}

private fun AccesosResponseDTO.aAcceso(): AccesoTienda = AccesoTienda(
    id = id.toString(),
    nombre = nombre,
    descripcion = descripcion,
    estaHabilitado = estaHabilitado
)
