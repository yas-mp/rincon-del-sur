package com.example.tiendaretailsur.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ConteosApi {
    @GET("api/conteos")
    suspend fun listarConteos(): Response<List<ConteoResponse>>

    @POST("/api/conteos")
    suspend fun registrarConteo(
        @Body datos : ConteoRequest
    ): Response<ConteoResponse>

    @GET("api/conteos/grafico-hora")
    suspend fun obtenerFlujoPorHora(): Response<List<DatoGraficoResponse>>

    @GET("api/conteos/grafico-segmentacion")
    suspend fun obtenerSegmentancion() : Response<List<DatoGraficoResponse>>

    @GET("api/conteos/gracico-turno")
    suspend fun obtenerFlujoPorTurno() : Response<List<DatoGraficoResponse>>
}