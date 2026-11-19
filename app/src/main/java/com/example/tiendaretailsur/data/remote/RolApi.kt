package com.example.tiendaretailsur.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface RolApi {

    @GET("api/roles")
    suspend fun listar(): Response<List<RolResponseDTO>>

    @GET("api/roles/{id}")
    suspend fun obtenerRol(
        @Path("id") id: Long
    ): Response<RolResponseDTO>

    @PUT("api/roles/{id}")
    suspend fun actualizarOl(
        @Path("id") id: Long,
        @Body requestDTO: RolRequestDTO
    ): Response<RolResponseDTO>
}