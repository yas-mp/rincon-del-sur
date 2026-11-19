package com.example.tiendaretailsur.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuariosApi {

    @GET("api/usuarios")
    suspend fun listar(
        @Query("buscar") buscar: String? = null
    ): Response<List<UsuarioResponse>>

    @GET("api/usuarios/{id}")
    suspend fun obtener(
        @Path("id") id: Long
    ): Response<UsuarioResponse>

    @POST("api/usuarios")
    suspend fun registrar(
        @Body datos: RegistroRequest
    ): Response<UsuarioResponse>

    @POST("api/usuarios/login")
    suspend fun iniciarSesion(
        @Body datos: LoginRequest
    ): Response<UsuarioResponse>

    @PUT("api/usuarios/{id}/primer-ingreso")
    suspend fun completarPrimerIngreso(
        @Path("id") id: Long,
        @Body datos: PrimerIngresoRequest
    ): Response<UsuarioResponse>

    @POST("api/usuarios/recuperar-pregunta")
    suspend fun obtenerPreguntaSeguridad(
        @Body datos: RecuperarPreguntaRequest
    ): Response<PreguntaResponse>

    @POST("api/usuarios/verificar-respuesta")
    suspend fun verificarRespuesta(
        @Body datos: VerificarRespuestaRequest
    ): Response<VerificacionResponse>

    @PUT("api/usuarios/restablecer-contrasena")
    suspend fun restablecerContrasena(
        @Body datos: RestablecerContrasenaRequest
    ): Response<Unit>

    @PUT("api/usuarios/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body datos: ActualizarUsuarioRequest
    ): Response<UsuarioResponse>

    @PUT("api/usuarios/{id}/contrasena")
    suspend fun cambiarContrasena(
        @Path("id") id: Long,
        @Body datos: CambiarContrasenaRequest
    ): Response<Unit>

    @DELETE("api/usuarios/{id}")
    suspend fun eliminar(
        @Path("id") id: Long
    ): Response<Unit>
}