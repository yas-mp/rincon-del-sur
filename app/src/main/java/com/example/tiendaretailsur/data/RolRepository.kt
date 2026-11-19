package com.example.tiendaretailsur.data

import com.example.tiendaretailsur.data.remote.RetrofitClient
import com.example.tiendaretailsur.data.remote.RolApi
import com.example.tiendaretailsur.data.remote.RolRequestDTO
import com.example.tiendaretailsur.data.remote.RolResponseDTO
import com.example.tiendaretailsur.model.RolUsuario

class RolRepository (
    private val rolApi: RolApi = RetrofitClient.rolApi
) {
    suspend fun listar(): List<RolUsuario> = try {
        val response = rolApi.listar()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.map { dto ->
                RolUsuario.entries.find { r -> r.id == dto.id } ?: RolUsuario.OBSERVADOR
            }
        } else {
            RolUsuario.entries.toList()
        }
    } catch (e: Exception) {
        RolUsuario.entries.toList()
    }

    suspend fun obtenerRol(id: Long): RolResponseDTO? = try {
        val response = rolApi.obtenerRol(id)
        if (response.isSuccessful) response.body() else null
    } catch (e: Exception) {
        null
    }

    suspend fun actualizarRol(id: Long, nombre: String, descripcion: String, habilitado: Boolean): RolResponseDTO? = try {
        val response = rolApi.actualizarOl(id, RolRequestDTO(nombre, descripcion, habilitado))
        if (response.isSuccessful) response.body() else null
    } catch (e: Exception) {
        null
    }
}