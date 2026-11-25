package com.example.tiendaretailsur.data

import com.example.tiendaretailsur.data.remote.ConteoRequest
import com.example.tiendaretailsur.data.remote.ConteoResponse
import com.example.tiendaretailsur.data.remote.ConteosApi
import com.example.tiendaretailsur.data.remote.DatoGraficoResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeConteosApi(
    var conteosSimulados: List<ConteoResponse> = emptyList(),
    var simularError: Boolean = false
) : ConteosApi {

    override suspend fun listarConteos(): Response<List<ConteoResponse>> {
        if (simularError) return Response.error(500, "Error".toResponseBody())
        return Response.success(conteosSimulados)
    }

    override suspend fun registrarConteo(datos: ConteoRequest): Response<ConteoResponse> {
        if (simularError) return Response.error(400, "Error".toResponseBody())
        return Response.success(
            ConteoResponse(
                id = 1L,
                puertaAcceso = datos.puertaAcceso,
                cantidadTransito = datos.cantidadTransito,
                cantidadClientes = datos.cantidadClientes,
                rangoEtario = datos.rangoEtario,
                compania = datos.compania,
                observador = datos.observador,
                hora = datos.hora,
                usuarioId = datos.usuarioId
            )
        )
    }

    override suspend fun obtenerFlujoPorHora(): Response<List<DatoGraficoResponse>> =
        Response.success(emptyList())

    override suspend fun obtenerSegmentancion(): Response<List<DatoGraficoResponse>> =
        Response.success(emptyList())

    override suspend fun obtenerFlujoPorTurno(): Response<List<DatoGraficoResponse>> =
        Response.success(emptyList())
}