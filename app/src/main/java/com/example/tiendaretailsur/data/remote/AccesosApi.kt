package com.example.tiendaretailsur.data.remote


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AccesosApi {
    @GET("api/accesos")
    suspend fun listarAccesos(): Response<List<AccesosResponseDTO>>

    @GET("api/accesos/habilitados")
    suspend fun listarAccesosHabilitados() : Response<List<AccesosResponseDTO>>

    @GET("api/accesos/{id}")
    suspend fun accesosPorId(
        @Path("id") id: Long
    ): Response<AccesosResponseDTO>

    @POST("api/accesos/")
    suspend fun crearAcceso(
        @Body requestDTO: AccesosRequestDTO
    ): Response<AccesosResponseDTO>

    @PUT("api/accesos/{id}")
    suspend fun cambiarAccesoPorId(
        @Path("id") id : Long,
        @Body requestDTO: AccesosRequestDTO
    ): Response<AccesosResponseDTO>

    @PATCH("api/accesos/{id}/estado")
    suspend fun modificarEstadoPorId(
        @Path("id") id : Long,
        @Query("habilitado") habilitado : Boolean
    ): Response<AccesosResponseDTO>

}